package org.example.aot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.aot.agent.AntAgent
import java.util.*

@Composable
fun AntComposable(
    antAgent: AntAgent,
    cellSize: Dp
) {
    Image(
        painter = painterResource("ant.png"),
        contentDescription = "Ant",
        modifier = Modifier.size((cellSize.value * 0.65f).dp),
        colorFilter = ColorFilter.tint(
            colorFromUUID(antAgent.id)
        )
    )
}

private fun colorFromUUID(uuid: UUID): Color {

    val hue = (uuid.hashCode() and 0xFFFFFF) % 360

    return Color.hsv(
        hue.toFloat(),
        0.8f,
        0.9f
    )
}
