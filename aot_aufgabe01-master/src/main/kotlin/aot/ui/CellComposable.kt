package org.example.aot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Cell
import org.example.aot.item.PheromoneType

@Composable
fun CellComposable(
    cell: Cell,
    antAgents: List<AntAgent>,
    isSelected: Boolean,
    cellSize: Dp,
    onClick: () -> Unit
) {

    val showBorder = cellSize >= 8.dp

    val baseColor = when {
        cell.capacity == 0 -> Color.DarkGray
        cell.isNest -> Color(0xFF8D6E63)
        else -> Color.LightGray
    }

    val homeStrength = cell.getPheromoneStrength(PheromoneType.HOME)
    val foodStrength = cell.getPheromoneStrength(PheromoneType.FOOD)

    // Mixed pheromone color
    val pheromoneColor = Color(
        red = 0f,
        green = foodStrength.coerceIn(0f, 1f),
        blue = homeStrength.coerceIn(0f, 1f)
    )

    // Blend base with pheromones
    val finalColor = if (cell.capacity == 0) {
        baseColor
    } else {
        lerp(
            baseColor,
            pheromoneColor,
            maxOf(homeStrength, foodStrength)
                .coerceIn(0f, 1f)
        )
    }

    val cellModifier = Modifier
        .size(cellSize)
        .background(finalColor)
        .then(
            when {
                !showBorder -> Modifier

                isSelected -> Modifier.border(
                    width = (cellSize.value * 0.12f).coerceIn(1f, 2f).dp,
                    color = Color.Yellow
                )

                else -> Modifier.border(
                    width = (cellSize.value * 0.005f).coerceIn(1f, 2f).dp,
                    color = Color.Black
                )
            }
        )
        .clickable(onClick = onClick)

    Box(
        modifier = cellModifier
    ) {

        if (cell.foodAmount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding((cellSize.value * 0.08f).dp)
            ) {
                FoodComposable(cellSize)
            }
        }

        val visibleAnt = antAgents.firstOrNull()

        if (visibleAnt != null) {
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                AntComposable(visibleAnt, cellSize)
            }
        }

        if (antAgents.size > 1) {
            Text(
                text = antAgents.size.toString(),
                color = Color.White,
                fontSize = (cellSize.value * 0.28f).coerceIn(8f, 16f).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding((cellSize.value * 0.06f).dp)
            )
        }
    }
}