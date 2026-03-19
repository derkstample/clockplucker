package com.example.clockplucker.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.concurrent.timer
import kotlin.math.cos
import kotlin.math.sin

/**
 * A composable that renders numPlayers circles arranged in a large circle.
 * Circles are filled up to the current progress.
 * On entry or progress change, it rotates the active circle to the top and fills it.
 */
@Composable
fun PlayerProgressCircle(
    numPlayers: Int,
    progress: Int,
    modifier: Modifier = Modifier,
    radius: Dp = 100.dp
) {
    if (numPlayers <= 0) return

    val angleStep = 360f / numPlayers
    
    // We want the current 'progress' circle to be at the top (-90 degrees).
    // The rotation of the container should be -90 - (progress * angleStep).
    val targetRotation = -90f - (progress * angleStep)
    
    // To animate "to the next circle", we start the rotation from the previous progress position.
    // If progress is 0, we start at the target rotation (no rotation animation).
    val initialRotation = if (progress > 0) {
        -90f - ((progress - 1) * angleStep)
    } else {
        targetRotation
    }

    val rotation = remember { Animatable(initialRotation) }
    // By keying the Animatable with progress, we ensure it resets to 0f immediately
    // when progress changes, avoiding any flicker from its previous state.
    val fillProgress = remember(progress) { Animatable(0f) }

    LaunchedEffect(progress) {
        if (progress > 0) {
            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
        } else {
            rotation.snapTo(targetRotation)
            delay(timeMillis = 800)
        }

        fillProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    val circleSize = (radius / numPlayers.toFloat()) * 2.0f

    Box(
        modifier = modifier
            .size(radius * 2 + circleSize)
            .graphicsLayer { rotationZ = rotation.value },
        contentAlignment = Alignment.Center
    ) {
        for (i in 0 until numPlayers) {
            val angleDeg = i * angleStep
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val x = (radius.value * cos(angleRad)).dp
            val y = (radius.value * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .offset(x, y)
                    .size(circleSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val isFilled = i < progress
                val isCurrentlyFilling = i == progress
                
                if (isFilled || isCurrentlyFilling) {
                    val scale = if (isCurrentlyFilling) fillProgress.value else 1f
                    val alpha = if (isCurrentlyFilling) fillProgress.value else 1f
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerProgressCirclePreview() {
    MaterialTheme {
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            PlayerProgressCircle(numPlayers = 8, progress = 3)
        }
    }
}
