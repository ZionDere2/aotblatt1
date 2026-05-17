package org.example.aot.agent

import org.example.aot.action.Action
import org.example.aot.action.DropFoodAction
import org.example.aot.action.MoveAction
import org.example.aot.action.PickUpFoodAction
import org.example.aot.grid.Position
import org.example.aot.item.PheromoneType
import org.example.aot.perception.CellView
import org.example.aot.perception.Perception
import java.util.UUID

class AntAgent(
    val id: UUID = UUID.randomUUID(),
    var capacity: Int = 5,
    var pos: Position,
    var maxEnergy: Int = Int.MAX_VALUE
) {

    var state = AgentState.SEARCHING

    var foodAmount = 0

    var energy: Int = maxEnergy

    var lastMoveSuccessful = false

    var lastDirection: Direction? = null

    val recentPositions = ArrayDeque<Position>()

    fun step(perception: Perception): Action {

        val currentCellView = perception.currentCell

        val neighbors = perception.neighbors

        val accessibleNeighbors =
            neighbors.filter { (_, cell) ->
                cell.isAccessible &&
                        cell.pos != recentPositions.lastOrNull()
            }.ifEmpty {
                neighbors.filter { (_, cell) ->
                    cell.isAccessible
                }
            }

        when (state) {

            AgentState.SEARCHING -> {

                // Check if current cell has food
                if (currentCellView.foodAmount > 0 && !currentCellView.isNest) {
                    return PickUpFoodAction(this)
                }

                // Check if neighbor cell has food
                val directionToFood = accessibleNeighbors.entries
                    .filter { (_, cell) ->
                        cell.foodAmount > 0 && !cell.isNest
                    }
                    .maxByOrNull { (_, cell) -> cell.foodAmount }
                    ?.key

                if (directionToFood != null) {
                    return MoveAction(this, directionToFood)
                }

                // Follow food pheromones with randomness
                return MoveAction(
                    this,
                    chooseDirectionWithRandomness(
                        accessibleNeighbors,
                        PheromoneType.FOOD
                    )
                )
            }

            AgentState.RETURNING -> {

                // Check if current cell is nest
                if (currentCellView.isNest) {
                    return DropFoodAction(this)
                }

                // Check if neighbor cell is nest
                val directionToNest = accessibleNeighbors.entries
                    .firstOrNull { (_, cell) -> cell.isNest }
                    ?.key

                if (directionToNest != null) {
                    return MoveAction(this, directionToNest)
                }

                // Follow home pheromones with randomness
                return MoveAction(
                    this,
                    chooseDirectionWithRandomness(
                        accessibleNeighbors,
                        PheromoneType.HOME
                    )
                )
            }
        }
    }

    private fun chooseDirectionWithRandomness(
        accessibleNeighbors: Map<Direction, CellView>,
        pheromoneType: PheromoneType,
        randomness: Float = 0.2f
    ): Direction {

        // Random exploration
        if (Math.random() < randomness) {
            return accessibleNeighbors.keys.random()
        }

        // Only consider cells with pheromones
        val pheromoneNeighbors = accessibleNeighbors.entries
            .filter {
                (it.value.pheromones[pheromoneType] ?: 0f) > 0f
            }

        // No pheromones nearby -> random movement
        if (pheromoneNeighbors.isEmpty()) {
            if (
                lastDirection != null &&
                accessibleNeighbors.containsKey(lastDirection) &&
                Math.random() < 1
            ) {
                return lastDirection!!
            }

            return accessibleNeighbors.keys.random()
        }

        // Follow strongest pheromone
        return pheromoneNeighbors
            .maxByOrNull {
                it.value.pheromones[pheromoneType] ?: 0f
            }!!
            .key
    }

    fun rememberPosition(position: Position) {
        recentPositions.add(position)
        if (recentPositions.size > 10) {
            recentPositions.removeFirst()
        }
    }

    fun getFoodSpaceLeft(): Int {
        return capacity - foodAmount
    }
}