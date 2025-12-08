package com.individual_project3.kodegame.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt
import com.individual_project3.kodegame.R
import kotlinx.coroutines.isActive


@Composable
fun JumpSequence(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    spikeYdp: Dp = 140.dp,
    onSpikeNearCenter: (() -> Unit)? = null
) {
    if (!isVisible) return

    val spikeRes = R.drawable.spikes
    val spikeDp = 48.dp
    val travelMs = 3350

    val density = LocalDensity.current

    var containerWidthPx by remember { mutableStateOf(0f) }
    val spikeSizePx = with(density) { spikeDp.toPx() }
    val centerX = containerWidthPx / 2f
    val spikeOffset = remember { Animatable(0f) }

    var hasJumpTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (containerWidthPx <= 0f) return@LaunchedEffect
        while (isActive) {

            // reset spike
            spikeOffset.snapTo(containerWidthPx + spikeSizePx)
            hasJumpTriggered = false

            // slide fully through screen
            spikeOffset.animateTo(
                targetValue = -spikeSizePx,
                animationSpec = tween(durationMillis = travelMs, easing = LinearEasing)
            ) {
                val currentX = this.value
                val triggerX = centerX + spikeSizePx * 4.8f
                if (!hasJumpTriggered && currentX <= triggerX) {
                    hasJumpTriggered = true
                    onSpikeNearCenter?.invoke()
                }
            }

            delay(300)
        }
    }


    // Draw only spike — character is drawn separately in SplashScreen
    Box(modifier = modifier
        .fillMaxWidth()
        .onGloballyPositioned{layout ->
            containerWidthPx = layout.size.width.toFloat()
        }) {
        Image(
            painter = painterResource(id = spikeRes),
            contentDescription = "Spike",
            modifier = Modifier
                .size(spikeDp)
                .offset {
                    IntOffset(
                        x = spikeOffset.value.roundToInt(),
                        y = with(density) { spikeYdp.toPx().roundToInt() }
                    )
                }
        )
    }
}
