package com.individual_project3.kodegame.assets.audio


import android.content.Context
import android.media.MediaPlayer

class AudioManager(private val context: Context) {
    private var player: MediaPlayer? = null

    fun play(resId: Int, loop: Boolean = false) {
        stop()
        player = MediaPlayer.create(context, resId).apply {
            isLooping = loop
            start()
        }
    }

    fun stop() {
        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
    }
}
