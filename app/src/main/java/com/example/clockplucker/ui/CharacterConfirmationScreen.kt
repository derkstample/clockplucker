package com.example.clockplucker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.SelectedModes
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Character
import com.example.clockplucker.drawStableVerticalScrollbar
import com.example.clockplucker.ui.theme.EvilOnPrimaryContainer
import com.example.clockplucker.ui.theme.EvilPrimary
import com.example.clockplucker.ui.theme.GoodOnPrimaryContainer
import com.example.clockplucker.ui.theme.GoodPrimary
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CharacterConfirmationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel,
    playerIndex: Int
) {
    val script = viewModel.loadedScript ?: return
    val player = viewModel.players[playerIndex]

    // Saver to handle saving/restoring the list of selected characters
    val characterListSaver = listSaver<MutableList<Character>, String>(
        save = { list -> list.map { it.id } },
        restore = { ids -> 
            mutableStateListOf<Character>().apply {
                addAll(ids.mapNotNull { id -> script.characters.find { it.id == id } })
            }
        }
    )

    val selectedCharacters = rememberSaveable(playerIndex, saver = characterListSaver) {
        mutableStateListOf<Character>().apply {
            addAll(player.selectedChars)
        }
    }

    // Effect to update the ViewModel whenever the list changes (removal or reordering)
    LaunchedEffect(selectedCharacters.toList()) {
        viewModel.updatePlayer(playerIndex, player.copy(selectedChars = selectedCharacters.toList()))
    }

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
        }
    }

    val confirmationText = remember(annotatedRestrictionsText, viewModel.playerPriorityToggle) {
        buildAnnotatedString {
            append("Are you sure about your character selections? ")
            append(annotatedRestrictionsText)
            if (viewModel.playerPriorityToggle) {
                append(" You may rearrange your selected characters in order of preference.")
            }
        }
    }

    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        selectedCharacters.add(to.index, selectedCharacters.removeAt(from.index))
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val reorderEnabled = remember { viewModel.playerPriorityToggle }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = {
                    onBack()
                },
                onNext = {
                    onNext()
                },
                progress = 2,
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
            SectionHeader(
                text = "SELECTION CONFIRMATION",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = confirmationText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            if(reorderEnabled) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Most Preferred",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .drawStableVerticalScrollbar(state = listState)
            ) {
                items(
                    items = selectedCharacters,
                    key = { character -> character.id }
                ) { character ->
                    ReorderableItem(reorderableState, key = character.id) { isDragging ->
                        CharacterConfirmRow(
                            character = character,
                            modifier = Modifier
                                .animateItem()
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer {
                                    if (isDragging) {
                                        scaleX = 1.02f
                                        scaleY = 1.02f
                                        shadowElevation = 8.dp.toPx()
                                        clip = true
                                    }
                                }
                                .padding(horizontal = 8.dp),
                            handleModifier = Modifier.longPressDraggableHandle(
                                onDragStarted = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            ),
                            reorderable = reorderEnabled,
                            onRemove = { selectedCharacters.remove(character) }
                        )
                    }
                }
            }

            if(reorderEnabled) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Least Preferred",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterConfirmRow(
    character: Character,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier,
    reorderable: Boolean = false,
    onRemove: () -> Unit
) {
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

    val nameColor = remember(character.alignment) {
        if (character.alignment == CharAlignment.GOOD) GoodPrimary else EvilPrimary
    }
    val abilityColor = remember(character.alignment) {
        if (character.alignment == CharAlignment.GOOD) GoodOnPrimaryContainer else EvilOnPrimaryContainer
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .then(if (reorderable) handleModifier else Modifier)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(4.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (reorderable) {
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 56.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Drag Handle"
                    )
                }
            }

            if (character.icon != 0) {
                Image(
                    painter = painterResource(id = character.icon),
                    contentDescription = character.name,
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = nameColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = abilityText,
                    style = MaterialTheme.typography.bodySmall,
                    color = abilityColor
                )
            }

            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove"
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
    }
}
