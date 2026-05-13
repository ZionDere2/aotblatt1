package org.example.aot.action

import org.example.aot.agent.AgentState
import org.example.aot.agent.AntAgent
import org.example.aot.agent.Direction
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import org.example.aot.manager.SimulationManager

class MoveAction(
    val agent: AntAgent,
    val direction: Direction,
) : Action(agent) {
    override fun execute(
        manager: SimulationManager,
    ): Set<Position> {
        val currentPos = agent.pos
        val nextPos = direction.applyTo(currentPos)

        val currentCell = manager.grid.getCell(currentPos)
        val nextCell = manager.grid.getCellOrNull(nextPos) ?: return emptySet()

        // Check if obstacle
        if(!nextCell.isAccessible()) { return emptySet() }

        // Check if already full
        if(nextCell.getAntAgentsAmount(manager) >= nextCell.capacity) { return emptySet() }

        // Drop Pheromone depending on state
        when(agent.state) {
            AgentState.SEARCHING -> currentCell.increasePheromone(PheromoneType.HOME, 0.2f)
            AgentState.RETURNING -> currentCell.increasePheromone(PheromoneType.FOOD, 0.5f)
        }
        manager.markPheromoneChanged(currentPos)

        agent.pos = nextPos
        agent.lastDirection = direction
        agent.lastMoveSuccessful = true

        agent.rememberPosition(currentPos)

        return setOf(currentPos, nextPos)
    }

}
