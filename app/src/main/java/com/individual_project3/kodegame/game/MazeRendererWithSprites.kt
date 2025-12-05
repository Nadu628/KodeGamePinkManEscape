package com.individual_project3.kodegame.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.commands.Pos
import com.individual_project3.kodegame.assets.sprites.SpriteManager

@Composable
fun MazeRendererWithSprites(
    maze: Maze,
    playerImage: ImageBitmap?,
    playerAnimX: Float,
    playerAnimY: Float,
    spriteManager: SpriteManager,
    tileSizeDp: Dp
) {
    val density = LocalDensity.current
    val tileSizePx = with(density) { tileSizeDp.toPx() }
    val canvasWidthDp = tileSizeDp * maze.width
    val canvasHeightDp = tileSizeDp * maze.height

    Box(
        modifier = Modifier
            .width(canvasWidthDp)
            .height(canvasHeightDp)
            .background(Color(0xFF87CEFA)) // sky
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 1. Tiles
            for (y in 0 until maze.height) {
                for (x in 0 until maze.width) {

                    val pos = Pos(x, y)
                    val px = x * tileSizePx
                    val py = y * tileSizePx

                    val bgColor = when {
                        pos in maze.walls -> Color(0xFFB0BEC5)      // gray walls
                        pos == maze.start -> Color(0xFFC8E6C9)      // green start
                        pos == maze.goal  -> Color(0xFFBBDEFB)      // blue exit
                        else              -> Color(0xFFFFFFFF)      // white path
                    }

                    drawRect(
                        color = bgColor,
                        topLeft = Offset(px, py),
                        size = Size(tileSizePx, tileSizePx)
                    )
                }
            }

            val time = System.currentTimeMillis()

            // 2. Strawberries
            val strawberryFrame = spriteManager.strawberryFrames
                .takeIf { it.isNotEmpty() }
                ?.let { frames ->
                    val idx = ((time / 120) % frames.size).toInt()
                    frames[idx]
                }

            strawberryFrame?.let { frame ->
                val srcSize = IntSize(frame.width, frame.height)
                val dstSize = IntSize(tileSizePx.toInt(), tileSizePx.toInt())

                for (berry in maze.strawberries) {
                    val px = berry.x * tileSizePx
                    val py = berry.y * tileSizePx

                    drawImage(
                        image = frame,
                        srcOffset = IntOffset.Zero,
                        srcSize = srcSize,
                        dstOffset = IntOffset(px.toInt(), py.toInt()),
                        dstSize = dstSize
                    )
                }
            }

            // 3. Spikes / hazards
            val spikeFrame = spriteManager.spikeFrames.firstOrNull()
            spikeFrame?.let { frame ->
                val srcSize = IntSize(frame.width, frame.height)
                val dstSize = IntSize(tileSizePx.toInt(), tileSizePx.toInt())

                for (hazard in maze.enemies) {
                    val px = hazard.x * tileSizePx
                    val py = hazard.y * tileSizePx

                    drawImage(
                        image = frame,
                        srcOffset = IntOffset.Zero,
                        srcSize = srcSize,
                        dstOffset = IntOffset(px.toInt(), py.toInt()),
                        dstSize = dstSize
                    )
                }
            }

            // 4. Player sprite
            playerImage?.let { frame ->
                val px = playerAnimX * tileSizePx
                val py = playerAnimY * tileSizePx

                val srcSize = IntSize(frame.width, frame.height)
                val dstSize = IntSize(tileSizePx.toInt(), tileSizePx.toInt())

                drawImage(
                    image = frame,
                    srcOffset = IntOffset.Zero,
                    srcSize = srcSize,
                    dstOffset = IntOffset(px.toInt(), py.toInt()),
                    dstSize = dstSize
                )
            }
        }
    }
}

