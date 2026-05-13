package org.example.aot.perception

import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType


data class CellView(
    val pos: Position,
    val isNest: Boolean,
    val foodAmount: Int,
    val pheromones: Map<PheromoneType, Float>,
    val isAccessible: Boolean
) {
}