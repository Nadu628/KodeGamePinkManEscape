package com.individual_project3.kodegame.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt
import com.individual_project3.kodegame.R
import kotlinx.coroutines.isActive

@Composable
fun JumpSequence(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    spikeYdp: Dp = 140.dp,
    drawCharacter: Boolean = false,
    onSpikeNearCenter: (() -> Unit)? = null
) {
    if (!isVisible) return

    val spikeRes = R.drawable.spikes
    val spikeDp = 48.dp
    val travelMs = 2800

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val spikeSizePx = with(density) { spikeDp.toPx() }

    val centerX = screenWidthPx / 2f
    val spikeOffset = remember { Animatable(screenWidthPx + spikeSizePx) }

    LaunchedEffect(Unit) {
        while (isActive) {
            spikeOffset.snapTo(screenWidthPx + spikeSizePx)

            spikeOffset.animateTo(
                targetValue = -spikeSizePx,
                animationSpec = tween(travelMs, easing = LinearEasing)
            ) {
                // trigger callback when spike reaches center
                if (value <= centerX && onSpikeNearCenter != null) {
                    onSpikeNearCenter.invoke()
                }
            }

            delay(400)
        }
    }

    // Draw only spike — character is drawn separately in SplashScreen
    Box(modifier = modifier) {
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
