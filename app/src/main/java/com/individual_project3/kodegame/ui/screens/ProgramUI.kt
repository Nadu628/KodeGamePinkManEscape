package com.individual_project3.kodegame.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.individual_project3.kodegame.assets.commands.UiCommand
import com.individual_project3.kodegame.ui.theme.CloudCommandBlock

@Composable
fun CommandPalette(
    onCommandSelected: (UiCommand) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CloudCommandBlock(
            text = "↑",
            modifier = Modifier.clickable { onCommandSelected(UiCommand.MoveUp) }
        )
        CloudCommandBlock(
            text = "↓",
            modifier = Modifier.clickable { onCommandSelected(UiCommand.MoveDown) }
        )
        CloudCommandBlock(
            text = "←",
            modifier = Modifier.clickable { onCommandSelected(UiCommand.MoveLeft) }
        )
        CloudCommandBlock(
            text = "→",
            modifier = Modifier.clickable { onCommandSelected(UiCommand.MoveRight) }
        )

        // later:
        // CloudCommandBlock("LOOP") { onCommandSelected(UiCommand.Loop(...)) }
        // CloudCommandBlock("IF")   { ... }
    }
}

@Composable
fun ProgramTrack(
    program: List<UiCommand>,
    onRemoveAt: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Program",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 70.dp)
                .background(Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            if (program.isEmpty()) {
                Text(
                    "Tap commands above to add",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            } else {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    program.forEachIndexed { index, cmd ->
                        val label = when (cmd) {
                            UiCommand.MoveUp    -> "↑"
                            UiCommand.MoveDown  -> "↓"
                            UiCommand.MoveLeft  -> "←"
                            UiCommand.MoveRight -> "→"
                        }
                        CloudCommandBlock(
                            text = label,
                            modifier = Modifier.clickable { onRemoveAt(index) }
                        )
                    }
                }
            }
        }
    }
}
