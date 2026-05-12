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

object CharacterTransformer {
    fun applyScriptwideRules(target: Character, script: List<Character>) {
        when (target.id) {
            "hermit" -> {
                val drunk = script.any { it.id == "drunk" }
                val lunatic = script.any { it.id == "lunatic" }
                target.thinksTheyAre = when {
                    drunk && lunatic -> listOf(CharType.DEMON)
                    drunk -> listOf(CharType.TOWNSFOLK)
                    lunatic -> listOf(CharType.DEMON)
                    else -> target.thinksTheyAre
                }
            }
        }
    }
    fun transformAlchemist(alchemist: Character, minion: Character, duplicatesEnabled: Boolean, jinx: String?) {
        val isNotInPlay = jinx?.contains("not-in-play") == true
        val isInPlay = jinx?.contains("in play") == true

        if (jinx == null || (!isNotInPlay && !isInPlay)) {
            alchemist.maxInstances = minion.maxInstances
            alchemist.dependsOn = minion.dependsOn
            alchemist.hardJinxedWith = if (duplicatesEnabled) minion.hardJinxedWith else minion.hardJinxedWith + minion.id
            alchemist.additiveModifiers = minion.additiveModifiers
            alchemist.overrideModifiers = minion.overrideModifiers
        } else if (isNotInPlay) {
            alchemist.hardJinxedWith = listOf(minion.id)
        } else {
            alchemist.dependsOn = minion.id
        }
    }
}