package com.individual_project3.kodegame.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class AudioManager(private val context: Context) {
    private val maxStreams = 4
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<Int, Int>() // resId -> soundId
    private val loadCount = AtomicInteger(0)
    private var expectedLoads = 0

    private var bgPlayer: MediaPlayer? = null

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attrs)
            .setMaxStreams(maxStreams)
            .build()
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            if (loadCount.incrementAndGet() >= expectedLoads) {
                // all loaded
            }
        }
    }

    /**
     * Load SFX resources into SoundPool. Call once with your SFX resource ids.
     * Order doesn't matter; method is idempotent for already-loaded ids.
     */
    fun loadSfx(@RawRes vararg resIds: Int) {
        expectedLoads = resIds.size
        loadCount.set(0)
        resIds.forEach { res ->
            if (!soundMap.containsKey(res)) {
                val sid = soundPool.load(context, res, 1)
                soundMap[res] = sid
            } else {
                // already loaded, count it as loaded
                loadCount.incrementAndGet()
            }
        }
    }

    /** Returns true when all requested SFX have finished loading (best-effort). */
    fun areSfxReady(): Boolean = loadCount.get() >= expectedLoads

    /** Play a short SFX previously loaded via loadSfx */
    fun playSfx(@RawRes resId: Int, volume: Float = 1f) {
        val sid = soundMap[resId] ?: return
        soundPool.play(sid, volume, volume, 1, 0, 1f)
    }

    /** Start background music (looping) using MediaPlayer */
    fun startBackground(@RawRes musicRes: Int, loop: Boolean = true) {
        stopBackground()
        bgPlayer = MediaPlayer.create(context, musicRes).apply {
            isLooping = loop
            setVolume(0.6f, 0.6f)
            start()
        }
    }

    /** Stop background music */
    fun stopBackground() {
        bgPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        bgPlayer = null
    }

    /** Stop all SFX and release resources */
    fun release() {
        stopBackground()
        soundPool.release()
        soundMap.clear()
    }
}