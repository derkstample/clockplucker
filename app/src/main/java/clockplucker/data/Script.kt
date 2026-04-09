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
