package com.individual_project3.kodegame.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.individual_project3.kodegame.R

@Composable
fun CloudCommandBlock(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
) {
    val bubbleFont = FontFamily(Font(R.font.poppins_regular))

    Box(
        modifier = modifier
            .height(56.dp)
            .widthIn(min = 72.dp)
            .clip(RoundedCornerShape(18.dp))
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.95f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 6.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontFamily = bubbleFont,
                color = Color.Black
            )
        }
    }
}
