package org.example.aot

import androidx.compose.ui.window.application
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Grid
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import org.example.aot.manager.SimulationManager
import org.example.aot.ui.SimulationWindow
import kotlin.random.Random

fun main() = application {
    println("Ant simulation started")

    val grid = Grid(250, 250)

    val manager = SimulationManager(grid = grid, evaporationRate = 0.01f)

    // Agents
    repeat(100) {
        manager.agents.add(AntAgent(pos = Position(25, 225), capacity = 5))
    }

    // Nest
    grid.getCell(Position(25, 225)).isNest = true

    // Obstacles
    grid.getCell(Position(6,4)).capacity = 0
    grid.getCell(Position(6,5)).capacity = 0
    grid.getCell(Position(6,6)).capacity = 0
    grid.getCell(Position(2,6)).capacity = 0
    grid.getCell(Position(3,6)).capacity = 0
    grid.getCell(Position(4,6)).capacity = 0
    grid.getCell(Position(5,6)).capacity = 0
    grid.getCell(Position(2,4)).capacity = 0
    grid.getCell(Position(3,4)).capacity = 0
    grid.getCell(Position(4,4)).capacity = 0
    grid.getCell(Position(5,4)).capacity = 0

    // Food
    grid.getCell(Position(225, 25)).foodAmount = 1000

    SimulationWindow(
        manager = manager,
        onCloseRequest = ::exitApplication
    )

}
