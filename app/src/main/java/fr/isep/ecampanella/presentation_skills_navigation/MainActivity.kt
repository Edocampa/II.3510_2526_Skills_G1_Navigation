package fr.isep.ecampanella.presentation_skills_navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp



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
//displays the content of tabs with cards
@Composable
fun TabScreenContent(title: String, items: List<String>) {

    //vertical layout that fills the screen and allows scrolling
    Column(
        modifier = Modifier
            .fillMaxSize()                     //full available screen space
            .background(Color(0xFF121212))     //Dark background color
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) //column scrollable
    ) {

        //title at the top
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        //Loop through each item and display it in a card
        items.forEach { item ->

            //Each item is inside a card with padding around it
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF282828)
                )
            ) {

                //Row inside the card holding icon + text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //Icon changes depending on the tab title
                    Icon(
                        imageVector = when(title) {
                            "Songs" -> Icons.Default.PlayArrow
                            "Albums" -> Icons.Default.Star
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = Color(0xFF1DB954),     // Spotify-like green color
                        modifier = Modifier.size(40.dp)
                    )

                    //Horizontal spacing between icon and text
                    Spacer(modifier = Modifier.width(16.dp))

                    //Display the item text
                    Text(
                        text = item,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


 //displays a centered title and description text.
@Composable
fun ScreenContent(title: String, description: String) {

    //vertical layout container centered both horizontally and vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, //Align children center horizontally
        verticalArrangement = Arrangement.Center            //Place children in the vertical center
    ) {

        //Main title text
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge, //Typography for large headlines
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        //Space between title and description
        Spacer(modifier = Modifier.height(16.dp))

        //Description text below the title
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}
