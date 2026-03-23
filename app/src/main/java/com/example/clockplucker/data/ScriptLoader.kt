package com.example.clockplucker.data

import org.json.JSONArray
import org.json.JSONObject

class ScriptLoader {
    /**
     * Parses a JSON string representing a script (either a list of IDs or full character objects).
     */
    fun parseScript(jsonString: String): Script {
        val characters = mutableListOf<Character>()
        var scriptName = "Unknown Script"
        var scriptAuthor = "Unknown Author"

        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val item = array.get(i)
                
                when (item) {
                    is JSONObject -> {
                        val id = item.optString("id")
                        
                        if (id == "_meta") {
                            scriptName = item.optString("name", scriptName)
                            scriptAuthor = item.optString("author", scriptAuthor)
                            continue
                        }
                        
                        if (id.isEmpty()) continue

                        val name = item.optString("name", formatIdToName(id))
                        val team = item.optString("team", item.optString("roleType", "townsfolk")).lowercase()
                        
                        val type = when (team) {
                            "townsfolk" -> CharType.TOWNSFOLK
                            "outsider" -> CharType.OUTSIDER
                            "minion" -> CharType.MINION
                            "demon" -> CharType.DEMON
                            else -> CharType.TOWNSFOLK
                        }

                        val ability = item.optString("ability", "")
                        
                        characters.add(Character(id = id,name = name, type = type, icon = 0, ability = ability))
                    }
                    is String -> {
                        val characterInfo = CharacterRepository.getCharacterInfo(item)
                        if(characterInfo != null) {
                            characters.add(characterInfo)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Script(name = scriptName, author = scriptAuthor, characters = characters)
    }

    private fun formatIdToName(id: String): String {
        return id.split("_", "-")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}
