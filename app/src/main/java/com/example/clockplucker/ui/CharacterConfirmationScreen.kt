package com.example.clockplucker.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.R
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

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CharacterConfirmationScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel,
    playerIndex: Int
) {
    val script = viewModel.loadedScript ?: return
    val player = viewModel.players[playerIndex]
    
    val selectableCharacters = script.selectableCharacters

    val selectedCharacters = remember(playerIndex) {
        mutableStateListOf<Character>().apply {
            addAll(player.selectedChars)
        }
    }

    // Effect to update the ViewModel whenever the list changes (removal or reordering)
    LaunchedEffect(selectedCharacters.toList()) {
        viewModel.updatePlayer(playerIndex, player.copy(selectedChars = selectedCharacters.toList()))
    }

    // todo: annotatedRestrictionsText is being reset on screen navigation because selectedCharacters.size goes to 0, fix somehow?
    val labelSmallStyle = MaterialTheme.typography.labelSmall.toSpanStyle()
    val context = LocalContext.current
    val annotatedRestrictionsText = remember(
        selectedCharacters.size,
        viewModel.selectedMode,
        viewModel.alignmentN,
        viewModel.typeN,
        selectableCharacters,
        context
    ) {
        buildAnnotatedString {
            when (viewModel.selectedMode) {
                SelectedModes.NO_RESTRICTIONS -> append(context.getString(R.string.mode_no_restrictions_player_desc))
                SelectedModes.ALIGNMENT -> {
                    val goodInScript = selectableCharacters.count { it.alignment == CharAlignment.GOOD }
                    val evilInScript = selectableCharacters.count { it.alignment == CharAlignment.EVIL }
                    val goodLimit = minOf(viewModel.alignmentN, goodInScript)
                    val evilLimit = minOf(viewModel.alignmentN, evilInScript)

                    val goodCount = selectedCharacters.count { it.alignment == CharAlignment.GOOD }
                    val evilCount = selectedCharacters.count { it.alignment == CharAlignment.EVIL }
                    val goodRemaining = maxOf(0, goodLimit - goodCount)
                    val evilRemaining = maxOf(0, evilLimit - evilCount)

                    append(context.getString(R.string.you_may_select))
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$goodRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append(context.getString(R.string.good))
                    }
                    append(context.getString(R.string.and))
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$evilRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append(context.getString(R.string.evil))
                    }
                    append(
                        " " +
                        context.getString(
                            R.string.character_s,
                            if (goodRemaining != 1 || evilRemaining != 1) "s" else ""
                        ))
                }
                SelectedModes.TYPE -> {
                    val tInScript = selectableCharacters.count { it.type == CharType.TOWNSFOLK }
                    val oInScript = selectableCharacters.count { it.type == CharType.OUTSIDER }
                    val mInScript = selectableCharacters.count { it.type == CharType.MINION }
                    val dInScript = selectableCharacters.count { it.type == CharType.DEMON }

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

                    append(context.getString(R.string.you_may_select))
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$tRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append(context.getString(R.string.townsfolk))
                    }
                    append(", ")
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append("$oRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = GoodPrimary)) {
                        append(context.getString(R.string.outsider_s, ""),
                            if (oRemaining != 1) "S" else ""
                        )
                    }
                    append(", ")
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$mRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append(context.getString(R.string.minion_s, ""),
                            if (mRemaining != 1) "S" else ""
                        )
                    }
                    append("," + context.getString(R.string.and))
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append("$dRemaining")
                    }
                    append(context.getString(R.string.more))
                    withStyle(style = labelSmallStyle.copy(color = EvilPrimary)) {
                        append(context.getString(R.string.demon_s, ""),
                            if (dRemaining != 1) "S" else ""
                        )
                    }
                    append(".")
                }
            }
        }
    }

    val confirmationText = remember(
        annotatedRestrictionsText,
        viewModel.playerPriorityToggle,
        context
    ) {
        buildAnnotatedString {
            append(context.getString(R.string.character_confirmation_confirmation))
            append(annotatedRestrictionsText)
            if (viewModel.playerPriorityToggle) {
                append(context.getString(R.string.player_priority_player_desc))
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
                text = stringResource(R.string.selection_confirmation),
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
                        text = stringResource(R.string.most_preferred),
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
                        text = stringResource(R.string.least_preferred),
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
    val name = character.name.asString()
    val ability = character.ability.asString()

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
                        contentDescription = stringResource(R.string.drag_handle)
                    )
                }
            }

            Image(
                painter = painterResource(id = character.icon),
                contentDescription = name,
                modifier = Modifier
                    .size(108.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = nameColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ability,
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
                    contentDescription = stringResource(R.string.remove_character)
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
