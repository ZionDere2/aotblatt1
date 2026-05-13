package org.example.aot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ManagerPanel (
    importJson: () -> Unit,
    exportJson: () -> Unit,
    resetManager: () -> Unit,
    evaporationRate: Float,
    increaseEvaporationRate: () -> Unit,
    decreaseEvaporationRate: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(50.dp)) {
        Button(
            onClick = importJson,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("Import\nJSON", textAlign = TextAlign.Center)
        }

        Button(
            onClick = exportJson,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("Export\nJSON", textAlign = TextAlign.Center)
        }

        Button(
            onClick = resetManager,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text("Reset", textAlign = TextAlign.Center)
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Button(
                onClick = decreaseEvaporationRate,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease evaporation rate")
            }

            Button(
                onClick = {},
                modifier = Modifier.fillMaxHeight()
            ) {
                Text("Evaporationr.\n${"%.2f".format(evaporationRate)}", textAlign = TextAlign.Center)
            }

            Button(
                onClick = increaseEvaporationRate,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase evaporation rate")
            }
        }
    }
}
