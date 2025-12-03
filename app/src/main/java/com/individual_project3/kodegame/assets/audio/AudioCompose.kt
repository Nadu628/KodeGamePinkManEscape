package com.individual_project3.kodegame.assets.audio

import androidx.annotation.RawRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
fun rememberAudioManager(
    @RawRes hit: Int,
    @RawRes jump: Int,
    @RawRes drop: Int,
    @RawRes collect: Int,
    @RawRes button: Int,
    @RawRes success: Int,
    @RawRes fail: Int,
    @RawRes music: Int,
    autoStartMusic: Boolean = true
): AudioManager {
    val ctx = LocalContext.current
    val audioManager = remember { AudioManager(ctx) }

    DisposableEffect(audioManager) {
        audioManager.loadSfx(hit, jump, drop, collect, button, success, fail)
        if (autoStartMusic) audioManager.startBackground(music)
        onDispose {
            audioManager.stopBackground()
            audioManager.release()
        }
    }

    val ready by remember { derivedStateOf { audioManager.areSfxReady() } }
    // small warm-up delay to allow setOnLoadCompleteListener to update
    LaunchedEffect(ready) {
        if (!ready) {
            while (!audioManager.areSfxReady()) {
                delay(40)
            }
        }
    }

    return audioManager
}
