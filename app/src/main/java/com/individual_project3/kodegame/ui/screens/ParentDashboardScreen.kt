package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.ui.viewModel.ParentDashboardViewModel
import androidx.lifecycle.ViewModelProvider
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.LocalizedString
import com.individual_project3.kodegame.data.progress.ChildProgressCard
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo


@Composable
fun ParentDashboardScreen(
    parentId: Int,
    navController: NavController
) {
    val context = LocalContext.current

    val audio = KodeGameApp.audio
    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }

    val bubbleFont = FontFamily(Font(R.font.poppins_bold))

    // -------- VIEWMODEL SETUP --------
    val viewModel: ParentDashboardViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(context)
                val dao = db.progressDao()
                @Suppress("UNCHECKED_CAST")
                return ParentDashboardViewModel(dao) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(parentId) {
        viewModel.loadChildren(parentId)
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xffb3e5fc),
            Color(0xffb2ff59)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                LocalizedString(R.string.parent_dashboard), fontSize = 32.sp, fontFamily = bubbleFont, color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            CloudButtonTwo(
                text = LocalizedString(R.string.add_child), modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.navigate("parent_registration_screen")
            }

            Spacer(Modifier.height(20.dp))

            when {
                uiState.loading -> Text(
                    LocalizedString(R.string.loading), fontFamily = bubbleFont, color = Color.White
                )

                uiState.children.isEmpty() -> Text(
                    LocalizedString(R.string.no_children_found), fontFamily = bubbleFont, color = Color.White
                )

                else -> {
                    uiState.children.forEach { child ->

                        // Child Card (tappable)
                        ChildProgressCard(
                            child = child, onClick = {
                                audio.play(R.raw.sfx_button_click)
                                navController.navigate("child_progress/${child.childId}")
                            })

                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            CloudButtonTwo(
                text = LocalizedString(R.string.logout), modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.navigate("pick_user_screen") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

    }
}
