package com.individual_project3.kodegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.individual_project3.kodegame.game.Maze
import com.individual_project3.kodegame.game.PlayerState
import com.individual_project3.kodegame.game.Pos
import com.individual_project3.kodegame.ui.Navigation
import com.individual_project3.kodegame.ui.screens.GameScreen
import com.individual_project3.kodegame.ui.theme.KodeGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sampleMaze = Maze(
            width = 6,
            height = 8,
            walls = setOf(
                // some walls (x,y)
                Pos(1, 1), Pos(2, 1), Pos(3, 1),
                Pos(4, 3), Pos(2, 4)
            ),
            enemies = setOf(Pos(3, 3), Pos(5, 6)),
            strawberries = setOf(Pos(0, 2), Pos(4, 2)),
            start = Pos(0, 0),
            goal = Pos(5, 7)
        )

        val initialPlayer = PlayerState(
            pos = sampleMaze.start,
            strawberries = 0,
            startPos = sampleMaze.start
        )

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    GameScreen(
                        maze = sampleMaze,
                        initialPlayerState = initialPlayer,
                        onExit = { finish() } // simple exit action
                    )
                }
            }
        }

    }
}
