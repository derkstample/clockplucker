package com.example.clockplucker.data

class PlayerCountLookup {
    fun getTownsfolk(players: Int): Int {
        return when(players){
            5 -> 3
            6 -> 3
            7 -> 5
            8 -> 5
            9 -> 5
            10 -> 7
            11 -> 7
            12 -> 7
            13 -> 9
            14 -> 9
            15 -> 9
            else -> -1
        }
    }

    fun getOutsiders(players: Int): Int {
        return when(players){
            5 -> 0
            6 -> 1
            7 -> 0
            8 -> 1
            9 -> 2
            10 -> 0
            11 -> 1
            12 -> 2
            13 -> 0
            14 -> 1
            15 -> 2
            else -> -1
        }
    }

    fun getMinions(players: Int): Int {
        return when(players){
            5 -> 1
            6 -> 1
            7 -> 1
            8 -> 1
            9 -> 1
            10 -> 2
            11 -> 2
            12 -> 2
            13 -> 3
            14 -> 3
            15 -> 3
            else -> -1
        }
    }

    fun getDemons(players: Int): Int {
        return when (players) {
            5 -> 1
            6 -> 1
            7 -> 1
            8 -> 1
            9 -> 1
            10 -> 1
            11 -> 1
            12 -> 1
            13 -> 1
            14 -> 1
            15 -> 1
            else -> -1
        }
    }
}