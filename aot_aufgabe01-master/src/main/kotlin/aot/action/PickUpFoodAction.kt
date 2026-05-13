package org.example.aot.action

import org.example.aot.agent.AntAgent
import org.example.aot.agent.AgentState
import org.example.aot.grid.Position
import org.example.aot.manager.SimulationManager
import kotlin.math.min

class PickUpFoodAction(
    val agent: AntAgent,
) : Action(agent) {
    override fun execute(
        manager: SimulationManager,
    ): Set<Position> {
        val pos = agent.pos

        val cell = manager.grid.getCell(pos)
        val availableFood = cell.foodAmount
        val availableSpace = agent.getFoodSpaceLeft()

        val pickedUpFood = min(availableFood, availableSpace)
        if (pickedUpFood <= 0) {
            return emptySet()
        }

        cell.foodAmount -= pickedUpFood
        agent.foodAmount += pickedUpFood
        agent.state = AgentState.RETURNING
        agent.lastMoveSuccessful = true
        agent.recentPositions.clear()

        return setOf(pos)
    }

}
