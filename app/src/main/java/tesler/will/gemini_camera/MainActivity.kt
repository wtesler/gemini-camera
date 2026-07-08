package tesler.will.gemini_camera

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import tesler.will.gemini_camera.ui.CameraScreen
import tesler.will.gemini_camera.ui.HomeScreen
import tesler.will.gemini_camera.ui.SettingsScreen
import tesler.will.gemini_camera.ui.theme.Gemini_cameraTheme

@Suppress("UNCHECKED_CAST")
class MainActivity : ComponentActivity() {
    private var currentIntentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentIntentState = intent
        enableEdgeToEdge()
        setContent {
            Gemini_cameraTheme {
                val backStack = rememberNavBackStack(Route.Home as NavKey)

                LaunchedEffect(currentIntentState) {
                    if (currentIntentState?.action == "tesler.will.gemini_camera.ACTION_OPEN_CAMERA") {
                        if (backStack.lastOrNull() != Route.Camera) {
                            backStack.add(Route.Camera)
                        }
                    }
                }
                
                val entryProvider = entryProvider {
                    entry<Route.Home> {
                        HomeScreen(
                            onNavigateToCamera = {
                                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                    entry<Route.Settings> {
                        SettingsScreen()
                    }
                    entry<Route.Camera> {
                        CameraScreen()
                    }
                }

                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        item(
                            selected = backStack.lastOrNull() is Route.Home,
                            onClick = { 
                                if (backStack.lastOrNull() !is Route.Home) {
                                    backStack.clear()
                                    backStack.add(Route.Home)
                                }
                            },
                            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                            label = { Text("Home") }
                        )
                        item(
                            selected = backStack.lastOrNull() is Route.Settings,
                            onClick = { 
                                if (backStack.lastOrNull() !is Route.Settings) {
                                    backStack.clear()
                                    backStack.add(Route.Settings)
                                }
                            },
                            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
                            label = { Text("Settings") }
                        )
                    }
                ) {
                    NavDisplay(
                        backStack = backStack,
                        entryProvider = { key -> entryProvider(key as Route) as NavEntry<NavKey> },
                        onBack = { backStack.removeLastOrNull() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntentState = intent
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainPreview() {
    Gemini_cameraTheme {
        Text("Gemini Camera App")
    }
}
