package org.example.aot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.aot.manager.SimulationManager

@Composable
fun ControlPanel(
    manager: SimulationManager,
    isRunning: Boolean,
    onToggleRunning: () -> Unit,
    onStep: () -> Unit,
    increaseSimulationSpeed: () -> Unit,
    decreaseSimulationSpeed: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(onClick = decreaseSimulationSpeed) {
                Icon(Icons.Default.Remove, "Decrease Speed")
            }
            Button(onClick = onToggleRunning) {
                if(isRunning) {
                    Icon(Icons.Default.Pause, "Pause")
                } else {
                    Icon(Icons.Default.PlayArrow, "Play")
                }
            }
            Button(onClick = increaseSimulationSpeed) {
                Icon(Icons.Default.Add, "Increase Speed")
            }
        }

        Button(
            onClick = onStep,
        ) {
            Icon(Icons.Default.SkipNext, "Step")
        }
    }
}
