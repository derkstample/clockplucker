package com.example.clockplucker

import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Character
import com.example.clockplucker.data.Count
import com.example.clockplucker.data.ModifierMode
import com.example.clockplucker.data.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chocosolver.solver.Model
import org.chocosolver.solver.search.strategy.Search
import org.chocosolver.solver.variables.BoolVar
import org.chocosolver.solver.variables.IntVar

class RoleSolver (
    private val players: List<Player>,
    private val characters: List<Character>,
    private val startingCount: Count,
    private val selectedPriority: SelectedPriorities,
    private val playerPriority: Boolean,
    private val surpriseCharPriority: Int
) {
    suspend fun solve(): Map<Player, Character?> = withContext(Dispatchers.Default) {
        val model = Model("Ultra Gardener 9000")

        // Decision Matrix: assignment[p][r]
        val assignment = Array(players.size) { p ->
            model.boolVarArray("p${p}_roles", characters.size)
        }

        // Count variables for each role
        // roleUsage[r] = how many players have role r
        val roleUsage = Array(characters.size) { r ->
            val max = characters[r].maxInstances
            model.intVar("r${characters[r].id}_usage", 0, max)
        }

        // Tracks if a character is in play (sum of players assigned to it)
        val inPlay = Array(characters.size) { r ->
            model.boolVar("${characters[r].id}_inPlay")
        }

        // Link assignment matrix to roleUsage counts
        for (r in characters.indices) {
            val playersWithThisRole = Array(players.size) { p -> assignment[p][r] }
            model.sum(playersWithThisRole, "=", roleUsage[r]).post()
            // Link inPlay boolean: true if roleUsage > 0
            model.sum(playersWithThisRole, ">", 0).reifyWith(inPlay[r])
        }

        // Basic Constraints

        // Each player must have exactly 1 role
        for (p in players.indices) {
            model.sum(assignment[p], "=", 1).post()
        }

        // --- SETUP MODIFIERS ---

        val townsfolkCount = model.intVar("tCount", 0, players.size)
        val outsiderCount = model.intVar("oCount", 0, players.size)
        val minionCount = model.intVar("mCount", 0, players.size)
        val demonCount = model.intVar("dCount", 0, players.size)

        // Define the base counts from the game rules (startingCount)
        val baseT = startingCount.townsfolk
        val baseO = startingCount.outsider
        val baseM = startingCount.minion
        val baseD = startingCount.demon

        // Lists to hold the setup modifiers from each character
        val tMods = mutableListOf(model.intVar(baseT))
        val oMods = mutableListOf(model.intVar(baseO))
        val mMods = mutableListOf(model.intVar(baseM))
        val dMods = mutableListOf(model.intVar(baseD))

        characters.forEachIndexed { rIdx, char ->
            char.modifierOptions.forEach { mod ->
                // If character is in play, apply modifier, else 0
                if (mod.mode == ModifierMode.ADDITIVE) {
                    tMods.add(createConditionalMod(model, inPlay[rIdx], mod.counts.townsfolk))
                    oMods.add(createConditionalMod(model, inPlay[rIdx], mod.counts.outsider))
                    mMods.add(createConditionalMod(model, inPlay[rIdx], mod.counts.minion))
                    dMods.add(createConditionalMod(model, inPlay[rIdx], mod.counts.demon))
                } else if (mod.mode == ModifierMode.OVERRIDE) {
                    // OVERRIDE logic: If inPlay, force the final count to specific value
                    // Note: Multiple overrides in play at once will cause "No Solution" (Correct for BotC)
                    model.ifThen(inPlay[rIdx], model.arithm(townsfolkCount, "=", mod.counts.townsfolk))
                    model.ifThen(inPlay[rIdx], model.arithm(outsiderCount, "=", mod.counts.outsider))
                    model.ifThen(inPlay[rIdx], model.arithm(minionCount, "=", mod.counts.minion))
                    model.ifThen(inPlay[rIdx], model.arithm(demonCount, "=", mod.counts.demon))
                }
            }
        }

        // Sum all additive modifiers into the final count variables
        model.sum(tMods.toTypedArray(), "=", townsfolkCount).post()
        model.sum(oMods.toTypedArray(), "=", outsiderCount).post()
        model.sum(mMods.toTypedArray(), "=", minionCount).post()
        model.sum(dMods.toTypedArray(), "=", demonCount).post()

        // Integrity: Sum of types must equal player count
        model.sum(arrayOf(townsfolkCount, outsiderCount, minionCount, demonCount), "=", players.size).post()

        // Link types to calculated counts
        val typeVars = CharType.entries.associateWith { type ->
            characters.indices.filter { characters[it].type == type }.map { roleUsage[it] }
        }
        model.sum(typeVars[CharType.TOWNSFOLK]!!.toTypedArray(), "=", townsfolkCount).post()
        model.sum(typeVars[CharType.OUTSIDER]!!.toTypedArray(), "=", outsiderCount).post()
        model.sum(typeVars[CharType.MINION]!!.toTypedArray(), "=", minionCount).post()
        model.sum(typeVars[CharType.DEMON]!!.toTypedArray(), "=", demonCount).post()

        // Map for quick lookup of character index by ID
        val idToIndex = characters.withIndex().associate { it.value.id to it.index }

        // --- AUTOMATED DEPENDENCIES & HARD JINXES ---
        characters.forEachIndexed { rIdx, char ->
            // Dependency: If char requires another, inPlay[this] <= inPlay[target]
            char.dependsOn?.let { targetId ->
                idToIndex[targetId]?.let { targetIdx ->
                    model.arithm(inPlay[rIdx], "<=", inPlay[targetIdx]).post()
                }
            }

            // 1. Hard Jinxes: These two characters cannot both be in play
            char.hardJinxedWith.forEach { jinxId ->
                idToIndex[jinxId]?.let { jIdx ->
                    model.arithm(inPlay[rIdx], "+", inPlay[jIdx], "<=", 1).post()
                }
            }
        }

        // --- OBJECTIVE: MAXIMIZE SATISFACTION ---
        val satisfaction = model.intVar("total_sat", 0, 10000)
        val weights = mutableListOf<IntVar>()
        val coeffs = mutableListOf<Int>()

        for (p in players.indices) {
            for (r in characters.indices) {
                val weight = calculateWeight(players[p], characters[r])
                if (weight != 0) {
                    weights.add(assignment[p][r])
                    coeffs.add(weight)
                }
            }
        }
        model.scalar(weights.toTypedArray(), coeffs.toIntArray(), "=", satisfaction).post()
        model.setObjective(Model.MAXIMIZE, satisfaction)

        // --- EXECUTION ---
        val solver = model.solver
        solver.limitTime("30s")

        val allAssignmentVars = assignment.flatMap { it.toList() }.toTypedArray()
        solver.setSearch(Search.intVarSearch(*allAssignmentVars))

        val solution = solver.findOptimalSolution(satisfaction, Model.MAXIMIZE)

        // Use defaultSolution() if findOptimalSolution timed out but found something
        if (solution != null || solver.solutionCount > 0) {
            val finalSolution = solution ?: solver.defaultSolution()
            return@withContext players.associateWith { p ->
                val pIdx = players.indexOf(p)
                val rIdx = characters.indices.firstOrNull { r ->
                    finalSolution.getIntVal(assignment[pIdx][r]) == 1
                }
                rIdx?.let { characters[it] }
            }
        }
        emptyMap()
    }

    /**
     * Helper to create a variable that is [value] if [condition] is true, else 0
     */
    private fun createConditionalMod(model: Model, condition: BoolVar, value: Int): IntVar {
        if (value == 0) return model.intVar(0)
        val res = model.intVar("mod", minOf(0, value), maxOf(0, value))
        model.ifThenElse(condition,
            model.arithm(res, "=", value),
            model.arithm(res, "=", 0)
        )
        return res
    }

    fun calculateWeight(player: Player, char: Character): Int {
        val isPreferred = player.selectedChars.contains(char)

        // 1. Base score
        val baseScore = if (playerPriority) {
            if (isPreferred) { player.selectedChars.reversed().indexOf(char) * 5 } else 1
        } else {
            if (isPreferred) 5 else 1
        }

        // 2. Apply History Multiplier (The "Pity" factor)
        val weightedScore = (baseScore * player.historyWeight)

        // 3. Priority boost (The "Storyteller's Choice")
        val priorityMultiplier = when (selectedPriority) {
            SelectedPriorities.NO_PRIORITIES -> 1
            SelectedPriorities.ALIGNMENT -> if (player.alignmentPriority == char.alignment) 5 else 1 // todo: fine tune multiplier values
            SelectedPriorities.TYPE -> if (player.typePriority == char.type) 5 else 1
        }

        // 4. Priority for surprise characters like the Drunk, Lunatic, and Marionette
        val surpriseMultiplier = if (!char.isSelectable) surpriseCharPriority * 5 else 1

        return weightedScore * priorityMultiplier * surpriseMultiplier
    }
}