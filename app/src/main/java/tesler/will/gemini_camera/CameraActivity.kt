package tesler.will.gemini_camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import tesler.will.gemini_camera.ui.CameraScreen
import tesler.will.gemini_camera.ui.theme.Gemini_cameraTheme

@Suppress("UNCHECKED_CAST")
class CameraActivity : ComponentActivity() {
    private var currentIntentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentIntentState = intent
        enableEdgeToEdge()
        setContent {
            Gemini_cameraTheme {
                CameraScreen(
                    onClose = {
                        finish()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CameraPreview() {
    Gemini_cameraTheme {
        Text("Gemini Camera App")
    }
}
