package com.individual_project3.kodegame.assets.sprites

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PlayerAnimState { Idle, Running, Jumping, Dropping, Hit }

@Composable
fun PlayerSprite(
    state: PlayerAnimState,
    @DrawableRes idleFrames: List<Int>,
    @DrawableRes runFrames: List<Int>,
    @DrawableRes jumpFrames: List<Int>,
    @DrawableRes dropFrames: List<Int>,
    @DrawableRes hitFrames: List<Int>,
    cellSize: Dp,
    x: Float,
    y: Float,
    modifier: Modifier = Modifier
) {
    val animX = remember { Animatable(x) }
    val animY = remember { Animatable(y) }

    LaunchedEffect(x, y) {
        animX.animateTo(x, animationSpec = tween(220))
        animY.animateTo(y, animationSpec = tween(220))
    }

    val (frames, frameDelay, bob) = when (state) {
        PlayerAnimState.Idle -> Triple(idleFrames, 140L, true)
        PlayerAnimState.Running -> Triple(runFrames, 90L, false)
        PlayerAnimState.Jumping -> Triple(jumpFrames, 80L, false)
        PlayerAnimState.Dropping -> Triple(dropFrames, 80L, false)
        PlayerAnimState.Hit -> Triple(hitFrames, 120L, false)
    }

    Box(modifier = modifier) {
        SpriteRenderer(
            frames = frames,
            frameDelayMs = frameDelay,
            bobbing = bob,
            bobAmplitudeDp = 6.dp,
            bobPeriodMs = 900L,
            modifier = Modifier,
            contentDescription = "player"
        )
    }
}
