package com.individual_project3.kodegame.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.individual_project3.kodegame.R


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
        if (isRunning) {
            // loop while running
            while (true) {
                delay(frameDelayMs)
                frameIndex = (frameIndex + 1) % runFrames.size
            }
        } else {
            // reset to first run frame when not running (so image is stable)
            frameIndex = 0
        }
    }

    Image(
        painter = painterResource(id = runFrames[frameIndex % runFrames.size]),
        contentDescription = "Running character",
        modifier = modifier
    )
}