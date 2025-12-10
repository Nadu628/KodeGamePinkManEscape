package com.individual_project3.kodegame

import android.app.Application
import com.individual_project3.kodegame.assets.audio.AudioManager

class KodeGameApp : Application() {

    companion object {
        lateinit var audio: AudioManager
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Create one shared AudioManager
        audio = AudioManager(this)

        // Load all global SFX once
        audio.loadSfx(
            R.raw.sfx_button_click,
            R.raw.sfx_jump,
            R.raw.sfx_drop,
            R.raw.sfx_hit,
            R.raw.sfx_collecting_fruit,
            R.raw.sfx_success
        )
    }
}
