package com.individual_project3.kodegame.assets.audio

import android.content.Context
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes


class AudioManager(private val ctx: Context) {
    private val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(8)
        .setAudioAttributes(attrs)
        .build()

    // SFX ids
    var idHit: Int = 0; private set
    var idJump: Int = 0; private set
    var idDrop: Int = 0; private set
    var idCollect: Int = 0; private set
    var idButton: Int = 0; private set
    var idSuccess: Int = 0; private set
    var idFail: Int = 0; private set

    private var bgPlayer: MediaPlayer? = null

    // CHANGED: load tracking so UI can wait until SFX are ready
    private var loadedCount = 0
    private var expectedLoads = 0

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loadedCount++
        }
    }

    /**
     * Load SFX resources. Use formats that are safe for raw resources (ogg or wav recommended).
     * CHANGED: this method sets expectedLoads so callers can wait for readiness.
     */
    fun loadSfx(
        @RawRes hit: Int,
        @RawRes jump: Int,
        @RawRes drop: Int,
        @RawRes collect: Int,
        @RawRes button: Int,
        @RawRes success: Int,
        @RawRes fail: Int
    ) {
        expectedLoads = 7
        try {
            idHit = soundPool.load(ctx, hit, 1)
        } catch (e: Resources.NotFoundException) { idHit = 0 }
        try { idJump = soundPool.load(ctx, jump, 1) } catch (e: Exception) { idJump = 0 }
        try { idDrop = soundPool.load(ctx, drop, 1) } catch (e: Exception) { idDrop = 0 }
        try { idCollect = soundPool.load(ctx, collect, 1) } catch (e: Exception) { idCollect = 0 }
        try { idButton = soundPool.load(ctx, button, 1) } catch (e: Exception) { idButton = 0 }
        try { idSuccess = soundPool.load(ctx, success, 1) } catch (e: Exception) { idSuccess = 0 }
        try { idFail = soundPool.load(ctx, fail, 1) } catch (e: Exception) { idFail = 0 }
    }

    fun areSfxReady(): Boolean = loadedCount >= expectedLoads

    fun startBackground(@RawRes musicRes: Int, loop: Boolean = true, volume: Float = 0.35f) {
        if (bgPlayer == null) {
            bgPlayer = MediaPlayer.create(ctx, musicRes)
            bgPlayer?.isLooping = loop
            bgPlayer?.setVolume(volume, volume)
        }
        bgPlayer?.start()
    }

    fun stopBackground() {
        bgPlayer?.pause()
        bgPlayer?.seekTo(0)
    }

    fun release() {
        try { soundPool.release() } catch (_: Exception) {}
        try { bgPlayer?.release() } catch (_: Exception) {}
        bgPlayer = null
    }

    fun playSfx(id: Int, volume: Float = 1f, loop: Int = 0) {
        if (id != 0) soundPool.play(id, volume, volume, 1, loop, 1f)
    }
}
