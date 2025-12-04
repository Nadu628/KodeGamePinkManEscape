package com.individual_project3.kodegame.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import com.individual_project3.kodegame.R
import kotlinx.coroutines.isActive


@Composable
fun RunningCharacter(
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    frameDelayMs: Long = 80L
) {
    val runFrames = listOf(
        R.drawable.pink_run1, R.drawable.pink_run2, R.drawable.pink_run3, R.drawable.pink_run4,
        R.drawable.pink_run5, R.drawable.pink_run6, R.drawable.pink_run7, R.drawable.pink_run8,
        R.drawable.pink_run9, R.drawable.pink_run10, R.drawable.pink_run11, R.drawable.pink_run12
    )

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        if (!isRunning) {
            frameIndex = 0
            return@LaunchedEffect
        }

        while (isActive && isRunning) {
            delay(frameDelayMs)
            frameIndex = (frameIndex + 1) % runFrames.size
        }
    }

    Image(
        painter = painterResource(id = runFrames[frameIndex]),
        contentDescription = "Running character",
        modifier = modifier
    )
}
