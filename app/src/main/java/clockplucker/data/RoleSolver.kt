package clockplucker.data

//    Copyright 2026 Derek Rodriguez
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

import clockplucker.SelectedPriorities
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solver
import org.chocosolver.solver.constraints.extension.Tuples
import org.chocosolver.solver.search.strategy.Search
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.toTypedArray
import kotlin.random.Random

// todo: an assigned Summoner will never allow a Marionette to generate,
//      since they prevent a demon from generating. Technically, this is
//      wrong; the jinx specifies that a Marionette can be in a Summoner
//      game, as a good player neighboring the summoned demon is made
//      the Marionette upon the demon's summoning. Not sure the best way
//      to make this exception without it being horribly hacky.
//      idea: in the viewmodel, upon loading the script, if it contains
//      both a Marionette and Summoner, edit the Marionette's id to
//      something like marionette-summoner to break the RoleSolver
//      neighboring constraint without affecting any other logic.
//      Handle the reassignment in GrimRevealScreen like lilmonsta.
//      Make id "marionette-late_entry" because the lilmonsta affects
//      it the same way.

class RoleSolver (
    private val players: List<Player>,
    private val availableChars: List<Character>,
    private val baseCount: Count,
    private val surpriseChances: Map<Character, Float> = emptyMap(),
    private val selectedPriority: SelectedPriorities = SelectedPriorities.NO_PRIORITIES,
    private val playerPriorityToggle: Boolean = false,
    private val containsPope: Boolean = false,
    private val autoSentinel: Boolean = false
){
    data class SolverProgress(
        val solutionsFound: Int,
        val bestScore: Int,
        val nodesExplored: Long,
        val timeElapsed: Float
    )

    private var internalSolver: Solver? = null
    private val stopRequested = AtomicBoolean(false)

    /**
     * Polling method to get the current state of the solver.
     */
    fun getProgress(): SolverProgress? {
        val s = internalSolver ?: return null
        return SolverProgress(
            solutionsFound = s.measures.solutionCount.toInt(),
            bestScore = if (s.measures.solutionCount > 0)
                s.getObjectiveManager<IntVar>().bestSolutionValue.toInt() else 0,
            nodesExplored = s.measures.nodeCount,
            timeElapsed = s.measures.timeCount
        )
    }

    fun stop() {
        stopRequested.set(true)
    }

    fun optimizeAssignments(
        onProgress: (SolverProgress) -> Unit = {}
    ): Map<Player, Pair<Character, Character?>> {
        val model = Model("Ultra Gardener 9000")
        val solver = model.solver
        internalSolver = solver
        solver.limitSearch { stopRequested.get() }

        val numPlayers = players.size
        val numChars = availableChars.size
        val none = numChars // Dummy index representing no reservation

        if (numPlayers == 0 || numChars == 0) return emptyMap()

        // 1. Define Variables
        // assignments[i] represents the index of the character assigned to player i
        val assignments = model.intVarArray("assignments", numPlayers, 0, numChars - 1)
        // reservations[i] represents the index of the character reserved by player i
        val reservations = model.intVarArray("reservations", numPlayers, 0, none)

        // realOccurrences[j] represents how many times character j is assigned overall
        val realOccurrences = Array(numChars) { j ->
            val char = availableChars[j]
            val maxOcc = if (containsPope && char.alignment == CharAlignment.GOOD) numPlayers else char.maxInstances
            model.intVar("realOcc_${char.id}", 0, minOf(numPlayers, maxOcc))
        }
        val reservedOccurrences = Array(numChars + 1) { j ->
            val maxOcc = if (j < numChars) {
                val char = availableChars[j]
                if (containsPope && char.alignment == CharAlignment.GOOD) numPlayers else char.maxInstances
            } else numPlayers
            model.intVar("resOcc_$j", 0, maxOcc)
        }

        val charIndices = IntArray(numChars) { it }
        model.globalCardinality(assignments, charIndices, realOccurrences, true).post()

        val resIndices = IntArray(numChars + 1) { it }
        model.globalCardinality(reservations, resIndices, reservedOccurrences, true).post()

        // 2. Build the Objective/Profit Matrix & Surprise Logic
        val playerPrefMaps = players.map { player ->
            player.selectedChars.withIndex().associate { it.value to it.index }
        }

        val surpriseWeighting = Array(numPlayers) { IntArray(numChars) }
        val inPlayVars = mutableListOf<BoolVar>()
        val surpriseBonusWeights = mutableListOf<Int>()
        val pickedPlayers = mutableSetOf<Int>()

        availableChars.forEachIndexed { cIdx, char ->
            if (char.thinksTheyAre.isNotEmpty()) {
                val chance = surpriseChances[char] ?: 0f
                // Every deceiver gets a variable to track if they are in play
                val inPlay = model.arithm(realOccurrences[cIdx], ">", 0).reify()
                inPlayVars.add(inPlay)

                // Single check per surprise character at the start
                if (Random.nextFloat() < chance) {
                    // SUCCESS: Nearly guarantee inclusion
                    surpriseBonusWeights.add(2000)

                    // Symmetry breaker: Pick one priority player
                    val availablePlayers = (0 until numPlayers).filter { it !in pickedPlayers }
                    if (availablePlayers.isNotEmpty()) {
                        val pIdx = availablePlayers.random()
                        pickedPlayers.add(pIdx)
                        surpriseWeighting[pIdx][cIdx] = 50 // Small tie-breaker
                    }
                } else {
                    // FAILURE: Nearly guarantee exclusion
                    surpriseBonusWeights.add(-2000)
                }
            }
        }

        val profitMatrix = Array(numPlayers) { pIdx ->
            IntArray(numChars) { cIdx ->
                calculateBaseProfit(
                    players[pIdx],
                    availableChars[cIdx],
                    surpriseWeighting[pIdx][cIdx],
                    playerPrefMaps[pIdx]
                )
            }
        }

        val reserveProfitMatrix = Array(numPlayers) { pIdx ->
            IntArray(numChars + 1) { cIdx ->
                if (cIdx == none) 0
                else calculateReserveProfit(
                    players[pIdx],
                    availableChars[cIdx],
                    playerPrefMaps[pIdx]
                )
            }
        }

        // 3. Max Instances Constraint
        for (j in 0 until numChars) {
            val charJ = availableChars[j]

            // If it's a Minion or Demon, real assignments and reservations don't stack for the limit.
            // This allows a player to BE the Imp while another player (Lunatic) THINKS they are the Imp.
            if (charJ.type != CharType.MINION && charJ.type != CharType.DEMON) {
                // For others (Townsfolk/Outsiders), physical + reserved must fit in the bag
                if (!(containsPope && charJ.alignment == CharAlignment.GOOD)) {
                    model.arithm(realOccurrences[j], "+", reservedOccurrences[j], "<=", charJ.maxInstances).post()
                }
            }
        }

        // 4. Enforce Deception Logic
        val validTuples = Tuples(true)
        for (j in 0 until numChars) {
            val charJ = availableChars[j]
            if (charJ.thinksTheyAre.isEmpty()) {
                // Not a deceiver: Must have NONE reserved
                validTuples.add(j, none)
            } else {
                // Is a deceiver: Must reserve a valid type that is not also a deceiver
                for (k in 0 until numChars) {
                    val charK = availableChars[k]
                    val isAllowedType = charK.type in charJ.thinksTheyAre
                    // A deceiver can think they are Char K if:
                    // 1. It's the right type.
                    // 2. Char K isn't also a deceiver (to avoid Drunk thinking they are a Marionette).
                    // OR if it's a Minion/Demon, we allow it even if the 'real' instance exists.
                    if (isAllowedType && charK.thinksTheyAre.isEmpty()) {
                        validTuples.add(j, k)
                    }
                }
            }
        }

        for (i in 0 until numPlayers) {
            val pair = arrayOf(assignments[i], reservations[i])
            model.table(pair, validTuples).post()
        }

        // 5. Dependencies and Hard Jinxes
        for (j in 0 until numChars) {
            val char = availableChars[j]

            // Hard Jinxes (Mutual Exclusion)
            if (char.hardJinxedWith.isNotEmpty()) {
                val inPlay = model.arithm(realOccurrences[j], ">", 0).reify()
                char.hardJinxedWith.forEach { jinxId ->
                    val jinxIdx = availableChars.indexOfFirst { it.id == jinxId }
                    if (jinxIdx >= 0) {
                        val otherChar = availableChars[jinxIdx]
                        if (char.maxInstances == 1 && otherChar.maxInstances == 1) {
                            model.arithm(realOccurrences[j], "+", realOccurrences[jinxIdx], "<=", 1).post()
                        } else {
                            model.ifThen(
                                inPlay,
                                model.arithm(realOccurrences[jinxIdx], "=", 0)
                            )
                        }
                    }
                }
            }

            // Depends On (Implication)
            if (char.dependsOn != null) {
                val depIdx = availableChars.indexOfFirst { it.id == char.dependsOn }
                if (depIdx >= 0) {
                    val inPlay = model.arithm(realOccurrences[j], ">", 0).reify()
                    model.ifThen(
                        inPlay,
                        model.arithm(realOccurrences[depIdx], ">", 0)
                    )
                }
            }
        }

        // 6. Marionette-Demon Indexing Constraint
        val marionetteIdx = availableChars.indexOfFirst { it.id == "marionette" }
        val demonIndices = availableChars.indices.filter { availableChars[it].type == CharType.DEMON }

        if (marionetteIdx >= 0) {
            // Create a boolean variable for each player: "is this player the marionette?"
            val playerIsMarionette = Array(numPlayers) { i ->
                model.arithm(assignments[i], "=", marionetteIdx).reify()
            }

            // Create a boolean variable for each player: "is this player a demon?"
            val demonIndicesArray = demonIndices.toIntArray()
            val playerIsDemon = Array(numPlayers) { i ->
                model.member(assignments[i], demonIndicesArray).reify()
            }

            // Enforce: If Player I is Marionette, then Player I-1 OR Player I+1 must be Demon
            for (i in 0 until numPlayers) {
                val prev = (i + numPlayers - 1) % numPlayers
                val next = (i + 1) % numPlayers

                // Logical constraint: playerIsMarionette[i] implies (playerIsDemon[prev] OR playerIsDemon[next])
                model.ifThen(
                    playerIsMarionette[i],
                    model.arithm(playerIsDemon[prev], "+", playerIsDemon[next], ">=", 1)
                )
            }
        }

        // 7. Marionette-Huntsman-Damsel Constraint
        val huntsmanIdx = availableChars.indexOfFirst { it.id == "huntsman" }
        val damselIdx = availableChars.indexOfFirst { it.id == "damsel" }

        // Only apply if all relevant characters exist in the current script/availableChars
        if (marionetteIdx >= 0 && huntsmanIdx >= 0 && damselIdx >= 0) {
            val marionetteHasHuntsmanRes = Array(numPlayers) { i ->
                // Condition: assignments[i] == Marionette AND reservations[i] == Huntsman
                val isMarionette = model.arithm(assignments[i], "=", marionetteIdx).reify()
                val hasHuntsmanRes = model.arithm(reservations[i], "=", huntsmanIdx).reify()
                model.and(isMarionette, hasHuntsmanRes).reify()
            }

            // If any player satisfies the condition, damsel must be in play
            val anyMarionetteHuntsman = model.or(*marionetteHasHuntsmanRes).reify()
            model.ifThen(
                anyMarionetteHuntsman,
                model.arithm(realOccurrences[damselIdx], ">", 0)
            )
        }

        // 8. Marionette-Balloonist-Modifier Constraint
        val balloonistIdx = availableChars.indexOfFirst { it.id == "balloonist" }
        val marionetteThinkingBalloonist = if (marionetteIdx >= 0 && balloonistIdx >= 0) {
            val playerIsMarioBalloonist = Array(numPlayers) { i ->
                val isMarionette = model.arithm(assignments[i], "=", marionetteIdx).reify()
                val hasBalloonistRes = model.arithm(reservations[i], "=", balloonistIdx).reify()
                model.and(isMarionette, hasBalloonistRes).reify()
            }
            // marionetteThinkingBalloonist is true if ANY player meets the criteria
            model.or(*playerIsMarioBalloonist).reify()
        } else {
            model.boolVar(false)
        }

        val extraDecisionVars = applyTypeCountConstraints(
            model,
            realOccurrences,
            availableChars,
            numPlayers,
            baseCount,
            marionetteThinkingBalloonist
        )

        // 10. Objective Function (Maximize Player Preferences)
        val baseScores = model.intVarArray("baseScores", numPlayers, 0, 16383)
        val reserveScores = model.intVarArray("reserveScores", numPlayers, 0, 16383)

        val maxSurpriseAbs = inPlayVars.size * 2000
        val totalSurpriseBonus = model.intVar("totalSurpriseBonus", -maxSurpriseAbs, maxSurpriseAbs)
        val totalScore = model.intVar("totalScore", -1000000, 1000000)

        if (inPlayVars.isNotEmpty()) {
            model.scalar(inPlayVars.toTypedArray(), surpriseBonusWeights.toIntArray(), "=", totalSurpriseBonus).post()
        } else {
            model.arithm(totalSurpriseBonus, "=", 0).post()
        }

        for (i in 0 until numPlayers) {
            // playerScore[i] = profitMatrix[i][assignments[i]]
            model.element(baseScores[i], profitMatrix[i], assignments[i]).post()
            model.element(reserveScores[i], reserveProfitMatrix[i], reservations[i]).post()
        }
        // Add the global bonus to the total score
        val allScores = baseScores + reserveScores + arrayOf(totalSurpriseBonus)
        model.sum(allScores, "=", totalScore).post()

        // 11. Solve
        model.setObjective(Model.MAXIMIZE, totalScore)

        val sortedChars = Array(numPlayers) { pIdx ->
            availableChars.indices.sortedByDescending { cIdx -> profitMatrix[pIdx][cIdx] }.toIntArray()
        }
        val sortedRes = Array(numPlayers) { pIdx ->
            (0 until numChars + 1).sortedByDescending { cIdx ->
                if (cIdx == none) 0
                else reserveProfitMatrix[pIdx][cIdx]
            }.toIntArray()
        }

        val assignmentToIndex = assignments.withIndex().associate { it.value to it.index }
        val reservationToIndex = reservations.withIndex().associate { it.value to it.index }

        solver.setSearch(
            Search.intVarSearch(
                { vars ->
                    var best: IntVar? = null
                    for (v in vars) {
                        if (!v.isInstantiated) {
                            if (best == null || v.domainSize < best.domainSize) {
                                best = v
                            }
                        }
                    }
                    best
                },
                { v ->
                    val pIdx = assignmentToIndex[v]!!
                    val sorted = sortedChars[pIdx]
                    for (valIdx in sorted) {
                        if (v.contains(valIdx)) return@intVarSearch valIdx
                    }
                    v.lb
                },
                *assignments
            ),
            Search.intVarSearch(
                { vars ->
                    var best: IntVar? = null
                    for (v in vars) {
                        if (!v.isInstantiated) {
                            if (best == null || v.domainSize < best.domainSize) {
                                best = v
                            }
                        }
                    }
                    best
                },
                { v ->
                    val pIdx = reservationToIndex[v]!!
                    val sorted = sortedRes[pIdx]
                    for (valIdx in sorted) {
                        if (v.contains(valIdx)) return@intVarSearch valIdx
                    }
                    v.lb
                },
                *reservations
            ),
            Search.intVarSearch(*extraDecisionVars.toTypedArray())
        )
        solver.limitTime("5s") // 5 second time limit. In most cases, the solver finds a great solution within 1s.

        var bestAssignment: Map<Player, Pair<Character, Character?>> = emptyMap()
        var maxScoreFound = Int.MIN_VALUE

        while (solver.solve()) {
            val currentScore = totalScore.value
            // Only update assignment map if we found a strictly better solution
            if (currentScore > maxScoreFound) {
                maxScoreFound = currentScore
                val currentMap = mutableMapOf<Player, Pair<Character, Character?>>()
                for (i in 0 until numPlayers) {
                    val realChar = availableChars[assignments[i].value]
                    val resIdx = reservations[i].value
                    val fakeChar = if (resIdx == none) null else availableChars[resIdx]
                    currentMap[players[i]] = Pair(realChar, fakeChar)
                }
                bestAssignment = currentMap
            }

            // Immediate update for the new best solution
            onProgress(getProgress()!!)
        }

        return bestAssignment
    }

    private fun calculateBaseProfit(player: Player, char: Character, surpriseWeight: Int, prefMap: Map<Character, Int>): Int {
        var baseProfit: Int
        val selectedPosition = prefMap[char] ?: -1

        if (selectedPosition != -1) {
            if (playerPriorityToggle) {
                val selectedListSize = player.selectedChars.size
                baseProfit = (100 * ((1f * selectedListSize - selectedPosition) / selectedListSize)).toInt()
            } else baseProfit = 100
        } else {
            baseProfit = surpriseWeight // Uses the 50 point tie-breaker or 0
        }

        if (selectedPriority == SelectedPriorities.TYPE && player.typePriority == char.type) baseProfit += 150
        else if (selectedPriority == SelectedPriorities.ALIGNMENT && player.alignmentPriority == char.alignment) baseProfit += 150

        return baseProfit * player.historyWeight
    }

    private fun calculateReserveProfit(player: Player, char: Character, prefMap: Map<Character, Int>): Int {
        var reserveProfit = 0
        val selectedPosition = prefMap[char] ?: -1

        if (selectedPosition != -1) {
            if (playerPriorityToggle) {
                val selectedListSize = player.selectedChars.size
                // (int) (100 * (size - pos) / size) gives 10x multiplier at position 0, linearly down to 1x multiplier at the final position
                reserveProfit = (100 * ((1f * selectedListSize - selectedPosition) / selectedListSize)).toInt()
            } else reserveProfit = 100
        }
        if (selectedPriority == SelectedPriorities.TYPE && player.typePriority == char.type) reserveProfit += 150
        else if (selectedPriority == SelectedPriorities.ALIGNMENT && player.alignmentPriority == char.alignment) reserveProfit += 150
        return reserveProfit * player.historyWeight
    }

    private fun applyTypeCountConstraints(
        model: Model,
        occurrences: Array<IntVar>,
        chars: List<Character>,
        numPlayers: Int,
        baseCount: Count,
        marionetteThinkingBalloonist: BoolVar
    ): List<IntVar> {
        val decisionVars = mutableListOf<IntVar>()

        val tfVars = mutableListOf<IntVar>()
        val outVars = mutableListOf<IntVar>()
        val minVars = mutableListOf<IntVar>()
        val demVars = mutableListOf<IntVar>()

        chars.forEachIndexed { index, char ->
            when (char.type) {
                CharType.TOWNSFOLK -> tfVars.add(occurrences[index])
                CharType.OUTSIDER -> outVars.add(occurrences[index])
                CharType.MINION -> minVars.add(occurrences[index])
                CharType.DEMON -> demVars.add(occurrences[index])
                else -> {}
            }
        }

        val actualTF = model.intVar("actualTF", 0, numPlayers)
        val actualOut = model.intVar("actualOut", 0, numPlayers)
        val actualMin = model.intVar("actualMin", 0, numPlayers)
        val actualDem = model.intVar("actualDem", 0, numPlayers)

        if (tfVars.isNotEmpty()) model.sum(tfVars.toTypedArray(), "=", actualTF).post() else model.arithm(actualTF, "=", 0).post()
        if (outVars.isNotEmpty()) model.sum(outVars.toTypedArray(), "=", actualOut).post() else model.arithm(actualOut, "=", 0).post()
        if (minVars.isNotEmpty()) model.sum(minVars.toTypedArray(), "=", actualMin).post() else model.arithm(actualMin, "=", 0).post()
        if (demVars.isNotEmpty()) model.sum(demVars.toTypedArray(), "=", actualDem).post() else model.arithm(actualDem, "=", 0).post()

        val legionIndex = chars.indexOfFirst { it.id == "legion" }

        // --- STAGE 1: OVERRIDE MODIFIERS & DEFICIT REDISTRIBUTION ---

        // Enforce max 1 override-granting character active
        val charsWithOverrides = chars.indices.filter { chars[it].overrideModifiers.isNotEmpty() }
        val overrideInPlayVars = charsWithOverrides.map { j ->
            model.arithm(occurrences[j], ">", 0).reify()
        }.toTypedArray()

        if (overrideInPlayVars.isNotEmpty()) {
            // At most one character with an override modifier can be assigned
            model.sum(overrideInPlayVars, "<=", 1).post()
        }

        // Determine if a specific character type is currently being overridden
        val overriddenTF = model.boolVar("overriddenTF")
        val overriddenOut = model.boolVar("overriddenOut")
        val overriddenMin = model.boolVar("overriddenMin")
        val overriddenDem = model.boolVar("overriddenDem")

        fun overrideVarsFor(type: CharType): Array<BoolVar> {
            return charsWithOverrides.filter { chars[it].overrideModifiers.contains(type) }
                .map { j -> overrideInPlayVars[charsWithOverrides.indexOf(j)] }
                .toTypedArray()
        }

        val tfOverrideArr = overrideVarsFor(CharType.TOWNSFOLK)
        if (tfOverrideArr.isNotEmpty()) model.sum(tfOverrideArr, "=", overriddenTF).post() else model.arithm(overriddenTF, "=", 0).post()

        val outOverrideArr = overrideVarsFor(CharType.OUTSIDER)
        if (outOverrideArr.isNotEmpty()) model.sum(outOverrideArr, "=", overriddenOut).post() else model.arithm(overriddenOut, "=", 0).post()

        val minOverrideArr = overrideVarsFor(CharType.MINION)
        if (minOverrideArr.isNotEmpty()) model.sum(minOverrideArr, "=", overriddenMin).post() else model.arithm(overriddenMin, "=", 0).post()

        val demOverrideArr = overrideVarsFor(CharType.DEMON)
        if (demOverrideArr.isNotEmpty()) model.sum(demOverrideArr, "=", overriddenDem).post() else model.arithm(overriddenDem, "=", 0).post()

        // Calculate deficit from the overridden types
        val deficitTF = model.intVar("defTF", 0, numPlayers)
        model.arithm(deficitTF, "=", overriddenTF, "*", baseCount.townsfolk).post()
        val deficitOut = model.intVar("defOut", 0, numPlayers)
        model.arithm(deficitOut, "=", overriddenOut, "*", baseCount.outsider).post()
        val deficitMin = model.intVar("defMin", 0, numPlayers)
        model.arithm(deficitMin, "=", overriddenMin, "*", baseCount.minion).post()
        val deficitDem = model.intVar("defDem", 0, numPlayers)
        model.arithm(deficitDem, "=", overriddenDem, "*", baseCount.demon).post()

        val totalDeficit = model.intVar("totalDeficit", 0, numPlayers * 2)
        model.sum(arrayOf(deficitTF, deficitOut, deficitMin, deficitDem), "=", totalDeficit).post()

        // Legion-specific constrains
        val legionExtra = model.intVar("legionExtra", 0, numPlayers)
        val tfLB = model.intVar("tfLB", -numPlayers, 0)
        val outLB = model.intVar("outLB", -numPlayers, 0)

        val isLegion = model.boolVar("isLegion")

        if (legionIndex >= 0) {
            val legionOcc = occurrences[legionIndex]

            model.ifOnlyIf(
                model.arithm(legionOcc, ">", 0),
                model.arithm(isLegion, "=", 1)
            )

            // In a Legion game, the number of good and evils should generally be reversed
            //      I interpreted this as + or - 1, like the Sentinel
            val baseGood = baseCount.townsfolk + baseCount.outsider
            val minLegion = baseGood - 1
            val maxLegion = baseGood + 1

            // Constrain boundaries when Legion is in play
            model.ifThen(isLegion, model.arithm(legionOcc, ">=", minLegion))
            model.ifThen(isLegion, model.arithm(legionOcc, "<=", maxLegion))

            // If Legion is in play, there can be NO other demons
            val otherDemons = chars.indices.filter { it != legionIndex && chars[it].type == CharType.DEMON }
                .map { occurrences[it] }.toTypedArray()
            if (otherDemons.isNotEmpty()) {
                model.ifThen(isLegion, model.sum(otherDemons, "=", 0))
            }

            // Calculate the extra Demon slots that Legion consumes beyond the base count
            model.ifThenElse(
                isLegion,
                model.arithm(legionExtra, "=", legionOcc, "-", baseCount.demon),
                model.arithm(legionExtra, "=", 0)
            )
        } else {
            // Legion is not on the script
            model.arithm(isLegion, "=", 0).post()
            model.arithm(legionExtra, "=", 0).post()
        }

        // Set the lower bound for the extra tf / outsiders
        model.times(isLegion, -numPlayers, tfLB).post()
        model.times(isLegion, -numPlayers, outLB).post()

        // We expand domain sizes for Extra variables to handle Legion eating into them
        val extraTF = model.intVar("extraTF", -numPlayers, numPlayers)
        val extraOut = model.intVar("extraOut", -numPlayers, numPlayers)

        model.arithm(extraTF, ">=", tfLB).post()
        model.arithm(extraOut, ">=", outLB).post()

        val netExtraSlots = model.intVar("netExtraSlots", -numPlayers, numPlayers)
        model.arithm(netExtraSlots, "=", totalDeficit, "-", legionExtra).post()
        model.arithm(extraTF, "+", extraOut, "=", netExtraSlots).post()

        model.ifThen(model.arithm(overriddenTF, "=", 1), model.arithm(extraTF, "=", 0))
        model.ifThen(model.arithm(overriddenOut, "=", 1), model.arithm(extraOut, "=", 0))

        // Create Base Counts after Overrides
        fun createFinalBase(name: String, base: Int, overriddenVar: IntVar, extraVar: IntVar? = null): IntVar {
            val finalBase = model.intVar(name, 0, numPlayers * 2)
            val notOverridden = model.intVar(0, 1)
            model.arithm(notOverridden, "+", overriddenVar, "=", 1).post()

            val postOverride = model.intVar("${name}_post", 0, numPlayers)
            model.arithm(postOverride, "=", notOverridden, "*", base).post()

            if (extraVar != null) {
                model.arithm(finalBase, "=", postOverride, "+", extraVar).post()
            } else {
                model.arithm(finalBase, "=", postOverride).post()
            }
            return finalBase
        }

        val finalBaseTF = createFinalBase("finalBaseTF", baseCount.townsfolk, overriddenTF, extraTF)
        val finalBaseOut = createFinalBase("finalBaseOut", baseCount.outsider, overriddenOut, extraOut)
        val finalBaseMin = createFinalBase("finalBaseMin", baseCount.minion, overriddenMin)
        val finalBaseDem = createFinalBase("finalBaseDem", baseCount.demon, overriddenDem, legionExtra)


        // --- STAGE 2: ADDITIVE MODIFIERS ---

        val deltaTFs = mutableListOf<IntVar>()
        val deltaOuts = mutableListOf<IntVar>()
        val deltaMins = mutableListOf<IntVar>()
        val deltaDems = mutableListOf<IntVar>()

        for (j in chars.indices) {
            val char = chars[j]
            if (char.additiveModifiers.isEmpty()) continue

            val choiceTF: IntVar
            val choiceOut: IntVar
            val choiceMin: IntVar
            val choiceDem: IntVar

            if (char.additiveModifiers.size == 1) {
                val mod = char.additiveModifiers[0]
                choiceTF = model.intVar(mod.townsfolk)
                choiceOut = model.intVar(mod.outsider)
                choiceMin = model.intVar(mod.minion)
                choiceDem = model.intVar(mod.demon)
            } else {
                val choiceIdx = model.intVar("choiceIdx_$j", 0, char.additiveModifiers.size - 1)
                decisionVars.add(choiceIdx)
                choiceTF = model.intVar("choiceTF_$j", -numPlayers, numPlayers)
                choiceOut = model.intVar("choiceOut_$j", -numPlayers, numPlayers)
                choiceMin = model.intVar("choiceMin_$j", -numPlayers, numPlayers)
                choiceDem = model.intVar("choiceDem_$j", -numPlayers, numPlayers)

                val tuples = Tuples(true)
                char.additiveModifiers.forEachIndexed { idx, mod ->
                    tuples.add(idx, mod.townsfolk, mod.outsider, mod.minion, mod.demon)
                }
                model.table(arrayOf(choiceIdx, choiceTF, choiceOut, choiceMin, choiceDem), tuples).post()
            }

            val dTF = model.intVar("dTF_$j", -numPlayers, numPlayers)
            model.times(occurrences[j], choiceTF, dTF).post()
            deltaTFs.add(dTF)

            val dOut = model.intVar("dOut_$j", -numPlayers, numPlayers)
            model.times(occurrences[j], choiceOut, dOut).post()
            deltaOuts.add(dOut)

            val dMin = model.intVar("dMin_$j", -numPlayers, numPlayers)
            model.times(occurrences[j], choiceMin, dMin).post()
            deltaMins.add(dMin)

            val dDem = model.intVar("dDem_$j", -numPlayers, numPlayers)
            model.times(occurrences[j], choiceDem, dDem).post()
            deltaDems.add(dDem)

            // Specifically for the Balloonist, if they are reserved by specifically the marionette, their additive modifiers are applied
            if (char.id == "balloonist") {
                val marioDovTF = model.intVar("marioDovTF_$j", -numPlayers, numPlayers)
                val marioDovOut = model.intVar("marioDovOut_$j", -numPlayers, numPlayers)

                model.ifThenElse(
                    marionetteThinkingBalloonist,
                    model.arithm(marioDovTF, "=", choiceTF),
                    model.arithm(marioDovTF, "=", 0)
                )
                model.ifThenElse(
                    marionetteThinkingBalloonist,
                    model.arithm(marioDovOut, "=", choiceOut),
                    model.arithm(marioDovOut, "=", 0)
                )

                deltaTFs.add(marioDovTF)
                deltaOuts.add(marioDovOut)
            }
        }

        val totalAddTF = model.intVar("totalAddTF", -numPlayers, numPlayers)
        if (deltaTFs.isNotEmpty()) model.sum(deltaTFs.toTypedArray(), "=", totalAddTF).post() else model.arithm(totalAddTF, "=", 0).post()

        val totalAddOut = model.intVar("totalAddOut", -numPlayers, numPlayers)
        if (deltaOuts.isNotEmpty()) model.sum(deltaOuts.toTypedArray(), "=", totalAddOut).post() else model.arithm(totalAddOut, "=", 0).post()

        val totalAddMin = model.intVar("totalAddMin", -numPlayers, numPlayers)
        if (deltaMins.isNotEmpty()) model.sum(deltaMins.toTypedArray(), "=", totalAddMin).post() else model.arithm(totalAddMin, "=", 0).post()

        val totalAddDem = model.intVar("totalAddDem", -numPlayers, numPlayers)
        if (deltaDems.isNotEmpty()) model.sum(deltaDems.toTypedArray(), "=", totalAddDem).post() else model.arithm(totalAddDem, "=", 0).post()

        // --- STAGE 3: SENTINEL MODIFIER ---

        val sentinelTF = model.intVar("sentinelTF", -1, 1)
        val sentinelOut = model.intVar("sentinelOut", -1, 1)
        decisionVars.add(sentinelTF)
        decisionVars.add(sentinelOut)

        if (autoSentinel) {
            // They must precisely offset one another: either (-1, 1), (0, 0), or (1, -1)
            model.arithm(sentinelTF, "+", sentinelOut, "=", 0).post()
        } else {
            model.arithm(sentinelTF, "=", 0).post()
            model.arithm(sentinelOut, "=", 0).post()
        }

        // --- STAGE 4: FINAL TARGET CALCULATION ---

        val targetTF = model.intVar("targetTF", 0, numPlayers * 2)
        model.sum(arrayOf(finalBaseTF, totalAddTF, sentinelTF), "=", targetTF).post()

        val targetOut = model.intVar("targetOut", 0, numPlayers * 2)
        model.sum(arrayOf(finalBaseOut, totalAddOut, sentinelOut), "=", targetOut).post()

        val targetMin = model.intVar("targetMin", 0, numPlayers * 2)
        model.arithm(targetMin, "=", finalBaseMin, "+", totalAddMin).post()

        val targetDem = model.intVar("targetDem", 0, numPlayers * 2)
        model.arithm(targetDem, "=", finalBaseDem, "+", totalAddDem).post()


        // --- STAGE 5: ENFORCE COUNTS ---

        model.arithm(actualTF, "=", targetTF).post()
        model.arithm(actualOut, "=", targetOut).post()
        model.arithm(actualMin, "=", targetMin).post()
        model.arithm(actualDem, "=", targetDem).post()

        return decisionVars
    }
}