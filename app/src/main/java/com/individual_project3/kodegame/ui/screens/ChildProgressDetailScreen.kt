package com.individual_project3.kodegame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.individual_project3.kodegame.KodeGameApp
import com.individual_project3.kodegame.LocalizedString
import com.individual_project3.kodegame.R
import com.individual_project3.kodegame.assets.audio.AudioManager
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.data.progress.ProgressRecord   // <- adjust package if needed
import com.individual_project3.kodegame.ui.theme.CloudButtonTwo
import com.individual_project3.kodegame.ui.viewModel.ChildProgressViewModel

@Composable
fun ChildProgressScreen(
    childId: Int,
    navController: NavController
) {
    val context = LocalContext.current

    val audio = KodeGameApp.audio
    LaunchedEffect(Unit) {
        audio.loadSfx(R.raw.sfx_button_click)
    }

    val viewModel: ChildProgressViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getInstance(context)
                val dao = db.progressDao()
                @Suppress("UNCHECKED_CAST")
                return ChildProgressViewModel(dao) as T
            }
        }
    )

    val records by viewModel.progressRecords.collectAsState()
    val bubbleFont = FontFamily(Font(R.font.poppins_bold))

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFB3E5FC),
            Color(0xFFB2FF59)
        )
    )

    LaunchedEffect(childId) {
        viewModel.loadProgress(childId)
    }

    // ------- FULLSCREEN BACKGROUND WRAPPER -------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ------------ TOP BAR ------------

            Text(
                LocalizedString(R.string.child_progress),
                fontSize = 28.sp,
                fontFamily = bubbleFont,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            // ------------ CHARTS OR EMPTY STATE ------------
            if (records.isEmpty()) {
                Text(
                    LocalizedString(R.string.no_progress),
                    color = Color.White,
                    fontSize = 18.sp
                )
            } else {
                Text(
                    LocalizedString(R.string.strawberries_per_level),
                    fontSize = 22.sp,
                    fontFamily = bubbleFont,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                LevelsLineChart(records)

                Spacer(Modifier.height(24.dp))

                Text(
                    LocalizedString(R.string.strawberries_per_level_bar),
                    fontSize = 22.sp,
                    fontFamily = bubbleFont,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                StrawberriesBarChart(records)
            }

            Spacer(Modifier.height(32.dp))

            // ------------ BACK BUTTON ------------
            CloudButtonTwo(
                LocalizedString(R.string.back_to_dashboard),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                audio.play(R.raw.sfx_button_click)
                navController.popBackStack()
            }
        }

    }
}



// ---------------------------------------------------------
// LINE CHART – MPAndroidChart
// X = level, Y = strawberries
// ---------------------------------------------------------
@Composable
fun LevelsLineChart(records: List<ProgressRecord>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = records.map { rec ->
                Entry(rec.level.toFloat(), rec.strawberries.toFloat())
            }

            val dataSet = LineDataSet(entries, "Strawberries per Level").apply {
                color = Color(0xFF3F51B5).toArgb()
                setCircleColor(Color(0xFF3F51B5).toArgb())
                valueTextColor = Color.Black.toArgb()
                lineWidth = 3f
                circleRadius = 6f
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

// ---------------------------------------------------------
// BAR CHART – MPAndroidChart
// X = level, Y = strawberries
// ---------------------------------------------------------
@Composable
fun StrawberriesBarChart(records: List<ProgressRecord>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp)),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = records.map { rec ->
                BarEntry(rec.level.toFloat(), rec.strawberries.toFloat())
            }

            val dataSet = BarDataSet(entries, "Strawberries").apply {
                color = Color(0xFFFF9800).toArgb()
                valueTextColor = Color.Black.toArgb()
                valueTextSize = 12f
            }

            chart.data = BarData(dataSet)
            chart.invalidate()
        }
    )
}
