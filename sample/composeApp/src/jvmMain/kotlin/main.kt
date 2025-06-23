
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nxoim.sample.ui.App
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState()

    Window(
        title = "sample",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)

        LocalDensity.current.let {
            CompositionLocalProvider(
                LocalDensity provides Density(it.density * 0.8f, it.fontScale)
            ) {
                App()

            }
        }
    }
}