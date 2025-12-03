package com.individual_project3.kodegame.assets.sprites

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import kotlinx.coroutines.delay

@Composable
fun SpriteRenderer(
    @DrawableRes frames: List<Int>,
    modifier: Modifier = Modifier,
    frameDelayMs: Long = 120L,
    bobbing: Boolean = false,
    bobAmplitudeDp: Dp = 6.dp,
    bobPeriodMs: Long = 900L,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null
) {
    if (frames.isEmpty()) return

    val painters = frames.map { painterResource(id = it) }

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(frames, frameDelayMs) {
        while (true) {
            delay(frameDelayMs)
            frameIndex = (frameIndex + 1) % painters.size
        }
    }

    val bobAnim = remember { Animatable(0f) }
    if (bobbing) {
        LaunchedEffect(bobAnim, bobPeriodMs) {
            while (true) {
                bobAnim.animateTo(-1f, animationSpec = tween(durationMillis = (bobPeriodMs / 2).toInt()))
                bobAnim.animateTo(1f, animationSpec = tween(durationMillis = (bobPeriodMs / 2).toInt()))
            }
        }
    } else {
        LaunchedEffect(Unit) { bobAnim.snapTo(0f) }
    }

    val density = LocalDensity.current
    val bobOffsetDp = with(density) { (bobAnim.value * bobAmplitudeDp.toPx()).toDp() }

    Image(
        painter = painters[frameIndex],
        contentDescription = contentDescription,
        modifier = modifier.offset(y = bobOffsetDp),
        contentScale = contentScale
    )
}
