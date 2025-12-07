package com.individual_project3.kodegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.assets.commands.Maze
import com.individual_project3.kodegame.assets.commands.PlayerState
import com.individual_project3.kodegame.assets.commands.Pos
import com.individual_project3.kodegame.game.DifficultyMode
import com.individual_project3.kodegame.ui.Navigation
import com.individual_project3.kodegame.ui.screens.DifficultyScreen
import com.individual_project3.kodegame.ui.screens.GameScreen
import com.individual_project3.kodegame.ui.theme.KodeGameTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val audio = AudioManager(this)
        audio.loadSfx(
            R.raw.sfx_button_click,
            R.raw.sfx_jump,
            R.raw.sfx_drop,
            R.raw.sfx_hit,
            R.raw.sfx_collecting_fruit,
            R.raw.sfx_success
        )

        audio.startBackground(R.raw.sfx_game_music)
        setContent {
            Navigation()
        }

    }

    @Composable
    fun GameApp(){
        MaterialTheme {
            //GameScreen()
        }
    }
}
