package org.example.aot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.aot.agent.AntAgent
import org.example.aot.grid.Cell
import org.example.aot.manager.SimulationManager

@Composable
fun StatsPanel(
    manager: SimulationManager,
    refreshVersion: State<Int>,
    modifier: Modifier = Modifier
) {
    key(refreshVersion.value) {
        Card(
            modifier = modifier.padding(16.dp).width(300.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                StatRow("Tick", manager.tick.toString())
                StatRow("Ants", manager.agents.size.toString())
            }
        }
    }
}

@Composable
fun CellStatsPanel(
    cell: Cell?,
    ants: List<AntAgent>,
    refreshVersion: State<Int>,
    modifier: Modifier = Modifier
) {
    key(refreshVersion.value) {
        Card(
            modifier = modifier.padding(horizontal = 16.dp).width(300.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Cell Stats",
                    fontWeight = FontWeight.Bold
                )

                if (cell == null) {
                    Text("No cell selected")
                    return@Column
                }

                StatRow("Position", cell.pos.toString())

                StatRow("Nest", if (cell.isNest) "Yes" else "No")

                StatRow("Capacity", formatCapacity(cell.capacity))

                StatRow("Food", cell.foodAmount.toString())

                StatRow("Pheromone", "")
                cell.pheromones.forEach {
                    StatRow("└ " + it.key.toString(), formatFloat(it.value))
                }

                StatRow("Ants", ants.size.toString())
                ants.forEachIndexed { index, ant ->
                    val label = ant::class.simpleName ?: "Ant"
                    StatRow("└ ${index + 1}. $label", ant.state.toString())
                    StatRow("   └ Food", ant.foodAmount.toString())
                    StatRow("   └ Energy", ant.energy.toString())
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatCapacity(capacity: Int): String {
    return if (capacity == Int.MAX_VALUE) "Unlimited" else capacity.toString()
}

private fun formatFloat(value: Float): String {
    return "%.2f".format(value)
}