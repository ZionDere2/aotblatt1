package org.example.aot.grid

data class Position(
    var x: Int,
    var y: Int
) {
    override fun toString(): String {
        return "($x, $y)"
    }
}