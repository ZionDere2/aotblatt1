package org.example.aot.perception

import org.example.aot.agent.Direction

class Perception(
    val currentCell: CellView,
    val neighbors: Map<Direction, CellView>
) {
}