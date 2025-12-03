package com.individual_project3.kodegame.assets.sprites

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StrawberrySprite(
    @DrawableRes frames: List<Int>,
    cellSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        SpriteRenderer(
            frames = frames,
            frameDelayMs = 200L,
            bobbing = true,
            bobAmplitudeDp = 6.dp,
            contentDescription = "strawberry"
        )
    }
}
