package com.example.clockplucker

import com.example.clockplucker.data.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class RoleSolverTest {

    @Test
    fun testRoleSolverBasicAssignment() = runBlocking {
        // 1. Setup 8 players
        val players = (1..8).map { i ->
            Player(id = UUID.randomUUID(), name = "Player $i")
        }

        // 2. Setup 15 characters
        // 9 Townsfolk, 3 Outsiders, 2 Minions, 1 Demon
        val characters = mutableListOf<Character>()
        (1..9).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        (1..3).forEach { i ->
            characters.add(Character("o$i", "Outsider $i", CharType.OUTSIDER, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", "Minion $i", CharType.MINION, icon = 0, ability = ""))
        }
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))

        // 3. Define starting count for 8 players: 5 Townsfolk, 1 Outsider, 1 Minion, 1 Demon
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 0f,
            selectedPriority = SelectedPriorities.NO_PRIORITIES,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the distribution matches the starting count
        val assignedRoles = assignments.values
        assertEquals(5, assignedRoles.count { it.first.type == CharType.TOWNSFOLK })
        assertEquals(1, assignedRoles.count { it.first.type == CharType.OUTSIDER })
        assertEquals(1, assignedRoles.count { it.first.type == CharType.MINION })
        assertEquals(1, assignedRoles.count { it.first.type == CharType.DEMON })
    }

    @Test
    fun testRoleSolverMaximumSatisfaction() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", "Outsider $i", CharType.OUTSIDER, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", "Minion $i", CharType.MINION, icon = 0, ability = ""))
        }
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))
        // 2. Setup 6 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[6])), // wants o1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[8])), // wants m1
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 6 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 0f,
            selectedPriority = SelectedPriorities.NO_PRIORITIES,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments are optimal
        assert(assignments[players[0]]?.first == characters[0])
        assert(assignments[players[1]]?.first == characters[1])
        assert(assignments[players[2]]?.first == characters[2])
        assert(assignments[players[3]]?.first == characters[6])
        assert(assignments[players[4]]?.first == characters[8])
        assert(assignments[players[5]]?.first == characters[10])
    }

    @Test
    fun testRoleSolverMaximumSatisfactionWithOverrides() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", "Outsider $i", CharType.OUTSIDER, icon = 0, ability = ""))
        }
        characters.add(Character("baron", "Baron", CharType.MINION, icon = 0, ability = "", additiveModifiers = listOf(Count(townsfolk = -2, outsider = 2))))
        characters.add(Character("m2", "Minion 2", CharType.MINION, icon = 0, ability = ""))
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))
        // 2. Setup 5 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[8]), historyWeight = 3), // wants baron (means two townsfolk will get outsiders)
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 5 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 0f,
            selectedPriority = SelectedPriorities.NO_PRIORITIES,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments obey the Baron's modifier
        assert(
            assignments[players[0]]?.first?.type == CharType.TOWNSFOLK  && assignments[players[1]]?.first?.type == CharType.OUTSIDER && assignments[players[2]]?.first?.type == CharType.OUTSIDER
                    || assignments[players[0]]?.first?.type == CharType.OUTSIDER  && assignments[players[1]]?.first?.type == CharType.TOWNSFOLK && assignments[players[2]]?.first?.type == CharType.OUTSIDER
                    || assignments[players[0]]?.first?.type == CharType.OUTSIDER  && assignments[players[1]]?.first?.type == CharType.OUTSIDER && assignments[players[2]]?.first?.type == CharType.TOWNSFOLK
        )
        assert(assignments[players[3]]?.first == characters[8])
        assert(assignments[players[4]]?.first == characters[10])
    }

    @Test
    fun testRoleSolverMaximumSatisfactionWithTypePriority() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", "Outsider $i", CharType.OUTSIDER, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", "Minion $i", CharType.MINION, icon = 0, ability = ""))
        }
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))
        // 2. Setup 5 Players, each with 2 preferred characters
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0], characters[9]), typePriority = CharType.MINION), // wants t1 or m2, prioritized to be MINION
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[8])), // wants m1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 5 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 0f,
            selectedPriority = SelectedPriorities.TYPE,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments obey type priorities
        assert(assignments[players[0]]?.first == characters[9])
        assert(assignments[players[1]]?.first == characters[1])
        assert(assignments[players[2]]?.first == characters[2])
        assert(assignments[players[3]]?.first?.type == CharType.TOWNSFOLK)
        assert(assignments[players[4]]?.first == characters[10])
    }

    @Test
    fun testRoleSolverMaximumSatisfactionWithAlignmentPriority() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", "Outsider $i", CharType.OUTSIDER, icon = 0, ability = ""))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", "Minion $i", CharType.MINION, icon = 0, ability = ""))
        }
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))
        // 2. Setup 5 Players, each with 2 preferred characters
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0], characters[9]), alignmentPriority = CharAlignment.EVIL), // wants t1, prioritized to be EVIL
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[8])), // wants m1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 5 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 0f,
            selectedPriority = SelectedPriorities.ALIGNMENT,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments obey alignment priorities
        assert(assignments[players[0]]?.first?.alignment == CharAlignment.EVIL)
        assert(assignments[players[1]]?.first?.alignment == CharAlignment.GOOD)
        assert(assignments[players[2]]?.first?.alignment == CharAlignment.GOOD)
    }

    @Test
    fun testRoleSolverSurprise() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", "Townsfolk $i", CharType.TOWNSFOLK, icon = 0, ability = ""))
        }
        characters.add(Character("o1", "Drunk", CharType.OUTSIDER, icon = 0, ability = "", thinksTheyAre = listOf(CharType.TOWNSFOLK)))
        characters.add(Character("o2", "Outsider 2", CharType.OUTSIDER, icon = 0, ability = ""))
        (1..2).forEach { i ->
            characters.add(Character("m$i", "Minion $i", CharType.MINION, icon = 0, ability = ""))
        }
        characters.add(Character("d1", "Demon 1", CharType.DEMON, icon = 0, ability = ""))
        // 2. Setup 6 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[3])), // wants t4
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[8])), // wants m1
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 5 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            unselectableChance = 1f,
            selectedPriority = SelectedPriorities.NO_PRIORITIES,
            playerPriorityToggle = false
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assertNotNull(assignments)
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments obey alignment priorities
        assert(assignments[players[0]]?.first == characters[0])
        assert(assignments[players[1]]?.first == characters[6])
        assert(assignments[players[1]]?.second == characters[1])
        assert(assignments[players[2]]?.first == characters[2])
        assert(assignments[players[3]]?.first == characters[3])
        assert(assignments[players[4]]?.first == characters[8])
        assert(assignments[players[5]]?.first == characters[10])

    }

    fun printAssignments(assignments: Map<Player, Pair<Character, Character?>>) {
        assignments.forEach { (player, character) ->
            println("${player.name}: ${character.first.name} (thinks they are ${character.second?.name ?: "none"})")
        }
    }
}
