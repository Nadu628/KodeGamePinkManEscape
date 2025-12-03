package com.individual_project3.kodegame.assets.sprites

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.individual_project3.kodegame.R

class SpriteManager(private val context: Context) {
    var playerSprite: ImageBitmap? = null
        private set
    var enemySprite: ImageBitmap? = null
        private set
    var collectibleSprite: ImageBitmap? = null
        private set

    // CHANGED: async preload helper
    fun preloadAllAsync(scope: CoroutineScope) {
        scope.launch { preloadAll() }
    }

    // CHANGED: suspend loader that runs on IO
    suspend fun preloadAll() = withContext(Dispatchers.IO) {
        playerSprite = loadBitmap(R.drawable.player_sprite)
        enemySprite = loadBitmap(R.drawable.enemy_sprite)
        collectibleSprite = loadBitmap(R.drawable.collectible_sprite)
    }

    private fun loadBitmap(@DrawableRes resId: Int): ImageBitmap? {
        return try {
            val bmp = BitmapFactory.decodeResource(context.resources, resId)
            bmp?.asImageBitmap()
        } catch (t: Throwable) {
            null
        }
    }
}
