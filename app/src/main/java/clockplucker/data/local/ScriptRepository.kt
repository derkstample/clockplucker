package clockplucker.data.local

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

import kotlinx.coroutines.flow.Flow

class ScriptRepository(private val scriptDao: ScriptDao) {
    val allScripts: Flow<List<SavedScript>> = scriptDao.getAllScripts()

    suspend fun insert(script: SavedScript) {
        scriptDao.insertScript(script)
    }

    suspend fun delete(script: SavedScript) {
        scriptDao.deleteScript(script)
    }

    suspend fun update(script: SavedScript) {
        scriptDao.updateScript(script)
    }

    suspend fun updateLastAccessed(name: String, author: String) {
        scriptDao.updateLastAccessed(name, author)
    }
}
