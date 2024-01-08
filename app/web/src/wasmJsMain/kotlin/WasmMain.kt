import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.rnett.spellbook.ui.MainPage
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    CanvasBasedWindow("ImageViewer") {
        MainPage()
    }
}