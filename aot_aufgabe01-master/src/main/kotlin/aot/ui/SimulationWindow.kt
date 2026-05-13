package org.example.aot.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import org.example.aot.manager.SimulationManager
import java.util.prefs.Preferences

private const val WINDOW_POSITION_X_KEY = "windowPositionX"
private const val WINDOW_POSITION_Y_KEY = "windowPositionY"

private val windowPreferences: Preferences =
    Preferences.userNodeForPackage(SimulationManager::class.java)

@Composable
fun SimulationWindow(
    manager: SimulationManager,
    onCloseRequest: () -> Unit
) {
    val padding = 32.dp

    val windowState = rememberWindowState(
        position = loadWindowPosition(),
        size = DpSize.Unspecified
    )

    Window(
        onCloseRequest = {
            saveWindowPosition(windowState.position)
            onCloseRequest()
        },
        title = "Grid Simulation",
        state = windowState
    ) {
        SimulationScreen(
            manager = manager,
            contentPadding = padding / 2
        )
    }
}

private fun loadWindowPosition(): WindowPosition {
    val x = windowPreferences.getFloat(WINDOW_POSITION_X_KEY, Float.NaN)
    val y = windowPreferences.getFloat(WINDOW_POSITION_Y_KEY, Float.NaN)

    if (x.isNaN() || y.isNaN()) {
        return WindowPosition.PlatformDefault
    }

    return WindowPosition.Absolute(x.dp, y.dp)
}

private fun saveWindowPosition(position: WindowPosition) {
    if (position is WindowPosition.Absolute) {
        windowPreferences.putFloat(WINDOW_POSITION_X_KEY, position.x.value)
        windowPreferences.putFloat(WINDOW_POSITION_Y_KEY, position.y.value)
    }
}
