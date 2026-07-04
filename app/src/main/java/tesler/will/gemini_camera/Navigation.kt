package tesler.will.gemini_camera

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route : NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Camera : Route
}
