package com.example.clockplucker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.data.Character

@Composable
fun CharacterSelectScreen(
    onBack : () -> Unit,
    onNext : () -> Unit,
    viewModel: MainViewModel
) {

}

// will use later
@Composable
fun CharacterRow(character: Character) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (character.icon != 0) {
            Image(
                painter = painterResource(id = character.icon),
                contentDescription = character.name,
                modifier = Modifier
                    .size(96.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("?")
            }
        }
        Column {
            Text(
                text = character.name,
                fontWeight = FontWeight.Bold
            )
            Text(text = character.ability)
        }
    }
}
