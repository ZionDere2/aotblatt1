package org.example.aot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Grid
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import org.example.aot.manager.SimulationManager
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SimulationScreen(
    manager: SimulationManager,
    contentPadding: Dp = 0.dp
) {
    var rememberedManager by remember { mutableStateOf(manager) }
    var isRunning by remember { mutableStateOf(false) }
    var simulationSpeed by remember { mutableStateOf(200.milliseconds) }
    // State has changed -> new render
    val stepVersion = remember { mutableStateOf(0) }
    val cellVersions = remember { mutableStateMapOf<Position, Int>() }
    var selectedPosition by remember { mutableStateOf<Position?>(null) }

    // Editor
    var selectedEditorType by remember { mutableStateOf<EditorType?>(null) }

    fun refreshCell(pos: Position) {
        cellVersions[pos] = (cellVersions[pos] ?: 0) + 1
    }

    fun refreshCells(positions: Iterable<Position>) {
        positions.forEach { refreshCell(it) }
    }

    fun runStep() {
        refreshCells(rememberedManager.runSimulationStep())
        stepVersion.value++
    }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            runStep()

            delay(simulationSpeed)
        }
    }

    Row() {
        Column(
            modifier = Modifier.padding(contentPadding)
        ) {
            GridRenderer(
                grid = rememberedManager.grid,
                antAgents = rememberedManager.agents,
                cellVersions = cellVersions,
                selectedPosition = selectedPosition,
                onCellClick =
                    { pos ->

                        val previousSelection = selectedPosition
                        selectedPosition = pos
                        previousSelection?.let { refreshCell(it) }
                        refreshCell(pos)

                        selectedEditorType?.let { editorType ->
                            val cell = rememberedManager.grid.getCell(pos)

                            when (editorType) {
                                EditorType.ANT -> rememberedManager.agents.add(AntAgent(pos = pos))
                                EditorType.NEST -> cell.isNest = !cell.isNest
                                EditorType.OBSTACLE -> cell.capacity = 0
                                EditorType.FOOD -> { cell.foodAmount += 10 }
                                EditorType.PHEROMONE_HOME -> {
                                    cell.increasePheromone(PheromoneType.HOME)
                                    rememberedManager.markPheromoneChanged(pos)
                                }
                                EditorType.PHEROMONE_FOOD -> {
                                    cell.increasePheromone(PheromoneType.FOOD)
                                    rememberedManager.markPheromoneChanged(pos)
                                }
                                EditorType.DELETE -> {
                                    cell.foodAmount = 0
                                    cell.pheromones.clear()
                                    cell.isNest = false
                                    cell.capacity = Int.MAX_VALUE
                                    rememberedManager.agents.removeAll { it.pos == pos }
                                    rememberedManager.markPheromoneChanged(pos)
                                }
                            }

                            refreshCell(pos)
                            stepVersion.value++
                        }
                    },
            )

            Spacer(Modifier.height(8.dp))

            EditorPanel(selectedEditorType = selectedEditorType, onClick = {
                if(it == selectedEditorType) {
                    selectedEditorType = null
                } else {
                    selectedEditorType = it
                }
            })

            Spacer(Modifier.height(8.dp))

            ManagerPanel(
                importJson = { TODO() },
                exportJson = { TODO() },
                resetManager = {
                    rememberedManager = SimulationManager(grid = Grid(rememberedManager.grid.width, rememberedManager.grid.height), evaporationRate = rememberedManager.evaporationRate)
                    cellVersions.clear()
                    stepVersion.value++
                },
                evaporationRate = rememberedManager.evaporationRate,
                increaseEvaporationRate = {
                    rememberedManager.evaporationRate += 0.01f
                    stepVersion.value++
                },
                decreaseEvaporationRate = {
                    rememberedManager.evaporationRate =
                        (rememberedManager.evaporationRate - 0.01f)
                            .coerceAtLeast(0f)
                    stepVersion.value++
                }
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ControlPanel(
                manager = rememberedManager,
                isRunning = isRunning,
                onToggleRunning = {
                    isRunning = !isRunning
                    selectedEditorType = null
                                  },
                onStep = { runStep() },
                increaseSimulationSpeed = { simulationSpeed *= 0.5 },
                decreaseSimulationSpeed = { simulationSpeed *= 2 }
            )

            StatsPanel(
                manager = rememberedManager,
                refreshVersion = stepVersion
            )

            CellStatsPanel(
                cell = selectedPosition?.let { rememberedManager.grid.getCell(it) },
                ants = rememberedManager.agents.filter { ant -> ant.pos == selectedPosition },
                refreshVersion = stepVersion
            )
        }
    }
}
