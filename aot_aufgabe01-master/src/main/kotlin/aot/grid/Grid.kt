package org.example.aot.grid

class Grid(
    val width: Int,
    val height: Int,
) {
    val cells = Array(height) { y ->
        Array(width) { x ->
            Cell(Position(x, y))
        }
    }

    fun getCell(pos: Position): Cell {
        return cells[pos.y][pos.x]
    }

    fun getCellOrNull(pos: Position): Cell? {
        if (pos.x !in 0..<width) return null
        if (pos.y !in 0..<height) return null

        return cells[pos.y][pos.x]
    }
}