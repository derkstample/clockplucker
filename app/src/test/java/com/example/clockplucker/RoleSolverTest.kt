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
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..3).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 3. Define starting count for 8 players: 5 Townsfolk, 1 Outsider, 1 Minion, 1 Demon
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
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
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

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
            baseCount = startingCount
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
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
    fun testRoleSolverMaximumSatisfactionWithAdditiveModifier() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("baron", TextValue.Raw("Baron"), type = CharType.MINION, icon = 0, ability = TextValue.Raw(""), additiveModifiers = listOf(Count(townsfolk = -2, outsider = 2))))
        characters.add(Character("m2", TextValue.Raw("Minion 2"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

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
            baseCount = startingCount
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
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
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

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
            selectedPriority = SelectedPriorities.TYPE
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
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
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

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
            selectedPriority = SelectedPriorities.ALIGNMENT
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
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
    fun testRoleSolverMaximumSatisfactionWithSurprise() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("o1", TextValue.Raw("Outsider 1"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        val drunk = Character("drunk", TextValue.Raw("Drunk"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw(""), thinksTheyAre = listOf(CharType.TOWNSFOLK))
        characters.add(drunk)
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Setup 6 Players, each with 2 preferred characters
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0], characters[8])), // wants t1 or m1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1], characters[8])), // wants t2 or m1
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2], characters[10])), // wants t3 or d1
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[9], characters[0])), // wants m2 or t1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[10], characters[1])),  // wants d1 or t2
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[2], characters[3])) // wants t3 or t4
        )

        // 3. Define starting count for 6 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            selectedPriority = SelectedPriorities.NO_PRIORITIES,
            surpriseChances = mapOf(drunk to 1f)
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments are optimal
        assert(assignments[players[0]]?.first?.id == "m1")
        assert(assignments[players[1]]?.first?.id == "t2")
        assert(assignments[players[2]]?.first?.id == "t3")
        assert(assignments[players[3]]?.first?.id == "drunk")
        assert(assignments[players[3]]?.second?.id == "t1")
        assert(assignments[players[4]]?.first?.id == "d1")
        assert(assignments[players[5]]?.first?.id == "t4")
    }

    @Test
    fun testRoleSolverSurprise() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        val drunk = Character("o1", TextValue.Raw("Drunk"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw(""), thinksTheyAre = listOf(CharType.TOWNSFOLK))
        characters.add(drunk)
        characters.add(Character("o2", TextValue.Raw("Outsider 2"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Setup 6 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[3])), // wants t4
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
            surpriseChances = mapOf(drunk to 1f)
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments add a surprise character
        assert(assignments[players[0]]?.first?.id == "t1")
        assert(assignments[players[1]]?.first?.id == "o1")
        assert(assignments[players[1]]?.second?.id == "t2")
        assert(assignments[players[2]]?.first?.id == "t3")
        assert(assignments[players[3]]?.first?.id == "t4")
        assert(assignments[players[4]]?.first?.id == "m1")
        assert(assignments[players[5]]?.first?.id == "d1")
    }

    @Test
    fun testRoleSolverMarionette() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$1", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        val marionette = Character("marionette", TextValue.Raw("Marionette"), type = CharType.MINION, icon = 0, ability = TextValue.Raw(""), thinksTheyAre = listOf(CharType.TOWNSFOLK))
        characters.add(marionette)

        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Setup 6 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[6])), // wants o1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[4])), // wants t5
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[10]))  // wants d1
        )

        // 3. Define starting count for 6 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount,
            surpriseChances = mapOf(marionette to 1f)
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        assertEquals(players.size, assignments.size)

        // Check if all players are assigned a character
        players.forEach { player ->
            assertNotNull("Player ${player.name} should be assigned a character", assignments[player])
        }

        // Verify the assignments obey marionette rules (must neighbor the demon)
        assert(assignments[players[0]]?.first == characters[0])
        assert(assignments[players[1]]?.first == characters[1])
        assert(assignments[players[2]]?.first == characters[2])
        assert(assignments[players[3]]?.first == characters[6])
        assert(assignments[players[4]]?.first == characters[9])
        assert(assignments[players[4]]?.second == characters[4])
        assert(assignments[players[5]]?.first == characters[10])
    }

    @Test
    fun testRoleSolverMultipleInstances() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        // One townsfolk with maxInstances = 3
        characters.add(Character("t1", TextValue.Raw("Townsfolk 1"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw(""), maxInstances = 3))
        (2..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Setup 6 players, three of which want "Townsfolk 1"
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])),
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[0])),
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[0])),
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[6])), // wants o1
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[8])), // wants m1
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[10])) // wants d1
        )

        // 3. Define starting count for 6 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        assertEquals(players.size, assignments.size)

        // Assert that the first three players were assigned Townsfolk 1
        assertEquals(characters[0], assignments[players[0]]?.first)
        assertEquals(characters[0], assignments[players[1]]?.first)
        assertEquals(characters[0], assignments[players[2]]?.first)
    }

    @Test
    fun testRoleSolverMaximumSatisfactionWithOverrideModifier() = runBlocking {
        // 1. Create script with 12 characters
        val characters = mutableListOf<Character>()
        (1..7).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        // Demon with override modifier of CharType.MINION
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw(""), overrideModifiers = listOf(CharType.MINION)))

        // 2. Setup 5 players, each with one character in their selectedChars list.
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])), // wants t1
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])), // wants t2
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])), // wants t3
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[9])), // wants m1 (but shouldn't get it)
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[11]))  // wants d1
        )

        // 3. Define starting count for 5 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        // 4. Initialize RoleSolver
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount
        )

        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        assertEquals(players.size, assignments.size)

        // Verify no Minions are assigned
        val assignedRoles = assignments.values
        assertEquals(0, assignedRoles.count { it.first.type == CharType.MINION })

        // Assert that we have an additional townsfolk or outsider (total 4 good roles for 5 players if 1 demon)
        assertEquals(4, assignedRoles.count { it.first.type == CharType.TOWNSFOLK || it.first.type == CharType.OUTSIDER })
        assertEquals(1, assignedRoles.count { it.first.type == CharType.DEMON })
    }

    @Test
    fun testRoleSolverSurpriseFiftyPercent() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        val drunk = Character("test", TextValue.Raw("Drunk"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw(""), thinksTheyAre = listOf(CharType.TOWNSFOLK))
        characters.add(drunk)
        characters.add(Character("o2", TextValue.Raw("Outsider 2"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Setup 6 Players, each with 1 preferred character
        val players = listOf(
            Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(characters[0])),
            Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(characters[1])),
            Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(characters[2])),
            Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(characters[3])),
            Player(id = UUID.randomUUID(), name = "Player 5", selectedChars = listOf(characters[8])),
            Player(id = UUID.randomUUID(), name = "Player 6", selectedChars = listOf(characters[10]))
        )

        // 3. Define starting count for 6 players
        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        var drunkCount = 0
        repeat(10000) {
            val solver = RoleSolver(
                players = players,
                availableChars = characters,
                baseCount = startingCount,
                surpriseChances = mapOf(drunk to 0.5f)
            )
            val assignments = solver.optimizeAssignments()
            if (assignments.values.any { it.first.id == "test" }) {
                drunkCount++
            }
        }
        assert(drunkCount in 4000..6000) { "Drunk count was $drunkCount, expected 4000..6000" }
    }

    @Test
    fun testRoleSolverAtheist() = runBlocking {
        // 1. Create script with 11 characters
        val characters = mutableListOf<Character>()
        val atheist = Character("atheist", TextValue.Raw("Atheist"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw(""), overrideModifiers = listOf(CharType.MINION, CharType.DEMON))
        characters.add(atheist)
        (1..7).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        val minion1 = Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw(""))
        val minion2 = Character("m2", TextValue.Raw("Minion 2"), type = CharType.MINION, icon = 0, ability = TextValue.Raw(""))
        val demon = Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw(""))
        characters.add(minion1)
        characters.add(minion2)
        characters.add(demon)

        // 2. Setup 8 Players
        val players = mutableListOf<Player>()
        players.add(Player(id = UUID.randomUUID(), name = "Player 1", selectedChars = listOf(atheist), historyWeight = 3))
        players.add(Player(id = UUID.randomUUID(), name = "Player 2", selectedChars = listOf(minion1)))
        players.add(Player(id = UUID.randomUUID(), name = "Player 3", selectedChars = listOf(minion2)))
        players.add(Player(id = UUID.randomUUID(), name = "Player 4", selectedChars = listOf(demon)))
        (5..8).forEach { i ->
            players.add(Player(id = UUID.randomUUID(), name = "Player $i"))
        }

        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = startingCount
        )

        val assignments = solver.optimizeAssignments()
        val assignedRoles = assignments.values.map { it.first }

        // Assert that no evil characters are assigned in the solution
        assert(assignments.isNotEmpty())
        assert(assignedRoles.contains(atheist))
        assert(assignments.values.none { it.first.alignment == CharAlignment.EVIL })
    }

    @Test
    fun testRoleSolverDependency() = runBlocking {
        // Character t2 depends on t1
        val t1 = Character("t1", TextValue.Raw("Townsfolk 1"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw(""))
        val t2 = Character("t2", TextValue.Raw("Townsfolk 2"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw(""), dependsOn = "t1")
        val otherChars = (3..6).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("o1", TextValue.Raw("Outsider 1"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")),
                    Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")),
                    Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))
        val characters = listOf(t1, t2) + otherChars

        // 5 players: 3 Townsfolk, 0 Outsider, 1 Minion, 1 Demon
        val players = (1..5).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        // Player 1 specifically wants t2
        val updatedPlayers = players.toMutableList()
        updatedPlayers[0] = updatedPlayers[0].copy(selectedChars = listOf(t2))

        val startingCount = TypeCountLookup().getBaseCounts(players.size)

        val solver = RoleSolver(
            players = updatedPlayers,
            availableChars = characters,
            baseCount = startingCount
        )

        val assignments = solver.optimizeAssignments()

        assert(assignments.isNotEmpty())
        val assignedRoles = assignments.values.map { it.first }
        // Assert t2 is assigned because Player 1 wants it
        assert(assignedRoles.contains(t2))
        // Assert t1 is ALSO assigned because t2 depends on it
        assert(assignedRoles.contains(t1))
    }

    @Test
    fun testRoleSolverAutoSentinel() = runBlocking {
        // Base count for 6 players: 3 Townsfolk, 1 Outsider, 1 Minion, 1 Demon
        val characters = (1..6).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..3).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")),
                    Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..6).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val startingCount = TypeCountLookup().getBaseCounts(6)

        // 1. No players prefer an outsider -> assert 0 outsiders
        val players0 = basePlayers.map { p -> p.copy(selectedChars = listOf(characters[0], characters[1], characters[2], characters[3])) } // Everyone wants Townsfolk
        val assignments0 = RoleSolver(players0, characters, startingCount, autoSentinel = true).optimizeAssignments()
        assert(assignments0.isNotEmpty())
        assertEquals(0, assignments0.values.count { it.first.type == CharType.OUTSIDER })

        // 2. One player prefers an outsider -> assert 1 outsider
        val players1 = basePlayers.toMutableList()
        players1[0] = players1[0].copy(selectedChars = listOf(characters[6])) // Player 1 wants o1
        // Others want Townsfolk to provide pressure against more outsiders
        (1..5).forEach { i -> players1[i] = players1[i].copy(selectedChars = listOf(characters[0], characters[1], characters[2])) }
        val assignments1 = RoleSolver(players1, characters, startingCount, autoSentinel = true).optimizeAssignments()
        assert(assignments1.isNotEmpty())
        assertEquals(1, assignments1.values.count { it.first.type == CharType.OUTSIDER })

        // 3. Two players prefer outsiders -> assert 2 outsiders
        val players2 = basePlayers.toMutableList()
        players2[0] = players2[0].copy(selectedChars = listOf(characters[6])) // Player 1 wants o1
        players2[1] = players2[1].copy(selectedChars = listOf(characters[7])) // Player 2 wants o2
        (2..5).forEach { i -> players2[i] = players2[i].copy(selectedChars = listOf(characters[0], characters[1], characters[2])) }
        val assignments2 = RoleSolver(players2, characters, startingCount, autoSentinel = true).optimizeAssignments()
        assert(assignments2.isNotEmpty())
        assertEquals(2, assignments2.values.count { it.first.type == CharType.OUTSIDER })

        // 4. Three players prefer outsiders -> assert 2 outsiders (capped by sentinel limit ±1)
        val players3 = basePlayers.toMutableList()
        players3[0] = players3[0].copy(selectedChars = listOf(characters[6])) // Player 1 wants o1
        players3[1] = players3[1].copy(selectedChars = listOf(characters[7])) // Player 2 wants o2
        players3[2] = players3[2].copy(selectedChars = listOf(characters[8])) // Player 3 wants o3
        val assignments3 = RoleSolver(players3, characters, startingCount, autoSentinel = true).optimizeAssignments()
        assert(assignments3.isNotEmpty())
        assertEquals(2, assignments3.values.count { it.first.type == CharType.OUTSIDER })
    }

    @Test
    fun testRoleSolverXaan() = runBlocking {
        // Create Xaan character with its unique additive modifiers
        val xaan = Character(
            id = "xaan",
            name = TextValue.Raw("Xaan"),
            type = CharType.MINION,
            icon = 0,
            ability = TextValue.Raw("On night X, all Townsfolk are poisoned until dusk. [X Outsiders]"),
            additiveModifiers = listOf(
                Count(townsfolk = 4, outsider = -4),
                Count(townsfolk = 3, outsider = -3),
                Count(townsfolk = 2, outsider = -2),
                Count(townsfolk = 1, outsider = -1),
                Count(),
                Count(townsfolk = -1, outsider = 1),
                Count(townsfolk = -2, outsider = 2),
                Count(townsfolk = -3, outsider = 3),
                Count(townsfolk = -4, outsider = 4)
            )
        )

        // Script: 1 Demon, 1 Xaan (Minion), 4 Outsiders, many Townsfolk
        val characters = listOf(xaan) +
                (1..10).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..4).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..7).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        // 7 players: 5 Townsfolk, 0 Outsiders, 1 Minion, 1 Demon.
        val baseCount7 = Count(townsfolk = 5, outsider = 0, minion = 1, demon = 1)

        for (numPreferredOutsiders in 0..4) {
            val players = basePlayers.toMutableList()
            // Player 1 always wants Xaan
            players[0] = players[0].copy(selectedChars = listOf(xaan))
            // Player 2 always wants the Demon
            players[1] = players[1].copy(selectedChars = listOf(characters.last()))

            // Assign unique outsider preferences to some players
            for (i in 0 until numPreferredOutsiders) {
                players[i + 2] = players[i + 2].copy(selectedChars = listOf(characters[11 + i])) // Outsiders start at index 11
            }
            // Others want unique Townsfolk (indices 1..10)
            for (i in numPreferredOutsiders until 5) {
                players[i + 2] = players[i + 2].copy(selectedChars = listOf(characters[i + 1]))
            }

            val assignments = RoleSolver(players, characters, baseCount7).optimizeAssignments()
            val actualOutsiders = assignments.values.count { it.first.type == CharType.OUTSIDER }

            assert(assignments.isNotEmpty())
            assertEquals("Failed for $numPreferredOutsiders preferred outsiders", numPreferredOutsiders, actualOutsiders)
        }
    }

    @Test
    fun testRoleSolverBalloonist() = runBlocking {
        val balloonist = Character(
            id = "balloonist",
            name = TextValue.Raw("Balloonist"),
            type = CharType.TOWNSFOLK,
            icon = 0,
            ability = TextValue.Raw("[+0 or +1 Outsider]"),
            additiveModifiers = listOf(Count(), Count(townsfolk = -1, outsider = 1))
        )
        val characters = listOf(balloonist) +
                (1..10).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..2).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")),
                    Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..7).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount7 = Count(townsfolk = 5, outsider = 0, minion = 1, demon = 1)

        // Case 1: Players prefer Townsfolk -> should pick +0 Outsider mod
        val players0 = basePlayers.toMutableList()
        players0[0] = players0[0].copy(selectedChars = listOf(balloonist))
        (1..6).forEach { i -> players0[i] = players0[i].copy(selectedChars = listOf(characters[i])) } // t1..t6
        val assignments0 = RoleSolver(players0, characters, baseCount7).optimizeAssignments()
        assert(assignments0.isNotEmpty())
        assertEquals(0, assignments0.values.count { it.first.type == CharType.OUTSIDER })

        // Case 2: One player prefers an Outsider -> should pick +1 Outsider mod
        val players1 = basePlayers.toMutableList()
        players1[0] = players1[0].copy(selectedChars = listOf(balloonist))
        (1..6).forEach { i -> players1[i] = players1[i].copy(selectedChars = listOf(characters[i])) } // t1..t6
        players1[1] = players1[1].copy(selectedChars = listOf(characters[11])) // wants o1
        val assignments1 = RoleSolver(players1, characters, baseCount7).optimizeAssignments()
        assert(assignments1.isNotEmpty())
        assertEquals(1, assignments1.values.count { it.first.type == CharType.OUTSIDER })
    }

    @Test
    fun testRoleSolverHeretic() = runBlocking {
        val heretic = Character(
            id = "heretic",
            name = TextValue.Raw("Heretic"),
            type = CharType.OUTSIDER,
            icon = 0,
            ability = TextValue.Raw("..."),
            hardJinxedWith = listOf("lleech")
        )
        val lleech = Character("lleech", TextValue.Raw("Lleech"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw(""))
        val otherDemon = Character("imp", TextValue.Raw("Imp"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw(""))
        val characters = listOf(heretic, lleech, otherDemon) +
                (1..6).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..6).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount6 = Count(townsfolk = 3, outsider = 1, minion = 1, demon = 1)

        // Try to force both Heretic and Lleech
        val players = basePlayers.toMutableList()
        players[0] = players[0].copy(selectedChars = listOf(heretic))
        players[1] = players[1].copy(selectedChars = listOf(lleech))
        // Unique Townsfolk for others
        (2..5).forEach { i -> players[i] = players[i].copy(selectedChars = listOf(characters[i+1])) }

        val assignments = RoleSolver(players, characters, baseCount6).optimizeAssignments()
        val assignedRoles = assignments.values.map { it.first }

        // Assert that they ARE NOT both in play
        assert(assignments.isNotEmpty())
        assert(!(assignedRoles.contains(heretic) && assignedRoles.contains(lleech)))
    }

    @Test
    fun testRoleSolverHermit() = runBlocking {
        val hermit = Character(
            id = "hermit",
            name = TextValue.Raw("Hermit"),
            type = CharType.OUTSIDER,
            icon = 0,
            ability = TextValue.Raw("[-0 or -1 Outsider]"),
            additiveModifiers = listOf(Count(), Count(townsfolk = 1, outsider = -1))
        )
        val characters = listOf(hermit) +
                (1..10).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..2).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("m1", TextValue.Raw("Minion 1"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")),
                    Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..9).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount8 = TypeCountLookup().getBaseCounts(basePlayers.size)

        // Case 1: Player prefers an Outsider -> should pick +0 Outsider mod (keeping base 2)
        val players1 = basePlayers.toMutableList()
        players1[0] = players1[0].copy(selectedChars = listOf(hermit))
        for (i in 1..7) {
            players1[i] = players1[i].copy(selectedChars = listOf(characters[i])) // t1..t7
        }
        players1[1] = players1[1].copy(selectedChars = listOf(characters[11])) // wants o1
        val assignments1 = RoleSolver(players1, characters, baseCount8).optimizeAssignments()
        assert(assignments1.isNotEmpty())
        assertEquals(2, assignments1.values.count { it.first.type == CharType.OUTSIDER })

        // Case 2: Players prefer Townsfolk -> should pick -1 Outsider mod
        val players0 = basePlayers.toMutableList()
        players0[0] = players0[0].copy(selectedChars = listOf(hermit))
        for (i in 1..7) {
            players0[i] = players0[i].copy(selectedChars = listOf(characters[i])) // t1..t7
        }
        val assignments0 = RoleSolver(players0, characters, baseCount8).optimizeAssignments()
        assert(assignments0.isNotEmpty())
        assertEquals(1, assignments0.values.count { it.first.type == CharType.OUTSIDER })
    }

    @Test
    fun testRoleSolverGodfather() = runBlocking {
        val godfather = Character(
            id = "godfather",
            name = TextValue.Raw("Godfather"),
            type = CharType.MINION,
            icon = 0,
            ability = TextValue.Raw("[-1 or +1 Outsider]"),
            additiveModifiers = listOf(Count(townsfolk = -1, outsider = 1), Count(townsfolk = 1, outsider = -1))
        )
        val characters = listOf(godfather) +
                (1..10).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..2).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                listOf(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        val basePlayers = (1..8).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount8 = TypeCountLookup().getBaseCounts(basePlayers.size)

        // Case 1: Players prefer Outsiders -> should pick +1 Outsider mod (target 2)
        val players2 = basePlayers.toMutableList()
        players2[0] = players2[0].copy(selectedChars = listOf(godfather))
        for (i in 1..7) {
            players2[i] = players2[i].copy(selectedChars = listOf(characters[i])) // t1..t7
        }
        players2[1] = players2[1].copy(selectedChars = listOf(characters[11])) // wants o1
        players2[2] = players2[2].copy(selectedChars = listOf(characters[12])) // wants o2
        val assignments2 = RoleSolver(players2, characters, baseCount8).optimizeAssignments()
        assert(assignments2.isNotEmpty())
        assertEquals(2, assignments2.values.count { it.first.type == CharType.OUTSIDER })

        // Case 2: Players prefer Townsfolk -> should pick -1 Outsider mod (target 0)
        val players0 = basePlayers.toMutableList()
        players0[0] = players0[0].copy(selectedChars = listOf(godfather))
        for (i in 1..7) {
            players0[i] = players0[i].copy(selectedChars = listOf(characters[i])) // t1..t7
        }
        val assignments0 = RoleSolver(players0, characters, baseCount8).optimizeAssignments()
        assert(assignments0.isNotEmpty())
        assertEquals(0, assignments0.values.count { it.first.type == CharType.OUTSIDER })
    }

    @Test
    fun testRoleSolverKazali() = runBlocking {
        val kazali = Character(
            id = "kazali",
            name = TextValue.Raw("Kazali"),
            type = CharType.DEMON,
            icon = 0,
            ability = TextValue.Raw("[No Minions]"),
            overrideModifiers = listOf(CharType.MINION)
        )
        val characters =
            (1..10).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                    (1..2).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                    (1..2).map { i -> Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")) } +
                    listOf(kazali)

        val basePlayers = (1..6).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount6 = TypeCountLookup().getBaseCounts(basePlayers.size)

        val players = basePlayers.toMutableList()
        players[0] = players[0].copy(selectedChars = listOf(kazali))

        val assignments = RoleSolver(players, characters, baseCount6).optimizeAssignments()
        val assignedRoles = assignments.values.map { it.first }

        // Assert Kazali is assigned and NO minions are assigned
        assert(assignments.isNotEmpty())
        assert(assignedRoles.contains(kazali))
        assertEquals(0, assignedRoles.count { it.type == CharType.MINION })
    }

    @Test
    fun testRoleSolverLegion() = runBlocking {
        val legion = Character(
            id = "legion",
            name = TextValue.Raw("Legion"),
            type = CharType.DEMON,
            icon = 0,
            ability = TextValue.Raw("[+some Legion, no Minions]"),
            overrideModifiers = listOf(CharType.MINION),
            maxInstances = 15
        )
        val otherDemon = Character("imp", TextValue.Raw("Imp"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw(""))
        val characters = (1..8).map { i -> Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")) } +
                (1..4).map { i -> Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")) } +
                (1..4).map { i -> Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")) } +
                listOf(legion, otherDemon)

        val players = (1..10).map { i -> Player(id = UUID.randomUUID(), name = "Player $i") }
        val baseCount10 = TypeCountLookup().getBaseCounts(players.size) // 7 TF, 0 OUT, 2 MIN, 1 DEM

        // Case 1: 3 players select Legion, remainder selects townsfolk
        val players1 = players.mapIndexed { index, player ->
            if (index < 3) player.copy(selectedChars = listOf(legion)) else player.copy(selectedChars = listOf(characters[index]))
        }
        val assignments1 = RoleSolver(players1, characters, baseCount10).optimizeAssignments()

        assert(assignments1.isNotEmpty())
        val legionCount1 = assignments1.values.count { it.first.id == "legion" }
        assert(legionCount1 >= 5)
        assertEquals(0, assignments1.values.count { it.first.type == CharType.MINION })
        assertEquals(0, assignments1.values.count { it.first.type == CharType.DEMON && it.first.id != "legion" })

        // Case 2: 6 players select Legion, remainder selects townsfolk
        val players2 = players.mapIndexed { index, player ->
            if (index < 6) player.copy(selectedChars = listOf(legion)) else player.copy(selectedChars = listOf(characters[index]))
        }
        val assignments2 = RoleSolver(players2, characters, baseCount10).optimizeAssignments()
        assert(assignments2.isNotEmpty())
        val legionCount2 = assignments2.values.count { it.first.id == "legion" }
        assertEquals(6, legionCount2)
        assertEquals(0, assignments2.values.count { it.first.type == CharType.MINION })
        assertEquals(0, assignments1.values.count { it.first.type == CharType.DEMON && it.first.id != "legion" })

        // Case 3: 9 players select Legion, remainder selects townsfolk
        val players3 = players.mapIndexed { index, player ->
            if (index < 9) player.copy(selectedChars = listOf(legion)) else player.copy(selectedChars = listOf(characters[index]))
        }
        val assignments3 = RoleSolver(players3, characters, baseCount10).optimizeAssignments()
        assert(assignments3.isNotEmpty())
        val legionCount3 = assignments3.values.count { it.first.id == "legion" }
        assertEquals(7, legionCount3) // Max is (10 * 0.75) = 7
        assertEquals(0, assignments3.values.count { it.first.type == CharType.MINION })
        assertEquals(0, assignments1.values.count { it.first.type == CharType.DEMON && it.first.id != "legion" })
    }

    @Test
    fun testRoleSolverPope() = runBlocking {
        // 1. Create a list of 6 townsfolk, 2 outsiders, 2 minions, and 1 demon.
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Create a list of 8 players with preferences as described.
        val players = (1..8).map { i -> Player(id = UUID.randomUUID(), name = "Player $i", selectedChars = listOf(characters[0])) }

        val baseCount = TypeCountLookup().getBaseCounts(players.size)

        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = baseCount,
            containsPope = true
        )
        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        val assignedRoles = assignments.values.map { it.first }

        // Assert that there are 5 of the same townsfolk
        assertEquals(5, assignedRoles.count { it.id == "t1" })
        // Assert a single outsider, single minion, and a single demon assigned
        assertEquals(1, assignedRoles.count { it.type == CharType.OUTSIDER })
        assertEquals(1, assignedRoles.count { it.type == CharType.MINION })
        assertEquals(1, assignedRoles.count { it.type == CharType.DEMON })
    }

    @Test
    fun testRoleSolverPriorities() = runBlocking {
        // 1. Create a list of 6 townsfolk, 2 outsiders, 2 minions, and 1 demon.
        val characters = mutableListOf<Character>()
        (1..6).forEach { i ->
            characters.add(Character("t$i", TextValue.Raw("Townsfolk $i"), type = CharType.TOWNSFOLK, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("o$i", TextValue.Raw("Outsider $i"), type = CharType.OUTSIDER, icon = 0, ability = TextValue.Raw("")))
        }
        (1..2).forEach { i ->
            characters.add(Character("m$i", TextValue.Raw("Minion $i"), type = CharType.MINION, icon = 0, ability = TextValue.Raw("")))
        }
        characters.add(Character("d1", TextValue.Raw("Demon 1"), type = CharType.DEMON, icon = 0, ability = TextValue.Raw("")))

        // 2. Create a list of 8 players with preferences as described.
        val players = (1..8).map { i ->
            Player(id = UUID.randomUUID(),
                name = "Player $i",
                selectedChars = when (i) {
                    1 -> listOf(characters[1], characters[0], characters[6]) // t2 or t1 or o1
                    2 -> listOf(characters[2], characters[1], characters[3]) // t3 or t2 or t4
                    3 -> listOf(characters[3], characters[2], characters[10]) // t4 or t3 or d1
                    4 -> listOf(characters[4], characters[3], characters[7]) // t5 or t4 or o2
                    5 -> listOf(characters[5], characters[4], characters[8]) // t6 or t5 or m1
                    6 -> listOf(characters[7], characters[6], characters[0]) // o2 or o1 or t1
                    7 -> listOf(characters[9], characters[8], characters[2]) // m2 or m1 or t3
                    8 -> listOf(characters[10], characters[8], characters[1]) // d1 or m1 or t2
                    else -> emptyList()
                }
            )
        }

        // 5, 1, 1, 1
        val baseCount = TypeCountLookup().getBaseCounts(players.size)

        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = baseCount,
            playerPriorityToggle = true
        )
        // 5. Solve
        val assignments = solver.optimizeAssignments()

        // 6. Verify results
        assert(assignments.isNotEmpty())
        val assignedRoles = assignments.values.map { it.first }

        // Assert that list order influenced assignments correctly
        assert(assignments[players[0]]?.first?.id == "t2")
        assert(assignments[players[1]]?.first?.id == "t3")
        assert(assignments[players[2]]?.first?.id == "t4")
        assert(assignments[players[3]]?.first?.id == "t5")
        assert(assignments[players[4]]?.first?.id == "t6")
        assert(assignments[players[5]]?.first?.id == "o2")
        assert(assignments[players[6]]?.first?.id == "m2")
        assert(assignments[players[7]]?.first?.id == "d1")
    }

    fun printAssignments(assignments: Map<Player, Pair<Character, Character?>>) {
        if (assignments.isEmpty()) println("No assignments found")
        assignments.forEach { (player, character) ->
            println("${player.name}: ${character.first.name} (thinks they are ${character.second?.name ?: "none"})")
            println("Selected characters: ${player.selectedChars.map { it.name }}")
        }
    }
}
