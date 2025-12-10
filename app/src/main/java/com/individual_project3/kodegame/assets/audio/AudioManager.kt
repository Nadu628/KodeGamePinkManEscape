package com.individual_project3.kodegame.assets.audio


import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes

class AudioManager(private val context: Context) {

    // ---------- SOUND EFFECTS (SoundPool) ----------
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>() // resId -> soundId
    private val maxStreams = 6

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(maxStreams)
            .setAudioAttributes(attrs)
            .build()
    }

    /** Load your short SFX at startup */
    fun loadSfx(@RawRes vararg resIds: Int) {
        resIds.forEach { res ->
            if (!soundMap.containsKey(res)) {
                val id = soundPool.load(context, res, 1)
                soundMap[res] = id
            }
        }
    }

    /** Play a short SFX (jump, hit, collect, drop, etc.) */
    fun play(@RawRes resId: Int, volume: Float = 1f) {
        val id = soundMap[resId] ?: return
        soundPool.play(id, volume, volume, 1, 0, 1f)
    }

    // ---------- BACKGROUND MUSIC (MediaPlayer) ----------

    private var bgPlayer: MediaPlayer? = null

    /** Start background music (loops automatically) */
    fun startBackground(@RawRes musicRes: Int, volume: Float = 0.5f) {
        stopBackground()
        bgPlayer = MediaPlayer.create(context, musicRes)?.apply {
            isLooping = true
            setVolume(volume, volume)
            start()
        }
    }

    fun stopBackground() {
        bgPlayer?.let { player ->
            try {
                if (player.isPlaying) player.stop()
            } catch (_: Exception) { }
            try {
                player.release()
            } catch (_: Exception) { }
        }
        bgPlayer = null
    }

    fun isNotPlaying(): Boolean {
        return bgPlayer == null || bgPlayer?.isPlaying == false
    }


    fun release() {
        stopBackground()
        soundPool.release()
        soundMap.clear()
    }
}
