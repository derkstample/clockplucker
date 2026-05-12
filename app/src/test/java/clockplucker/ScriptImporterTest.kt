package clockplucker

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

import clockplucker.data.ScriptLoader
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

class ScriptImporterTest {

    private fun readTestResource(fileName: String): String {
        // The files are located in app/src/test/java/com/example/clockplucker/testScripts/bootlegger.json
        val file = File("src/test/java/com/example/clockplucker/testScripts/$fileName")
        if (!file.exists()) {
            throw IllegalArgumentException("Could not find test file: $fileName at ${file.absolutePath}")
        }
        return file.readText()
    }

    @Test
    fun testBootlegger() {
        val json = readTestResource("bootlegger.json")
        val script = ScriptLoader().parseScript(json)
        assertNotNull("bootlegger.json should return a Script object", script)
    }

    @Test
    fun testBotcScripts() {
        val json = readTestResource("botcScripts.json")
        val script = ScriptLoader().parseScript(json)
        assertNotNull("botcScripts.json should return a Script object", script)
    }

    @Test
    fun testScriptTool() {
        val json = readTestResource("scriptTool.json")
        val script = ScriptLoader().parseScript(json)
        assertNotNull("scriptTool.json should return a Script object", script)
    }

    @Test
    fun testInvalid() {
        val json = readTestResource("invalid.json")
        val script = ScriptLoader().parseScript(json)
        assertNull("invalid.json should return null", script)
    }

    @Test
    fun testReallyInvalid() {
        val json = readTestResource("reallyInvalid.json")
        val script = ScriptLoader().parseScript(json)
        assertNull("reallyInvalid.json should return null", script)
    }
}
