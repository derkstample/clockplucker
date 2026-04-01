package com.example.clockplucker.data

import com.example.clockplucker.SelectedPriorities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.extension.Tuples
import org.chocosolver.solver.variables.IntVar
import kotlin.random.Random

class RoleSolver (
    private val players: List<Player>,
    private val availableChars: List<Character>,
    private val baseCount: Count,
    private val unselectableChance: Float,
    private val selectedPriority: SelectedPriorities,
    private val playerPriorityToggle: Boolean
){
    suspend fun optimizeAssignments(): Map<Player, Pair<Character, Character?>> = withContext(Dispatchers.Default){
        val model = Model("Ultra Gardener 9000")
        val numPlayers = players.size
        val numChars = availableChars.size
        val NONE = numChars // Dummy index representing no reservation

        if (numPlayers == 0 || numChars == 0) return@withContext emptyMap()

        // 1. Build the Objective/Profit Matrix
        // Rows = Players, Cols = Characters
        val profitMatrix = Array(numPlayers) { pIdx ->
            IntArray(numChars) { cIdx ->
                calculateBaseProfit(
                    players[pIdx],
                    availableChars[cIdx]
                )
            }
        }

        val reserveProfitMatrix = Array(numPlayers) { pIdx ->
            IntArray(numChars + 1) { cIdx ->
                if (cIdx == NONE) 0
                else calculateReserveProfit(
                    players[pIdx],
                    availableChars[cIdx]
                )
            }
        }

        // 2. Define Variables
        // assignments[i] represents the index of the character assigned to player i
        val assignments = model.intVarArray("assignments", numPlayers, 0, numChars - 1)
        // reservations[i] represents the index of the character reserved by player i
        val reservations = model.intVarArray("reservations", numPlayers, 0, NONE)

        // realOccurrences[j] represents how many times character j is assigned overall
        val realOccurrences = model.intVarArray("realOccurrences", numChars, 0, numPlayers)
        val reservedOccurrences = model.intVarArray("reservedOccurrences", numChars + 1, 0, numPlayers)

        // 3. Max Instances Constraint
        for (j in 0 until numChars) {
            model.count(j, assignments, realOccurrences[j]).post()
            model.count(j, reservations, reservedOccurrences[j]).post()

            val totalOcc = model.intVar("totalOcc_$j", 0, numPlayers)
            model.arithm(realOccurrences[j], "+", reservedOccurrences[j], "=", totalOcc).post()
            // Ensure real + reserved tokens don't exceed the bag limit
            model.arithm(totalOcc, "<=", availableChars[j].maxInstances).post()
        }

        // 4. Enforce Deception Logic
        val validTuples = Tuples(true)
        for (j in 0 until numChars) {
            val charJ = availableChars[j]
            if (charJ.thinksTheyAre.isEmpty()) {
                // Not a deceiver: Must have NONE reserved
                validTuples.add(j, NONE)
            } else {
                // Is a deceiver: Must reserve a valid type
                for (k in 0 until numChars) {
                    if (availableChars[k].type in charJ.thinksTheyAre) {
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
            val inPlay = model.arithm(realOccurrences[j], ">", 0).reify()

            // Hard Jinxes (Mutual Exclusion)
            if (char.hardJinxedWith.isNotEmpty()) {
                char.hardJinxedWith.forEach { jinxId ->
                    val jinxIdx = availableChars.indexOfFirst { it.id == jinxId }
                    if (jinxIdx >= 0) {
                        model.ifThen(
                            inPlay,
                            model.arithm(realOccurrences[jinxIdx], "=", 0)
                        )
                    }
                }
            }

            // Depends On (Implication)
            if (char.dependsOn != null) {
                val depIdx = availableChars.indexOfFirst { it.id == char.dependsOn }
                if (depIdx >= 0) {
                    model.ifThen(
                        inPlay,
                        model.arithm(realOccurrences[depIdx], ">", 0)
                    )
                }
            }
        }

        // 6. Setup Modifiers and Type Counts
        applyTypeCountConstraints(model, realOccurrences, availableChars, numPlayers, baseCount)

        // 7. Objective Function (Maximize Player Preferences)
        val baseScores = model.intVarArray("baseScores", numPlayers, 0, 10000)
        val reserveScores = model.intVarArray("reserveScores", numPlayers, 0, 10000)
        val totalScore = model.intVar("totalScore", 0, 999999)

        for (i in 0 until numPlayers) {
            // playerScore[i] = profitMatrix[i][assignments[i]]
            model.element(baseScores[i], profitMatrix[i], assignments[i]).post()
            model.element(reserveScores[i], reserveProfitMatrix[i], reservations[i]).post()
        }
        val allScores = baseScores + reserveScores
        model.sum(allScores, "=", totalScore).post()

        // 8. Solve
        model.setObjective(Model.MAXIMIZE, totalScore)
        val solver = model.solver

        var bestAssignment: Map<Player, Pair<Character, Character?>> = emptyMap()

        // Iterate through solutions to find the maximum
        while (solver.solve()) {
            val currentMap = mutableMapOf<Player, Pair<Character, Character?>>()
            for (i in 0 until numPlayers) {
                val realChar = availableChars[assignments[i].value]
                val resIdx = reservations[i].value
                val fakeChar = if (resIdx == NONE) null else availableChars[resIdx]

                currentMap[players[i]] = Pair(realChar, fakeChar)
            }
            bestAssignment = currentMap
        }

        return@withContext bestAssignment
    }

    private fun calculateBaseProfit(player: Player, char: Character): Int {
        var baseProfit = 0
        val selectedPosition = player.selectedChars.indexOf(char)

        if (selectedPosition != -1) {
            baseProfit = 10 * player.historyWeight
            if (playerPriorityToggle) {
                val selectedListSize = player.selectedChars.size
                // (int) (10 * (size - pos) / size) gives 10x multiplier at position 0, linearly down to 1x multiplier at the final position
                baseProfit *= (10 * ((1f * selectedListSize - selectedPosition) / selectedPosition)).toInt()
            }
            if (selectedPriority == SelectedPriorities.TYPE && player.typePriority == char.type) baseProfit *= 10
            else if (selectedPriority == SelectedPriorities.ALIGNMENT && player.alignmentPriority == char.alignment) baseProfit *= 10
        } else {
            // Handle unselectable chance by injecting random probability into the deterministic solver matrix
            if (!char.thinksTheyAre.isEmpty() && Random.nextFloat() < unselectableChance) {
                baseProfit = 100 // Give a large baseline chance to be assigned (basically guarantee if the chance allows)
            }
        }
        return baseProfit
    }

    private fun calculateReserveProfit(player: Player, char: Character): Int {
        var reserveProfit = 0
        val selectedPosition = player.selectedChars.indexOf(char)

        if (selectedPosition != -1) {
            reserveProfit = 10 * player.historyWeight
            if (playerPriorityToggle) {
                val selectedListSize = player.selectedChars.size
                // (int) (10 * (size - pos) / size) gives 10x multiplier at position 0, linearly down to 1x multiplier at the final position
                reserveProfit *= (10 * ((1f * selectedListSize - selectedPosition) / selectedPosition)).toInt()
            }
            if (selectedPriority == SelectedPriorities.TYPE && player.typePriority == char.type) reserveProfit *= 10
            else if (selectedPriority == SelectedPriorities.ALIGNMENT && player.alignmentPriority == char.alignment) reserveProfit *= 10
        }
        return reserveProfit
    }

    private fun applyTypeCountConstraints(
        model: Model,
        occurrences: Array<IntVar>,
        chars: List<Character>,
        numPlayers: Int,
        baseCount: Count
    ) {
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

        // --- STAGE 1: OVERRIDE MODIFIERS & DEFICIT REDISTRIBUTION ---

        // Enforce max 1 override-granting character active
        val charsWithOverrides = chars.indices.filter { chars[it].overrideModifiers.isNotEmpty() }
        val overrideInPlayVars = charsWithOverrides.map { j ->
            val inPlay = model.boolVar("overrideInPlay_$j")
            model.ifOnlyIf(model.arithm(occurrences[j], ">", 0), model.arithm(inPlay, "=", 1))
            inPlay
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

        fun overrideVarsFor(type: CharType): Array<IntVar> {
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

        // Distribute the deficit to remaining safe types (Townsfolk/Outsiders)
        val extraTF = model.intVar("extraTF", 0, numPlayers)
        val extraOut = model.intVar("extraOut", 0, numPlayers)
        model.arithm(extraTF, "+", extraOut, "=", totalDeficit).post()

        // A type cannot receive redistributed points if it is currently overridden to zero
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
        val finalBaseDem = createFinalBase("finalBaseDem", baseCount.demon, overriddenDem)


        // --- STAGE 2: ADDITIVE MODIFIERS ---

        val deltaTFs = mutableListOf<IntVar>()
        val deltaOuts = mutableListOf<IntVar>()
        val deltaMins = mutableListOf<IntVar>()
        val deltaDems = mutableListOf<IntVar>()

        for (j in chars.indices) {
            val char = chars[j]
            var netTF = 0
            var netOut = 0
            var netMin = 0
            var netDem = 0

            // Multiple additive modifiers strictly sum up their effects mathematically
            for (mod in char.additiveModifiers) {
                netTF += mod.townsfolk
                netOut += mod.outsider
                netMin += mod.minion
                netDem += mod.demon
            }

            if (netTF != 0) {
                val d = model.intVar("dTF_$j", -numPlayers, numPlayers)
                model.arithm(d, "=", occurrences[j], "*", netTF).post()
                deltaTFs.add(d)
            }
            if (netOut != 0) {
                val d = model.intVar("dOut_$j", -numPlayers, numPlayers)
                model.arithm(d, "=", occurrences[j], "*", netOut).post()
                deltaOuts.add(d)
            }
            if (netMin != 0) {
                val d = model.intVar("dMin_$j", -numPlayers, numPlayers)
                model.arithm(d, "=", occurrences[j], "*", netMin).post()
                deltaMins.add(d)
            }
            if (netDem != 0) {
                val d = model.intVar("dDem_$j", -numPlayers, numPlayers)
                model.arithm(d, "=", occurrences[j], "*", netDem).post()
                deltaDems.add(d)
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


        // --- STAGE 3: FINAL TARGET CALCULATION ---

        val targetTF = model.intVar("targetTF", 0, numPlayers * 2)
        model.arithm(targetTF, "=", finalBaseTF, "+", totalAddTF).post()

        val targetOut = model.intVar("targetOut", 0, numPlayers * 2)
        model.arithm(targetOut, "=", finalBaseOut, "+", totalAddOut).post()

        val targetMin = model.intVar("targetMin", 0, numPlayers * 2)
        model.arithm(targetMin, "=", finalBaseMin, "+", totalAddMin).post()

        val targetDem = model.intVar("targetDem", 0, numPlayers * 2)
        model.arithm(targetDem, "=", finalBaseDem, "+", totalAddDem).post()


        // --- STAGE 4: ENFORCE COUNTS ---

        model.arithm(actualTF, "=", targetTF).post()
        model.arithm(actualOut, "=", targetOut).post()
        model.arithm(actualMin, "=", targetMin).post()
        model.arithm(actualDem, "=", targetDem).post()
    }
}