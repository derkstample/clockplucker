package com.example.clockplucker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.data.TypeCountLookup

@Composable
fun GrimRevealScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val script = viewModel.loadedScript
    val characters = script?.characters
    val players = viewModel.players
    val lookup = remember { TypeCountLookup() }


}
