package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.data.progress.ChildProgressEntity
import com.individual_project3.kodegame.ui.theme.bubbleFont
import kotlinx.coroutines.launch

@Composable
fun ParentDashboardScreen() {

    val scope = rememberCoroutineScope()
    var progress by remember { mutableStateOf<ChildProgressEntity?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            progress = App.db.progressDao().getChildProgress("child1")
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Child Progress", fontFamily = bubbleFont, fontSize = 28.sp)

        Spacer(Modifier.height(20.dp))

        progress?.let { p ->
            Text("Total Strawberries: ${p.totalStrawberries}", fontSize = 20.sp, fontFamily = bubbleFont)
            Text("Levels Completed: ${p.levelsCompleted}", fontSize = 20.sp, fontFamily = bubbleFont)
        } ?: Text("No progress yet.", fontSize = 18.sp, fontFamily = bubbleFont)
    }
}
