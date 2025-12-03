package com.individual_project3.kodegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.commands.PlayerState
import com.individual_project3.kodegame.assets.commands.Pos
import com.individual_project3.kodegame.ui.screens.GameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameScreen()
        }

    }
}
