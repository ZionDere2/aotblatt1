package org.example.aot.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Grid
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import java.util.UUID

@Composable
fun GridRenderer(
    grid: Grid,
    antAgents: List<AntAgent>,
    cellVersions: SnapshotStateMap<Position, Int>,
    selectedPosition: Position?,
    onCellClick: (Position) -> Unit
) {
    val maxCellSize = 50f
    val minCellSize = 5f
    val maxGridSize = 600f
    val largestGridSide = maxOf(grid.width, grid.height).coerceAtLeast(1)
    val cellSize = (maxGridSize / largestGridSide)
        .coerceIn(minCellSize, maxCellSize)
        .dp

    val density = LocalDensity.current
    val cellSizePx = with(density) { cellSize.toPx() }
    val gridWidth = cellSize * grid.width
    val gridHeight = cellSize * grid.height
    val antsByPosition = antAgents.groupBy { it.pos }

    // Read the map so Compose invalidates the canvas when individual cells are refreshed.
    val renderVersion = cellVersions.values.sum()

    Canvas(
        modifier = Modifier
            .size(width = gridWidth, height = gridHeight)
            .pointerInput(cellSizePx, grid.width, grid.height) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        if (event.type == PointerEventType.Release) {
                            val offset = event.changes.firstOrNull()?.position ?: continue
                            val x = (offset.x / cellSizePx).toInt()
                            val y = (offset.y / cellSizePx).toInt()

                            if (x in 0 until grid.width && y in 0 until grid.height) {
                                onCellClick(Position(x, y))
                            }
                        }
                    }
                }
            }
    ) {
        if (renderVersion == Int.MIN_VALUE) return@Canvas

        val showBorder = cellSizePx >= 8f
        val borderStroke = Stroke(width = (cellSizePx * 0.005f).coerceIn(1f, 2f))
        val selectedStroke = Stroke(width = (cellSizePx * 0.12f).coerceIn(1f, 2f))

        for (y in 0 until grid.height) {
            val top = y * cellSizePx

            for (x in 0 until grid.width) {
                val left = x * cellSizePx
                val cell = grid.cells[y][x]
                val position = cell.pos
                val topLeft = Offset(left, top)
                val cellDrawSize = Size(cellSizePx, cellSizePx)

                val baseColor = when {
                    cell.capacity == 0 -> Color.DarkGray
                    cell.isNest -> Color(0xFF8D6E63)
                    else -> Color.LightGray
                }

                val homeStrength = cell.getPheromoneStrength(PheromoneType.HOME)
                val foodStrength = cell.getPheromoneStrength(PheromoneType.FOOD)
                val pheromoneColor = Color(
                    red = 0f,
                    green = foodStrength.coerceIn(0f, 1f),
                    blue = homeStrength.coerceIn(0f, 1f)
                )
                val finalColor = if (cell.capacity == 0) {
                    baseColor
                } else {
                    lerp(baseColor, pheromoneColor, maxOf(homeStrength, foodStrength).coerceIn(0f, 1f))
                }

                drawRect(
                    color = finalColor,
                    topLeft = topLeft,
                    size = cellDrawSize
                )

                if (showBorder) {
                    drawRect(
                        color = Color.Black,
                        topLeft = topLeft,
                        size = cellDrawSize,
                        style = borderStroke
                    )
                }

                if (cell.foodAmount > 0) {
                    val radius = cellSizePx * 0.12f
                    drawCircle(
                        color = Color.Green,
                        radius = radius,
                        center = Offset(
                            x = left + cellSizePx - radius - cellSizePx * 0.08f,
                            y = top + radius + cellSizePx * 0.08f
                        )
                    )
                }

                val ants = antsByPosition[position]
                if (!ants.isNullOrEmpty()) {
                    val radius = cellSizePx * if (ants.size == 1) 0.28f else 0.34f

                    drawCircle(
                        color = colorFromUUID(ants.first().id),
                        radius = radius,
                        center = Offset(left + cellSizePx / 2f, top + cellSizePx / 2f)
                    )

                    if (ants.size > 1) {
                        drawCircle(
                            color = Color.White,
                            radius = radius * 0.35f,
                            center = Offset(
                                left + cellSizePx * 0.28f,
                                top + cellSizePx * 0.72f
                            )
                        )
                    }
                }
            }
        }

        selectedPosition?.let { selected ->
            if (selected.x in 0 until grid.width && selected.y in 0 until grid.height) {
                drawRect(
                    color = Color.Yellow,
                    topLeft = Offset(selected.x * cellSizePx, selected.y * cellSizePx),
                    size = Size(cellSizePx, cellSizePx),
                    style = selectedStroke
                )
            }
        }
    }
}

private fun colorFromUUID(uuid: UUID): Color {
    val hue = (uuid.hashCode() and 0xFFFFFF) % 360

    return Color.hsv(
        hue.toFloat(),
        0.8f,
        0.9f
    )
}
