package org.example.aot.manager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.aot.action.Action
import org.example.aot.agent.AntAgent
import org.example.aot.agent.Direction
import org.example.aot.grid.Grid
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import org.example.aot.perception.CellView
import org.example.aot.perception.Perception

class SimulationManager(
    val grid: Grid,
    evaporationRate: Float
) {
    var tick: Int = 0
    var evaporationRate by mutableStateOf(evaporationRate)

    val agents = mutableListOf<AntAgent>()
    val actionsQueue = mutableListOf<Action>()
    private val activePheromonePositions = mutableSetOf<Position>()

    fun markPheromoneChanged(pos: Position) {
        val cell = grid.getCell(pos)

        if (cell.pheromones.values.any { it > 0f }) {
            activePheromonePositions += pos
        } else {
            activePheromonePositions -= pos
        }
    }

    fun runSimulationStep(): Set<Position> {
        tick ++
        val changedPositions = mutableSetOf<Position>()
        actionsQueue.clear()
        agents.forEach { it.lastMoveSuccessful = false }

        // Collect actions for each agent
        for (agent in agents) {
            val perception = createPerceptionForAgent(agent)
            val action = agent.step(perception)
            actionsQueue.add(action)
        }

        // Execute each action FCFS
        for (action in actionsQueue) {
            changedPositions += action.execute(manager = this)
        }

        // Decrease energy for each agent and kill if energy = 0
        agents.forEach { it.energy-- }
        agents.removeAll { it.energy <= 0 }

        changedPositions += evaporatePheromones()

        return changedPositions
    }

    private fun evaporatePheromones(): Set<Position> {
        val changedPositions = mutableSetOf<Position>()

        for (pos in activePheromonePositions.toList()) {
            val cell = grid.getCell(pos)
            var hasActivePheromone = false

            for(type in PheromoneType.entries) {
                if(cell.pheromones[type] != null) {
                    cell.decreasePheromone(type, evaporationRate)
                    changedPositions += cell.pos

                    if ((cell.pheromones[type] ?: 0f) > 0f) {
                        hasActivePheromone = true
                    } else {
                        cell.pheromones.remove(type)
                    }
                }
            }

            if (!hasActivePheromone) {
                activePheromonePositions -= pos
            }
        }

        return changedPositions
    }

    private fun createPerceptionForAgent(antAgent: AntAgent): Perception {
        val pos = antAgent.pos
        val cell = grid.getCell(pos)

        // Current CellView
        val currentCellView = CellView(
            pos = cell.pos,
            isNest = cell.isNest,
            foodAmount = cell.foodAmount,
            pheromones = cell.pheromones,
            isAccessible = cell.isAccessible()
            )

        // Neighbor Cell Views
        val neighbors = mutableMapOf<Direction, CellView>();

        for (direction in Direction.entries) {
            val neighborPos = direction.applyTo(pos)

            val neighborCell = grid.getCellOrNull(neighborPos) ?: continue

            neighbors[direction] = CellView(
                pos = neighborCell.pos,
                isNest = neighborCell.isNest,
                foodAmount = neighborCell.foodAmount,
                pheromones = neighborCell.pheromones,
                isAccessible = neighborCell.isAccessible()
            )

        }

        return Perception(currentCell = currentCellView, neighbors = neighbors)
    }
}
