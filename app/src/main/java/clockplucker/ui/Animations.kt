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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.min
import kotlinx.coroutines.delay
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
    val targetRotation = -90f - (progress * angleStep)

    // Track the progress we have already finished animating to.
    // Using rememberSaveable ensures this survives device rotation.
    var lastAnimatedProgress by rememberSaveable { mutableIntStateOf(-1) }

    // Initialize the Animatable with the correct final state if this is a recreation (rotation)
    // or with the starting state if it's a new progress.
    val rotation = remember {
        val initialProgress = if (lastAnimatedProgress != -1 && lastAnimatedProgress != progress) {
            // Start from the previous player's angle to animate the transition
            lastAnimatedProgress
        } else if (lastAnimatedProgress == -1 && progress > 0) {
            // Fallback for when state is lost due to navigation
            progress - 1
        } else {
            // Start directly at current progress (prevents re-playing animation on rotation)
            progress
        }
        Animatable(-90f - (initialProgress * angleStep))
    }

    // Similarly for fillProgress: 1f if we were already at this progress, 0f if we need to animate it.
    val fillProgress = remember {
        Animatable(if (lastAnimatedProgress == progress) 1f else 0f)
    }

    LaunchedEffect(progress) {
        // If the values already match, this is a recomposition/rotation that was already initialized.
        if (lastAnimatedProgress == progress) return@LaunchedEffect

        // We animate the rotation if we are transitioning between players,
        // or if we just arrived on the screen at a non-zero progress.
        val shouldAnimateRotation = (lastAnimatedProgress != -1) || (progress > 0)

        if (shouldAnimateRotation) {
            // Ensure we are at the correct starting position before animating
            val startProgress = if (lastAnimatedProgress != -1) lastAnimatedProgress else progress - 1
            rotation.snapTo(-90f - (startProgress * angleStep))
            fillProgress.snapTo(0f)

            // Small delay to let the screen transition settle
            delay(timeMillis = 300)

            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
        } else {
            // First entry on player 0
            rotation.snapTo(targetRotation)
            fillProgress.snapTo(0f)
            delay(timeMillis = 800)
        }

        fillProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )

        lastAnimatedProgress = progress
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val circleSizeFactor = 2.0f / numPlayers.toFloat()
        val totalSizeFactor = 2.0f + circleSizeFactor
        
        // The diameter we'd like to have based on the preferred radius
        val preferredDiameter = radius * totalSizeFactor
        
        // The actual diameter available in the current constraints
        val availableDiameter = if (maxHeight.isSpecified && maxHeight < 2000.dp) {
            min(maxWidth, maxHeight)
        } else {
            maxWidth
        }
        
        // We use the smaller of what we want and what we have
        val totalSize = min(availableDiameter, preferredDiameter)
        
        // Recalculate radius and circleSize based on the actual totalSize
        val actualRadius = totalSize / totalSizeFactor
        val circleSize = actualRadius * circleSizeFactor

        Box(
            modifier = Modifier
                .size(totalSize)
                .graphicsLayer { rotationZ = rotation.value },
            contentAlignment = Alignment.Center
        ) {
            for (i in 0 until numPlayers) {
                val angleDeg = i * angleStep
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val x = (actualRadius.value * cos(angleRad)).dp
                val y = (actualRadius.value * sin(angleRad)).dp

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
}

@Composable
fun WaitingAnimation(
    numPlayers: Int,
    modifier: Modifier = Modifier,
    radius: Dp = 100.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waiting")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val circleSizeFactor = 2.0f / numPlayers.toFloat()
        val totalSizeFactor = 2.0f + circleSizeFactor

        // The diameter we'd like to have based on the preferred radius
        val preferredDiameter = radius * totalSizeFactor

        // The actual diameter available in the current constraints
        val availableDiameter = if (maxHeight.isSpecified && maxHeight < 2000.dp) {
            min(maxWidth, maxHeight)
        } else {
            maxWidth
        }

        // We use the smaller of what we want and what we have
        val totalSize = min(availableDiameter, preferredDiameter)

        // Recalculate radius and circleSize based on the actual totalSize
        val actualRadius = totalSize / totalSizeFactor
        val circleSize = actualRadius * circleSizeFactor

        Box(
            modifier = Modifier
                .size(totalSize)
                .rotate(angle),
            contentAlignment = Alignment.Center
        ) {
            for (i in 0 until numPlayers) {
                val circleAngle = (i.toFloat() / numPlayers) * 2 * Math.PI
                val x = (actualRadius.value * cos(circleAngle)).dp
                val y = (actualRadius.value * sin(circleAngle)).dp

                Box(
                    modifier = Modifier
                        .offset(x,y)
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
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

@Preview(showBackground = true)
@Composable
fun WaitingAnimationPreview() {
    MaterialTheme {
        Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            WaitingAnimation(
                numPlayers = 8
            )
        }
    }
}

