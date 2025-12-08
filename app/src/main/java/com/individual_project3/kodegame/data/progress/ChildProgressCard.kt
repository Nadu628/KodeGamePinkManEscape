package com.individual_project3.kodegame.data.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.R

@Composable
fun ChildProgressCard(
    child: ChildWithProgress,
    onClick: () -> Unit
) {
    val bubbleRegular = FontFamily(Font(R.font.poppins_regular))
    val bubbleBold = FontFamily(Font(R.font.poppins_bold))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = child.name,
                fontSize = 20.sp,
                fontFamily = bubbleBold,
                color = Color.Black
            )

            Text(
                text = "Levels Completed: ${child.totalLevels}",
                fontSize = 16.sp,
                fontFamily = bubbleRegular,
                color = Color.Black
            )

            Text(
                text = "Strawberries: ${child.totalStrawberries}",
                fontSize = 16.sp,
                fontFamily = bubbleRegular,
                color = Color.Black
            )
        }
    }
}

