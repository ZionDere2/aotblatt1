package org.example.aot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

enum class EditorType {
    ANT,
    NEST,
    OBSTACLE,
    FOOD,
    PHEROMONE_FOOD,
    PHEROMONE_HOME,
    DELETE,
}

@Composable
fun EditorPanel(
    selectedEditorType: EditorType?,
    onClick: (EditorType) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ant
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray)
                .border(
                    width = if (selectedEditorType == EditorType.ANT) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.ANT) })
        ) {
            Image(
                painter = painterResource("ant.png"),
                contentDescription = "Ant",
                modifier = Modifier.size(32.dp),
            )
        }

        // Nest
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFF8D6E63))
                .border(
                    width = if (selectedEditorType == EditorType.NEST) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.NEST) })
        ) {}

        // Obstacle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color.DarkGray)
                .border(
                    width = if (selectedEditorType == EditorType.OBSTACLE) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.OBSTACLE) })
        ) {}


        // Food
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray)
                .border(
                    width = if (selectedEditorType == EditorType.FOOD) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.FOOD) })
        ) {
            FoodComposable(cellSize = 50.dp)
        }

        // Home Pheromone
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color(red = 0f, green = 0f, blue = 0.8f))
                .border(
                    width = if (selectedEditorType == EditorType.PHEROMONE_HOME) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.PHEROMONE_HOME) })
        ) {}

        // Food Pheromone
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color(red = 0f, green = 0.8f, blue = 0f))
                .border(
                    width = if (selectedEditorType == EditorType.PHEROMONE_FOOD) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.PHEROMONE_FOOD) })
        ) {}

        // Delete
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(Color.LightGray)
                .border(
                    width = if (selectedEditorType == EditorType.DELETE) 3.dp else 1.dp,
                    color = Color.Black
                )
                .clickable(onClick = { onClick(EditorType.DELETE) })
        ) {
            Icon(Icons.Default.Delete, "Delete")
        }
    }
}