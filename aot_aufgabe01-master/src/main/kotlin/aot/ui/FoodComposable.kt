package org.example.aot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FoodComposable(cellSize: Dp) {
    Box(
        modifier = Modifier
            .size((cellSize.value * 0.24f).dp)
            .background(Color.Green, CircleShape)
    )
}
