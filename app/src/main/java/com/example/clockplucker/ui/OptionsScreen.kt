package com.example.clockplucker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clockplucker.HelpButton
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NInputField
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SectionHeader

@Composable
fun OptionsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = onBack,
                onNext = onNext,
                progress = 2
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }
    }
}

@Composable
fun GardeningOptionsButton(
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Button(
        onClick = { onToggle() },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gardening Options",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            val gardeningRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(gardeningRotation)
            )
        }
    }
}

@Composable
fun AnimatedGardeningOptionsContainer(
    isVisible: Boolean,

    selectedMode: Int,
    onModeChange: (Int) -> Unit,

    alignmentN: String,
    onAlignmentNChange: (String) -> Unit,

    typeN: String,
    onTypeNChange: (String) -> Unit,

    selectedPriority: Int,
    onPriorityChange: (Int) -> Unit,

    gardeningToggle: Boolean,
    onGardeningToggleChange: (Boolean) -> Unit,

    onHelpDialogTextChange: (String) -> Unit,

    alignmentFocusRequester: FocusRequester,
    typeFocusRequester: FocusRequester
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            SectionHeader("MODE")

            val modes = listOf(
                "No Restrictions" to "Players can select any number of preferred characters.",
                "n Of Each Alignment" to "Players can select up to n character(s) of each alignment (Good / Evil).",
                "n Of Each Type" to "Players can select up to n character(s) of each type (Townsfolk / Outsider / Minion / Demon)."
            )

            modes.forEachIndexed { index, pair ->
                val i = index + 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onModeChange(i)
                                if (i == 2) alignmentFocusRequester.requestFocus()
                                if (i == 3) typeFocusRequester.requestFocus()
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedMode == i,
                            onClick = {
                                onModeChange(i)
                                if (i == 2) alignmentFocusRequester.requestFocus()
                                if (i == 3) typeFocusRequester.requestFocus()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        when (i) {
                            2 -> {
                                NInputField(
                                    value = alignmentN,
                                    onValueChange = onAlignmentNChange,
                                    focusRequester = alignmentFocusRequester,
                                    onFocus = { onModeChange(2) }
                                )
                                Text(text = " Of Each Alignment")
                            }

                            3 -> {
                                NInputField(
                                    value = typeN,
                                    onValueChange = onTypeNChange,
                                    focusRequester = typeFocusRequester,
                                    onFocus = { onModeChange(3) }
                                )
                                Text(text = " Of Each Type")
                            }

                            else -> {
                                Text(text = pair.first)
                            }
                        }
                    }

                    val explanation = when (i) {
                        2 -> pair.second.replace(
                            " n ",
                            " ${if (alignmentN.isEmpty()) "n" else alignmentN} "
                        )

                        3 -> pair.second.replace(
                            " n ",
                            " ${if (typeN.isEmpty()) "n" else typeN} "
                        )

                        else -> pair.second
                    }
                    HelpButton(onClick = { onHelpDialogTextChange(explanation) })
                }
            }

            SectionHeader("STORYTELLER PRIORITIES")

            val priorities = listOf(
                "No Storyteller Priorities" to "The alignment and character type of each player is not influenced by the storyteller.",
                "Prioritize Alignments" to "The storyteller chooses the alignments of one or more players to prioritize (Good / Evil).",
                "Prioritize Types" to "The storyteller chooses the types of one or more players to prioritize (Townsfolk / Outsider / Minion / Demon)."
            )

            priorities.forEachIndexed { index, pair ->
                val i = index + 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPriorityChange(i) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPriority == i,
                            onClick = { onPriorityChange(i) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = pair.first)
                    }
                    HelpButton(onClick = { onHelpDialogTextChange(pair.second) })
                }
            }

            SectionHeader("") // Divider with no label

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGardeningToggleChange(!gardeningToggle) }
                        .padding(vertical = 4.dp)
                ) {
                    Switch(
                        checked = gardeningToggle,
                        onCheckedChange = onGardeningToggleChange
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Enable Player Priorities")
                }
                HelpButton(onClick = {
                    onHelpDialogTextChange("Allows players to prioritize their preferred characters. The gardening algorithm will weigh each assignment accordingly.")
                })
            }
        }
    }
}
