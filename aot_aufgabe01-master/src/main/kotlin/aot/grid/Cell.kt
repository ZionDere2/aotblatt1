package org.example.aot.grid

import org.example.aot.item.PheromoneType
import org.example.aot.manager.SimulationManager

class Cell(
    val pos: Position
) {
    var foodAmount: Int = 0
    val pheromones = mutableMapOf<PheromoneType, Float>()

    var capacity: Int = Int.MAX_VALUE

    var isNest: Boolean = false

    fun getPheromoneStrength(type: PheromoneType): Float {
        return pheromones[type]?:0f
    }

    fun increasePheromone(type: PheromoneType, strength: Float = 0.1f) {
        pheromones[type] = ((pheromones[type] ?: 0f) + strength).coerceAtMost(1f)
    }

    fun decreasePheromone(type: PheromoneType, strength: Float = 0.1f) {
        pheromones[type] = ((pheromones[type] ?: 0f) - strength).coerceAtLeast(0f)
    }

    fun isAccessible(): Boolean {
        return capacity > 0
    }

    fun getAntAgentsAmount(manager: SimulationManager): Int {
        return manager.agents.filter { it.pos == pos }.size
    }
}