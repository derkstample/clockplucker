package com.example.clockplucker.data

import org.json.JSONArray
import org.json.JSONObject

class ScriptLoader {
    /**
     * Parses a JSON string representing a script (either a list of IDs or full character objects).
     */
    fun parseScript(jsonString: String): Script? {
        val selectableCharacters = mutableListOf<Character>()
        val excludedCharacters = mutableListOf<Character>()
        var scriptName = "Unknown Script" // todo: string extraction, multi-language custom script support??
        var scriptAuthor = "Unknown Author"

        val excludedTypes = listOf(CharType.FABLED, CharType.LORIC, CharType.TRAVELLER)

        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                when (val item = array.get(i)) {
                    is JSONObject -> {
                        val id = item.optString("id")
                        
                        if (id == "_meta") {
                            scriptName = item.optString("name", scriptName)
                            scriptAuthor = item.optString("author", scriptAuthor)
                            continue
                        }
                        
                        if (id.isEmpty()) continue

                        // BOTC SCRIPTS SUPPORT
                        val characterInfo = CharacterRepository.getCharacterInfo(id)
                        if(characterInfo != null) {
                            if (characterInfo.type in excludedTypes) {
                                excludedCharacters.add(characterInfo)
                            } else {
                                selectableCharacters.add(characterInfo)
                            }
                            continue
                        }

                        // BOOTLEGGER CHARACTER SUPPORT
                        val name = item.optString("name", formatIdToName(id))
                        val team = item.optString("team", item.optString("roleType", "townsfolk")).lowercase()
                        
                        val type = when (team) {
                            "townsfolk" -> CharType.TOWNSFOLK
                            "outsider" -> CharType.OUTSIDER
                            "minion" -> CharType.MINION
                            "demon" -> CharType.DEMON
                            "fabled" -> CharType.FABLED
                            "loric" -> CharType.LORIC
                            "traveller", "traveler" -> CharType.TRAVELLER
                            else -> CharType.TOWNSFOLK
                        }

                        val ability = item.optString("ability", "")
                        if (ability.isEmpty()) return null // not much use having a custom character if you didn't bother to give it an ability
                        // that check also gives good enough input validation

                        val character = Character(
                            id = id,
                            name = TextValue.Raw(name),
                            type = type,
                            ability = TextValue.Raw(ability)
                        )
                        if (type in excludedTypes) {
                            excludedCharacters.add(character)
                        } else {
                            selectableCharacters.add(character)
                        }
                    }
                    // SCRIPT TOOL JSON SUPPORT
                    is String -> {
                        val characterInfo = CharacterRepository.getCharacterInfo(item)
                        if(characterInfo != null) {
                            if (characterInfo.type in excludedTypes) {
                                excludedCharacters.add(characterInfo)
                            } else {
                                selectableCharacters.add(characterInfo)
                            }
                        } else {
                            return null
                        }
                    }
                }
            }
        } catch (_: Exception) {
            return null
        }
        return Script(
            name = scriptName, 
            author = scriptAuthor, 
            selectableCharacters = selectableCharacters, 
            excludedCharacters = excludedCharacters
        )
    }

    private fun formatIdToName(id: String): String {
        return id.split("_", "-")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}
