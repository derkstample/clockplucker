package com.example.clockplucker.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.data.ScriptLoader

@Composable
fun ScriptScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val content =
                context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                    reader.readText()
                }
            content?.let { json ->
                val script = ScriptLoader().parseScript(json)
                viewModel.loadedScript = script

                // save local copy for quick select list
                val fileName = "script_${System.currentTimeMillis()}.json"
                context.openFileOutput(fileName, android.content.Context.MODE_PRIVATE).use { output ->
                    output.write(json.toByteArray())
                }
                viewModel.saveScriptToHistory(script.name, fileName)
            }
        }
    }

    SectionHeader("Script Select")
}
