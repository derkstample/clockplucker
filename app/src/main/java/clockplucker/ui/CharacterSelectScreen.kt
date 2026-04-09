package clockplucker.ui

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

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import clockplucker.MainViewModel
import clockplucker.NavigationBar
import com.example.clockplucker.R
import clockplucker.SectionHeader
import clockplucker.SelectedModes
import clockplucker.data.CharAlignment
import clockplucker.data.CharType
import clockplucker.data.Character
import clockplucker.drawStableVerticalScrollbar
import clockplucker.data.CharacterRepository
import clockplucker.data.DjinnRepository
import clockplucker.data.Script
import clockplucker.ui.theme.DisabledTheme
import clockplucker.ui.theme.EvilPrimary
import clockplucker.ui.theme.EvilTheme
import clockplucker.ui.theme.FabledPrimary
import clockplucker.ui.theme.GoodPrimary
import clockplucker.ui.theme.GoodTheme
import clockplucker.ui.theme.LoricPrimary

@SuppressLint("LocalContextGetResourceValueCall")
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
    
    val selectableCharacters = script.selectableCharacters
    
    val djinnJinxes = remember(selectableCharacters) {
        val jinxes = mutableListOf<Pair<Character,Character>>()
        for (i in selectableCharacters.indices) {
            for (j in selectableCharacters.indices) { // kind of horribly inefficient, but we only need to run it once per composition
                val char1 = selectableCharacters[i]
                val char2 = selectableCharacters[j]
                if (DjinnRepository.getJinxAbility(char1.id, char2.id) != null) {
                    jinxes.add(char1 to char2)
                }
            }
        }
        jinxes
    }
    
    val charactersByType = remember(selectableCharacters) { selectableCharacters.groupBy { it.type } }
    val context = LocalContext.current
    val hasNightPenalty = remember(selectableCharacters, context) {
        selectableCharacters.any { it.ability.resolve(context).contains("*") }
    }
    val listState = rememberLazyListState()

    // Use remember(playerIndex) so it re-initializes from the ViewModel when returning from the backstack.
    // Immediate updates to the ViewModel (via LaunchedEffect) ensure rotation is handled by the ViewModel surviving.
    val selectedCharacters = remember(playerIndex) {
        mutableStateListOf<Character>().apply {
            addAll(player.selectedChars)
        }
    }

    val fabledAndLoric = remember(script.excludedCharacters) {
        // If necessary, add the djinn to the fabled/loric chars. Ensure it is the first in the list
        val excludedCharacters = mutableListOf<Character>()
        if (djinnJinxes.isNotEmpty()) CharacterRepository.getCharacterInfo("djinn")?.let { excludedCharacters.add(it) }
        excludedCharacters.addAll(script.excludedCharacters)

        val fabled = excludedCharacters.filter { it.type == CharType.FABLED }
        val loric = excludedCharacters.filter { it.type == CharType.LORIC }
        fabled + loric // We need to split the types up because some script tools don't automatically put fabled before loric
    }

    // Push changes to the ViewModel immediately
    LaunchedEffect(selectedCharacters.toList()) {
        viewModel.updatePlayer(playerIndex, player.copy(selectedChars = selectedCharacters.toList()))
    }

    var showDisabledPopup by rememberSaveable { mutableStateOf(false) }

    val labelSmallStyle = MaterialTheme.typography.labelSmall.toSpanStyle()
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
                    append(
                        " " +
                                context.getString(
                                    R.string.character_s,
                                    if (goodRemaining != 1) "s" else ""
                                )
                    )
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
                            if (evilRemaining != 1) "s." else "."
                        )
                    )
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
            append(context.getString(R.string.you_will_be_able_to_review_your_selection))
        }
    }

    if (showDisabledPopup) {
        AlertDialog(
            onDismissRequest = { showDisabledPopup = false },
            confirmButton = {
                TextButton(onClick = { showDisabledPopup = false }) {
                    Text(text = stringResource(R.string.ok), style = MaterialTheme.typography.bodyMedium)
                }
            },
            title = { Text(stringResource(R.string.character_restricted)) },
            text = { Text(stringResource(R.string.character_restricted_desc)) }
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
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                charactersByType.forEach { (type, characters) ->
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp)
                        ) {
                            SectionHeader(
                                text = when (type) {
                                    CharType.OUTSIDER -> stringResource(R.string.outsider_s, "S")
                                    CharType.MINION -> stringResource(R.string.minion_s, "S")
                                    CharType.DEMON -> stringResource(R.string.demon_s, "S")
                                    else -> stringResource(R.string.townsfolk)
                                }
                            )
                        }
                    }
                    items(
                        items = characters,
                        key = { it.id }
                    ) { character ->
                        val isSelected = selectedCharacters.contains(character)
                        val isDisabled = character.thinksTheyAre.isNotEmpty()
                        val canSelect = remember(
                            selectedCharacters.size,
                            viewModel.selectedMode,
                            viewModel.alignmentN,
                            viewModel.typeN
                        ) {
                            derivedStateOf {
                                if (isSelected) true
                                else {
                                    when (viewModel.selectedMode) {
                                        SelectedModes.NO_RESTRICTIONS -> true
                                        SelectedModes.ALIGNMENT -> {
                                            val limit = minOf(
                                                viewModel.alignmentN,
                                                selectableCharacters.count { it.alignment == character.alignment })
                                            val count =
                                                selectedCharacters.count { it.alignment == character.alignment }
                                            count < limit
                                        }

                                        SelectedModes.TYPE -> {
                                            val limit = minOf(
                                                viewModel.typeN,
                                                selectableCharacters.count { it.type == character.type })
                                            val count =
                                                selectedCharacters.count { it.type == character.type }
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
                                jinxIcons = djinnJinxes.filter { it.first == character }.map { it.second.icon },
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

                if (fabledAndLoric.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 16.dp)
                        ) {
                            SectionHeader(
                                text = stringResource(R.string.fabled_loric)
                            )
                        }
                    }
                    items(fabledAndLoric.chunked(2)) { pair ->
                        ExcludedCharacterRow(
                            character1 = pair[0],
                            character2 = pair.getOrNull(1),
                            jinxes = djinnJinxes
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }
            }
            if (hasNightPenalty) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Text(
                    text = stringResource(R.string.not_the_first_night),
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
            text = stringResource(R.string.script_author, script.author),
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
    jinxIcons: List<Int> = emptyList(),
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

    val name = character.name.asAnnotatedString()
    val ability = character.ability.asAnnotatedString()

    val colorFilter = remember(isDisabled) {
        if (isDisabled) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
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
        Image(
            painter = painterResource(id = character.icon),
            contentDescription = name.toString(),
            colorFilter = colorFilter,
            modifier = Modifier
                .size(108.dp)
                .aspectRatio(1f)
                .padding(end = 8.dp)
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.primary
                )
                jinxIcons.forEach { icon ->
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        colorFilter = colorFilter,
                        modifier = Modifier
                            .size(36.dp)
                            .aspectRatio(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ability,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}

@Composable
fun ExcludedCharacterRow(
    character1: Character,
    character2: Character?,
    jinxes: List<Pair<Character,Character>>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation"
    )

    val name1 = character1.name.asAnnotatedString()
    val ability1 = character1.ability.asAnnotatedString()

    val name2 = character2?.name?.asAnnotatedString()
    val ability2 = character2?.ability?.asAnnotatedString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                expanded = !expanded
                onClick()
            }
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = character1.icon),
                    contentDescription = name1.toString(),
                    modifier = Modifier
                        .size(72.dp)
                        .aspectRatio(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = name1,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (character1.type == CharType.FABLED) FabledPrimary else LoricPrimary
                )
            }
            character2?.let {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = it.icon),
                        contentDescription = name2.toString(),
                        modifier = Modifier
                            .size(72.dp)
                            .aspectRatio(1f)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = name2!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (it.type == CharType.FABLED) FabledPrimary else LoricPrimary
                    )
                }
            } ?: Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = ability1,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    if (character2 != null && ability2 != null) {
                        Text(
                            text = ability2,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Add spacer to align with the icon above
                    Spacer(modifier = Modifier
                        .size(24.dp)
                        .padding(end = 16.dp))
                }

                if (character1.id == "djinn" || character2?.id == "djinn") {
                    for (jinx in jinxes) {
                        DjinnRow(jinx = jinx)
                    }
                }
            }
        }
    }
}

@Composable
fun DjinnRow(
    jinx: Pair<Character,Character>,
    modifier: Modifier = Modifier
) {
    val name1 = jinx.first.name.asAnnotatedString()
    val name2 = jinx.second.name.asAnnotatedString()
    val jinxAbilityId = DjinnRepository.getJinxAbility(jinx.first.id, jinx.second.id)
    val ability = jinxAbilityId?.let { stringResource(id = it) } ?: ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = jinx.first.icon),
            contentDescription = name1.toString(),
            modifier = Modifier
                .size(72.dp)
                .aspectRatio(1f)
        )
        Image(
            painter = painterResource(id = jinx.second.icon),
            contentDescription = name2.toString(),
            modifier = Modifier
                .size(72.dp)
                .aspectRatio(1f)
        )
        Text(
            text = ability,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}