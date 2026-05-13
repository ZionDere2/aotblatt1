package org.example.aot.action

import org.example.aot.agent.AntAgent
import org.example.aot.grid.Position
import org.example.aot.manager.SimulationManager

abstract class Action(
    val antAgent: AntAgent
) {
    abstract fun execute(
        manager: SimulationManager,
    ): Set<Position>
}