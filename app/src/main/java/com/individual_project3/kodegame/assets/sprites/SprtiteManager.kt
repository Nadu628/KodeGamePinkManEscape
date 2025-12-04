package com.individual_project3.kodegame.assets.sprites

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.individual_project3.kodegame.R

class SpriteManager(private val context: Context) {
    var playerIdleFrames: List<ImageBitmap> = emptyList()
        private set
    var playerRunFrames: List<ImageBitmap> = emptyList()
        private set
    var enemyFrames: List<ImageBitmap> = emptyList()
        private set
    var strawberryFrames: List<ImageBitmap> = emptyList()
        private set
    var playerJumpFrames: List<ImageBitmap> = emptyList()
        private set
    var playerDropFrames: List<ImageBitmap> = emptyList()
        private set
    var playerHitFrames: List<ImageBitmap> = emptyList()     // added
        private set
    var spikeFrames: List<ImageBitmap> = emptyList()     // added
        private set


    fun preloadAllAsync(scope: CoroutineScope) {
        scope.launch { preloadAll() }
    }

    suspend fun preloadAll() = withContext(Dispatchers.IO) {
        // Replace these drawable ids with your actual resources (full lists)
        playerIdleFrames = listOfNotNull(
            loadBitmap(R.drawable.pink_idle1), loadBitmap(R.drawable.pink_idle2),
            loadBitmap(R.drawable.pink_idle3), loadBitmap(R.drawable.pink_idle4),
            loadBitmap(R.drawable.pink_idle5), loadBitmap(R.drawable.pink_idle6),
            loadBitmap(R.drawable.pink_idle7), loadBitmap(R.drawable.pink_idle8),
            loadBitmap(R.drawable.pink_idle9), loadBitmap(R.drawable.pink_idle10),
            loadBitmap(R.drawable.pink_idle11)
        )

        playerRunFrames = listOfNotNull(
            loadBitmap(R.drawable.pink_run1), loadBitmap(R.drawable.pink_run2),
            loadBitmap(R.drawable.pink_run3), loadBitmap(R.drawable.pink_run4),
            loadBitmap(R.drawable.pink_run5), loadBitmap(R.drawable.pink_run6),
            loadBitmap(R.drawable.pink_run7), loadBitmap(R.drawable.pink_run8),
            loadBitmap(R.drawable.pink_run9), loadBitmap(R.drawable.pink_run10),
            loadBitmap(R.drawable.pink_run11), loadBitmap(R.drawable.pink_run12)
        )

        playerJumpFrames = listOfNotNull(loadBitmap(R.drawable.pink_jump))

        playerDropFrames = listOfNotNull(loadBitmap(R.drawable.pink_drop))

        playerHitFrames = listOfNotNull(
            loadBitmap(R.drawable.pink_hit1), loadBitmap(R.drawable.pink_hit2),
            loadBitmap(R.drawable.pink_hit3), loadBitmap(R.drawable.pink_hit4),
            loadBitmap(R.drawable.pink_hit5), loadBitmap(R.drawable.pink_hit6),
            loadBitmap(R.drawable.pink_hit7)
        )

        enemyFrames = listOfNotNull(
            loadBitmap(R.drawable.enemy_idle1), loadBitmap(R.drawable.enemy_idle2),
            loadBitmap(R.drawable.enemy_idle3), loadBitmap(R.drawable.enemy_idle4),
            loadBitmap(R.drawable.enemy_idle5), loadBitmap(R.drawable.enemy_idle6),
            loadBitmap(R.drawable.enemy_idle7), loadBitmap(R.drawable.enemy_idle8),
            loadBitmap(R.drawable.enemy_idle9), loadBitmap(R.drawable.enemy_idle10),
            loadBitmap(R.drawable.enemy_idle11)
        )

        strawberryFrames = listOfNotNull(
            loadBitmap(R.drawable.strawberry_idle1), loadBitmap(R.drawable.strawberry_idle2),
            loadBitmap(R.drawable.strawberry_idle3), loadBitmap(R.drawable.strawberry_idle4),
            loadBitmap(R.drawable.strawberry_idle5), loadBitmap(R.drawable.strawberry_idle6),
            loadBitmap(R.drawable.strawberry_idle7), loadBitmap(R.drawable.strawberry_idle8),
            loadBitmap(R.drawable.strawberry_idle9), loadBitmap(R.drawable.strawberry_idle10),
            loadBitmap(R.drawable.strawberry_idle11), loadBitmap(R.drawable.strawberry_idle12),
            loadBitmap(R.drawable.strawberry_idle13), loadBitmap(R.drawable.strawberry_idle14),
            loadBitmap(R.drawable.strawberry_idle15), loadBitmap(R.drawable.strawberry_idle16),
            loadBitmap(R.drawable.strawberry_idle17)
        )

        spikeFrames = listOfNotNull(loadBitmap(R.drawable.spikes))
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

