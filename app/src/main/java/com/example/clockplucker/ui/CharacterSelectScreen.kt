package com.example.clockplucker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.SelectedModes
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Character
import com.example.clockplucker.data.Script
import com.example.clockplucker.drawStableVerticalScrollbar
import com.example.clockplucker.ui.theme.DisabledTheme
import com.example.clockplucker.ui.theme.EvilPrimary
import com.example.clockplucker.ui.theme.EvilTheme
import com.example.clockplucker.ui.theme.GoodPrimary
import com.example.clockplucker.ui.theme.GoodTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterSelectScreen(
    onBack: () -> Unit,
    onNext: (List<Character>) -> Unit,
    viewModel: MainViewModel,
    playerIndex: Int
) {
    val script = viewModel.loadedScript ?: return
    val player = viewModel.players[playerIndex]
    val charactersByType = remember(script) { script.characters.groupBy { it.type } }
    val hasNightPenalty = remember(script) { script.characters.any { it.ability.contains("*") } }
    val listState = rememberLazyListState()
    val selectedCharacters = remember(playerIndex) {
        mutableStateListOf<Character>().apply {
            addAll(player.selectedChars)
        }
    }
    var showDisabledPopup by remember { mutableStateOf(false) }

    val labelSmallStyle = MaterialTheme.typography.labelSmall.toSpanStyle()
    val annotatedRestrictionsText = remember(selectedCharacters.size, viewModel.selectedMode, viewModel.alignmentN, viewModel.typeN, script) {
        buildAnnotatedString {
            when (viewModel.selectedMode) {
                SelectedModes.NO_RESTRICTIONS -> append("You may select any number of characters.")
                SelectedModes.ALIGNMENT -> {
                    val goodInScript = script.characters.count { it.alignment == CharAlignment.GOOD }
                    val evilInScript = script.characters.count { it.alignment == CharAlignment.EVIL }
                    val goodLimit = minOf(viewModel.alignmentN, goodInScript)
                    val evilLimit = minOf(viewModel.alignmentN, evilInScript)

                    val goodCount = selectedCharacters.count { it.alignment == CharAlignment.GOOD }
                    val evilCount = selectedCharacters.count { it.alignment == CharAlignment.EVIL }
                    val goodRemaining = maxOf(0, goodLimit - goodCount)
                    val evilRemaining = maxOf(0, evilLimit - evilCount)

                    append("You may select ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$goodRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("GOOD")
                    }
                    append(" and ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$evilRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("EVIL")
                    }
                    append(" character")
                    if (goodRemaining != 1 || evilRemaining != 1) append("s")
                    append(".")
                }
                SelectedModes.TYPE -> {
                    val tInScript = script.characters.count { it.type == CharType.TOWNSFOLK }
                    val oInScript = script.characters.count { it.type == CharType.OUTSIDER }
                    val mInScript = script.characters.count { it.type == CharType.MINION }
                    val dInScript = script.characters.count { it.type == CharType.DEMON }

                    val tLimit = minOf(viewModel.typeN, tInScript)
                    val oLimit = minOf(viewModel.typeN, oInScript)
                    val mLimit = minOf(viewModel.typeN, mInScript)
                    val dLimit = minOf(viewModel.typeN, dInScript)

                    val tCount = selectedCharacters.count { it.type == CharType.TOWNSFOLK }
                    val oCount = selectedCharacters.count { it.type == CharType.OUTSIDER }
                    val mCount = selectedCharacters.count { it.type == CharType.MINION }
                    val dCount = selectedCharacters.count { it.type == CharType.DEMON }

                    val tRemaining = maxOf(0, tLimit - tCount)
                    val oRemaining = maxOf(0, oLimit - oCount)
                    val mRemaining = maxOf(0, mLimit - mCount)
                    val dRemaining = maxOf(0, dLimit - dCount)

                    append("You may select ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$tRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("TOWNSFOLK")
                    }
                    append(", ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$oRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("OUTSIDER")
                        if (oRemaining != 1) append("S")
                    }
                    append(", ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$mRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("MINION")
                        if (mRemaining != 1) append("S")
                    }
                    append(", and ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$dRemaining")
                    }
                    append(" more ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("DEMON")
                        if (dRemaining != 1) append("S")
                    }
                    append(".")
                }
            }
            append(" You will be able to review your selection.")
        }
    }

    if (showDisabledPopup) {
        AlertDialog(
            onDismissRequest = { showDisabledPopup = false },
            confirmButton = {
                TextButton(onClick = { showDisabledPopup = false }) {
                    Text(text = "OK", style = MaterialTheme.typography.bodyMedium)
                }
            },
            title = { Text("Character Restricted") },
            text = { Text("This character's ability depends on its player thinking they are a different character. For this reason, you cannot select this character as preferred.") }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = onBack,
                onNext = { onNext(selectedCharacters.toList()) },
                progress = 1,
                total = 2,
                nextEnabled = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScriptHeader(script)

            Text(
                text = annotatedRestrictionsText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawStableVerticalScrollbar(state = listState)
            ) {
                charactersByType.forEach { (type, characters) ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp)
                        ) {
                            SectionHeader(
                                text = type.name + if (type != CharType.TOWNSFOLK) "S" else ""
                            )
                        }
                    }
                    items(
                        items = characters,
                        key = { it.id }
                    ) { character ->
                        val isSelected = selectedCharacters.contains(character)
                        val isDisabled = !character.thinksTheyAre.isEmpty()
                        val canSelect = remember(selectedCharacters.size, viewModel.selectedMode, viewModel.alignmentN, viewModel.typeN) {
                            derivedStateOf {
                                if (isSelected) true
                                else {
                                    when (viewModel.selectedMode) {
                                        SelectedModes.NO_RESTRICTIONS -> true
                                        SelectedModes.ALIGNMENT -> {
                                            val limit = minOf(viewModel.alignmentN, script.characters.count { it.alignment == character.alignment })
                                            val count = selectedCharacters.count { it.alignment == character.alignment }
                                            count < limit
                                        }
                                        SelectedModes.TYPE -> {
                                            val limit = minOf(viewModel.typeN, script.characters.count { it.type == character.type })
                                            val count = selectedCharacters.count { it.type == character.type }
                                            count < limit
                                        }
                                    }
                                }
                            }
                        }.value

                        val characterRowContent = @Composable {
                            CharacterRow(
                                character = character,
                                isSelected = isSelected,
                                isDisabled = isDisabled,
                                onClick = {
                                    if (isDisabled) {
                                        showDisabledPopup = true
                                    } else if (isSelected) {
                                        selectedCharacters.remove(character)
                                    } else if (canSelect) {
                                        selectedCharacters.add(character)
                                    }
                                }
                            )
                        }

                        if (isDisabled) {
                            DisabledTheme { characterRowContent() }
                        } else if (character.alignment == CharAlignment.GOOD) {
                            GoodTheme { characterRowContent() }
                        } else {
                            EvilTheme { characterRowContent() }
                        }
                    }
                }

                if (hasNightPenalty) {
                    item {
                        Text(
                            text = "* Not the first night",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptHeader(
    script: Script
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = script.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alignByBaseline()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "by ${script.author}",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun CharacterRow(
    character: Character,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    val surfaceColor = remember(isSelected, isDisabled, colorScheme) {
        if (isDisabled || isSelected) colorScheme.primaryContainer.copy(alpha = 0.15f)
        else Color.Transparent
    }

    val outlineColor = remember(isSelected, isDisabled, colorScheme) {
        if (isDisabled || isSelected) colorScheme.primary
        else Color.Transparent
    }

    val animatedSurfaceColor by animateColorAsState(
        targetValue = surfaceColor,
        label = "rowSurfaceColor",
        animationSpec = tween(durationMillis = 200)
    )

    val animatedOutlineColor by animateColorAsState(
        targetValue = outlineColor,
        label = "rowOutlineColor",
        animationSpec = tween(durationMillis = 200)
    )

    val abilityText = remember(character.ability) {
        buildAnnotatedString {
            val regex = Regex("\\[.*?]")
            var lastIndex = 0
            regex.findAll(character.ability).forEach { matchResult ->
                append(character.ability.substring(lastIndex, matchResult.range.first))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(matchResult.value)
                }
                lastIndex = matchResult.range.last + 1
            }
            append(character.ability.substring(lastIndex))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .border(1.dp, animatedOutlineColor, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(animatedSurfaceColor)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (character.icon != 0) {
            Image(
                painter = painterResource(id = character.icon),
                contentDescription = character.name,
                colorFilter = if (isDisabled) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null,
                modifier = Modifier
                    .size(108.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("?")
            }
        }
        Column {
            Text(
                text = character.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = abilityText,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}
