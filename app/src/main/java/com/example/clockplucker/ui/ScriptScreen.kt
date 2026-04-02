package com.example.clockplucker.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.data.Script
import com.example.clockplucker.data.ScriptLoader
import com.example.clockplucker.data.local.SavedScript
import com.example.clockplucker.drawStableVerticalScrollbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScriptScreen(
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val savedScripts by viewModel.savedScripts.collectAsState()
    val loadedScript = viewModel.loadedScript

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
                
                // save local copy for quick select list
                val fileName = "script_${System.currentTimeMillis()}.json"
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                    output.write(json.toByteArray())
                }
                viewModel.saveScriptToHistory(script, fileName)
                viewModel.loadedScript = script
            }
        }
    }

    val filteredScripts = remember(savedScripts, loadedScript) {
        savedScripts.filter { it.name != loadedScript?.name || it.author != loadedScript.author }
    }

    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = null,
                onNext = onNext,
                progress = 1,
                total = 3,
                nextEnabled = loadedScript != null
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SectionHeader(
                text = "SELECT SCRIPT",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            SelectedScriptArea(
                modifier = Modifier.padding(horizontal = 16.dp),
                loadedScript = loadedScript,
                launcher = launcher,
                savedScripts = savedScripts,
                onNext = onNext
            )

            SectionHeader(
                text = "SAVED SCRIPTS",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawStableVerticalScrollbar(state = listState)
            ) {
                itemsIndexed(filteredScripts, key = { _, script -> script.id }) { index, savedScript ->
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text(text = "Delete Script") },
                            text = { Text(text = "Are you sure you want to delete \"${savedScript.name}\"?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.deleteScript(savedScript)
                                        showDeleteDialog = false
                                    }
                                ) {
                                    Text(text = "Delete", style = MaterialTheme.typography.bodyMedium)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text(text = "Cancel", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        )
                    }

                    ScriptBox(
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 16.dp),
                        savedScript = savedScript,
                        onClick = {
                            viewModel.loadSavedScript(context, savedScript)
                        },
                        onDeleteClick = { showDeleteDialog = true }
                    )
                    if (index < filteredScripts.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectedScriptArea(
    modifier: Modifier = Modifier,
    loadedScript: Script?,
    launcher: ActivityResultLauncher<String>,
    savedScripts: List<SavedScript>,
    onNext: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = loadedScript != null, onClick = onNext),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = loadedScript,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                    },
                    label = "SelectedScriptAnimation"
                ) { targetScript ->
                    if (targetScript != null) {
                        Column {
                            val savedScript = savedScripts.find { 
                                it.name == targetScript.name && it.author == targetScript.author 
                            }
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = targetScript.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.alignByBaseline()
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "by ${targetScript.author}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
                            val dateAdded = savedScript?.dateAdded ?: System.currentTimeMillis()
                            val dateStr = dateFormat.format(Date(dateAdded))

                            Text(
                                text = "Added $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Text(
                            text = "No script selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            ScriptImportButton(launcher = launcher)
        }
    }
}

@Composable
fun ScriptImportButton(launcher: ActivityResultLauncher<String>){
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { launcher.launch("application/json") },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Script"
        )
    }
}

@Composable
fun ScriptDeleteButton(onClick: () -> Unit){
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove Script"
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScriptBox(
    modifier: Modifier = Modifier,
    savedScript: SavedScript, 
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = savedScript.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alignByBaseline()
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "by ${savedScript.author}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alignByBaseline()
                        )
                    }
                }
                ScriptDeleteButton(onClick = onDeleteClick)
            }
            Spacer(modifier = Modifier.height(4.dp))
            
            val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
            val dateStr = dateFormat.format(Date(savedScript.dateAdded))
            val relativeTime = formatRelativeTime(savedScript.lastAccessed)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Added $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Last Played $relativeTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    if (diff < 60000) return "just now"

    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = days / 365

    return when {
        years > 0 -> "$years year${if (years > 1) "s" else ""} ago"
        months > 0 -> "$months month${if (months > 1) "s" else ""} ago"
        days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
        hours > 0 -> {
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) {
                "$hours hour${if (hours > 1) "s" else ""} $remainingMinutes minute${if (remainingMinutes > 1) "s" else ""} ago"
            } else {
                "$hours hour${if (hours > 1) "s" else ""} ago"
            }
        }
        else -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
    }
}
