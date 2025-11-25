package com.individual_project3.kodegame.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt
import com.individual_project3.kodegame.R
import kotlinx.coroutines.delay

@Composable
fun JumpSequence(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    spikeYdp: Dp = 140.dp,
    onSpikeNearCenter: (() -> Unit)? = null
) {
    if (!isVisible) return

    val jumpFrames = listOf(
        R.drawable.pink_doublejump1, R.drawable.pink_doublejump2,
        R.drawable.pink_doublejump3, R.drawable.pink_doublejump4,
        R.drawable.pink_doublejump5, R.drawable.pink_doublejump6
    )

    val spikeRes = R.drawable.spikes
    val spikeDp = 48.dp
    val travelMs = 3000
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val spikeSizePx = with(density) { spikeDp.toPx() }

    val centerX = screenWidthPx / 2f

    val spikeOffset = remember { Animatable(screenWidthPx + spikeSizePx) }
    val characterY = remember { Animatable(0f) }
    var isJumping by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            spikeOffset.snapTo(screenWidthPx + spikeSizePx) // off-screen right
            isJumping = false
            characterY.snapTo(0f)
            spikeOffset.animateTo(
                targetValue = -spikeSizePx,
                animationSpec = tween(durationMillis = travelMs, easing = LinearEasing)
            )
            delay(450) // pause between spikes
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            while (spikeOffset.value > centerX + 12f) {
                delay(16)
            }
            isJumping = true
            onSpikeNearCenter?.invoke()

            // animate character Y up then down (visual jump inside JumpSequence)
            characterY.animateTo(
                targetValue = -40f, // jump up (px) — tune to match your assets
                animationSpec = tween(durationMillis = 280, easing = FastOutLinearInEasing)
            )
            characterY.animateTo(
                targetValue = 0f, // land
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
            )

            while (spikeOffset.value >= centerX - 12f) {
                delay(16)
            }

            isJumping = false
            // allow a short cooldown before next detection
            delay(150)
        }
    }

    //cycling for jump frames
    var frameIndex by remember { mutableStateOf(0) }
    LaunchedEffect(isJumping) {
        while (true) {
            if (isJumping) {
                frameIndex = (frameIndex + 1) % jumpFrames.size
                delay(90)
            } else {
                // ensure frameIndex resets when idle so the next jump starts from frame 0 (optional)
                frameIndex = 0
                delay(150)
            }
        }
    }

    Box(modifier = modifier) {
        // draw spike
        Image(
            painter = painterResource(id = spikeRes),
            contentDescription = "spike",
            modifier = Modifier
                .size(spikeDp)
                .offset {
                    IntOffset(
                        x = spikeOffset.value.roundToInt(),
                        y = with(density) { 120.dp.roundToPx() } // vertical placement of spike; tweak as needed
                    )
                }
        )

        // draw jumping frames only when jumping; parent should draw the running character when not jumping
        if (isJumping) {
            Image(
                painter = painterResource(id = jumpFrames[frameIndex % jumpFrames.size]),
                contentDescription = "jumping character",
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopCenter)
                    .offset {
                        IntOffset(x = 0, y = characterY.value.roundToInt())
                    }
            )
        }
    }
}
