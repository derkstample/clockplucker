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

data class Player(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val typePriority: CharType? = null, // we don't always need to prioritize types
    val alignmentPriority: CharAlignment? = null, // see above
    val selectedChars: List<Character> = emptyList(),
    val historyWeight: Int = 1
)