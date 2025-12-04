package com.individual_project3.kodegame.ui.splash

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import com.individual_project3.kodegame.R
import kotlinx.coroutines.isActive


@Composable
fun IdleCharacter(
    modifier: Modifier = Modifier,
    imageSize: Dp = 64.dp,
    frameDelayMs: Long = 150L
) {
    val idleFrames = listOf(
        R.drawable.pink_idle1, R.drawable.pink_idle2, R.drawable.pink_idle3,
        R.drawable.pink_idle4, R.drawable.pink_idle5, R.drawable.pink_idle6,
        R.drawable.pink_idle7, R.drawable.pink_idle8, R.drawable.pink_idle9,
        R.drawable.pink_idle10, R.drawable.pink_idle11
    )

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(frameDelayMs)
            frameIndex = (frameIndex + 1) % idleFrames.size
        }
    }

    val targetYOffset = if (frameIndex % 3 == 0) (-10).dp else 0.dp
    val yOffset by animateDpAsState(targetValue = targetYOffset, label = "idle-bob")

    Image(
        painter = painterResource(id = idleFrames[frameIndex]),
        contentDescription = "Idle character",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .offset(y = yOffset)
            .size(imageSize)
            .clip(RoundedCornerShape(8.dp)),
        alignment = Alignment.Center
    )
}
