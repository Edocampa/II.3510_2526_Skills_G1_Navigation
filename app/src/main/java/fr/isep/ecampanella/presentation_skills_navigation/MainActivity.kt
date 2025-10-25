package fr.isep.ecampanella.presentation_skills_navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow


/**
 * This is the sealed class that manage the routes for the side drawer menu
 *
 */
sealed class DrawerDestination(
    val route: String, //navigation route ID to display screens
    val title: String, //name of the shown screen
    val icon: ImageVector, //icons to show near the screen name
) {
    object Home : DrawerDestination("home", "Home", Icons.Default.Home)
    object Search : DrawerDestination("search", "Search", Icons.Default.Search)
    object Library : DrawerDestination("library", "Your Library", Icons.Default.List)
    object LikedSongs : DrawerDestination("liked", "Liked Songs", Icons.Default.Favorite)
    object CreatePlaylist : DrawerDestination("create", "Create Playlist", Icons.Default.Add)
    object YourEpisodes : DrawerDestination("episodes", "Your Episodes", Icons.Default.PlayArrow)
}

/**
 * This is the sealed class that manage the routes for tabs in the home screen
 */
sealed class TabDestination(val route: String, val title: String) {
    object Songs : TabDestination("songs", "Songs")
    object Albums : TabDestination("albums", "Albums")
    object Artists : TabDestination("artists", "Artists")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MusicDrawerApp()
            }
        }
    }
}

/**
 * This will display a custom header with all user infos and other things
 */

@Composable
fun MusicDrawerApp() {
    // Reminder: TODO
}
