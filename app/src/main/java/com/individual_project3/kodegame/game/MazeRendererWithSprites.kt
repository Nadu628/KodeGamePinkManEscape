package com.individual_project3.kodegame.game


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.individual_project3.kodegame.game.MazeGrid
import com.individual_project3.kodegame.game.TileType
import androidx.compose.ui.geometry.Size
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.commands.Pos
import com.individual_project3.kodegame.assets.sprites.SpriteManager

@Composable
fun MazeRendererWithSprites(
    maze: Maze,
    playerImage: ImageBitmap?,
    playerAnimX: Float,
    playerAnimY: Float,
    spriteManager: SpriteManager
) {
    val tileSizeDp = 18.dp
    val tileSizePx = with(LocalDensity.current) { tileSizeDp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((maze.height * tileSizeDp.value).dp)
            .background(Color(0xFF101010))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height((maze.height * tileSizeDp.value).dp)) {
            for (r in 0 until maze.height) {
                for (c in 0 until maze.width) {
                    val left = c * tileSizePx
                    val top = r * tileSizePx

                    val coord = Pos(r, c)
                    val bgColor = when {
                        maze.walls.contains(coord) -> Color.DarkGray
                        maze.enemies.contains(coord) -> Color.Red
                        maze.strawberries.contains(coord) -> Color(0xFF2E7D32)
                        maze.start == (coord) -> Color.Green
                        maze.goal == (coord) -> Color.Blue
                        else -> Color(0xFF1E1E1E)
                    }

                    drawRect(bgColor, topLeft = Offset(left, top), size = Size(tileSizePx, tileSizePx))
                }
            }



            // draw strawberries and enemies if you have positions in your Maze model
            // omitted here for brevity; use spriteManager.strawberryFrames and spriteManager.enemyFrames

            // draw player using playerImage at animated position
            playerImage?.let {
                val left = playerAnimX * tileSizePx
                val top = playerAnimY * tileSizePx
                drawImage(it, topLeft = Offset(left + 2f, top + 2f))
            } ?: run {
                val left = playerAnimX * tileSizePx
                val top = playerAnimY * tileSizePx
                drawRect(Color.White, topLeft = Offset(left + 2f, top + 2f), size = Size(tileSizePx - 4f, tileSizePx - 4f))
            }
        }
    }
}
