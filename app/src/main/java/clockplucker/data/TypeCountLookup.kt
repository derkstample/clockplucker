package clockplucker.data

//    Copyright 2026 Derek Rodriguez
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

data class Count(
    var townsfolk: Int = 0,
    var outsider: Int = 0,
    var minion: Int = 0,
    var demon: Int = 0
) {
    operator fun plus(other: Count): Count {
        return Count(
            other.townsfolk + townsfolk,
            other.outsider + outsider,
            other.minion + minion,
            other.demon + demon
        )
    }
    operator fun minus(other: Count): Count {
        return Count(
            townsfolk - other.townsfolk,
            outsider - other.outsider,
            minion - other.minion,
            demon - other.demon
        )
    }
}

class TypeCountLookup {
    fun getBaseCounts(playerCount: Int): Count {
        return when (playerCount) {
            5 -> Count(3,0,1,1)
            6 -> Count(3,1,1,1)
            7 -> Count(5,0,1,1)
            8 -> Count(5,1,1,1)
            9 -> Count(5,2,1,1)
            10 -> Count(7,0,2,1)
            11 -> Count(7,1,2,1)
            12 -> Count(7,2,2,1)
            13 -> Count(9,0,3,1)
            14 -> Count(9,1,3,1)
            15 -> Count(9,2,3,1)
            else -> Count(-1,-1,-1,-1)
        }
    }
}