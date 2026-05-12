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

import java.util.UUID

data class Script(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val author: String,
    val selectableCharacters: List<Character> = emptyList(),
    val excludedCharacters: List<Character> = emptyList()
) {
    val allCharacters: List<Character> get() = selectableCharacters + excludedCharacters
    val containsSentinel: Boolean get() = excludedCharacters.any { it.id == "sentinel" }
    val containsPope: Boolean get() = excludedCharacters.any { it.id == "pope" }
    val containsSurprises: Boolean get() = selectableCharacters.any { !it.thinksTheyAre.isEmpty() }
    val containsAlchemist: Boolean get() = selectableCharacters.any { it.id == "alchemist" }
}
