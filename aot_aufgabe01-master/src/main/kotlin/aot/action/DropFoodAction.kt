package org.example.aot.action

import org.example.aot.agent.AgentState
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Position
import org.example.aot.manager.SimulationManager

class DropFoodAction(
    val agent: AntAgent,
) : Action(agent) {
    override fun execute(
        manager: SimulationManager,
    ): Set<Position> {
        val pos = agent.pos

        val cell = manager.grid.getCell(pos)
        val availableFood = agent.foodAmount

        cell.foodAmount += availableFood
        agent.foodAmount -= availableFood
        agent.state = AgentState.SEARCHING
        agent.lastMoveSuccessful = true
        agent.recentPositions.clear()
        agent.energy = agent.maxEnergy

        return setOf(pos)
    }

}