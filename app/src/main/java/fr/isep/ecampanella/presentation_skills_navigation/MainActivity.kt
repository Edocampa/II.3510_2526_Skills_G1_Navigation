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
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import android.net.http.SslCertificate.saveState
import android.net.http.SslCertificate.restoreState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
/**
 * This is the sealed class that manage the routes for the side drawer menu
 *
 */
sealed class DrawerDestination(
    val route: String, //navigation route ID to display screens
    val title: String, //name of the shown screen
    val icon: ImageVector, //icons to show near the screen name
    val badge: String? = null // indicator that shows the number of notifications
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
 *
 *
 * It's the main function that manages the entire app
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicDrawerApp() {
    //navController for navigation and drawerState to open/close drawer
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route //Tracks current route

    // Side drawer with menu items

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MusicDrawerContent(
                currentRoute = currentRoute,
                onDestinationClick = { route ->
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        Scaffold(
            // TopBar with title and hamburger icon to open drawer
            topBar = {
                TopAppBar(
                    title = { Text("Spotify Demo") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1DB954), // Spotify green
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            //This is the main content with all screens
            NavigationHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}


// The Drawer Content that represents the side menu

@Composable
fun MusicDrawerContent(
    currentRoute: String?,
    onDestinationClick: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF121212)
    ) {
        Column(
            // This is the main navigation items like Home,Search,Library
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // In this point there will be an Header: TODO (reminder)


            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: Main Navigation
            val mainDestinations = listOf(
                DrawerDestination.Home,
                DrawerDestination.Search,
                DrawerDestination.Library
            )

            mainDestinations.forEach { destination ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.title,
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = destination.title,
                            color = Color.White
                        )
                    },
                    selected = currentRoute == destination.route,
                    onClick = { onDestinationClick(destination.route) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF1DB954).copy(alpha = 0.3f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )

            // SECTION 2: Library
            Text(
                text = "YOUR LIBRARY",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )

            //This is the library section like Liked songs,Create Playlist,Episodes

            val libraryDestinations = listOf(
                DrawerDestination.LikedSongs,
                DrawerDestination.CreatePlaylist,
                DrawerDestination.YourEpisodes
            )

            libraryDestinations.forEach { destination ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.title,
                            tint = if (destination.route == "liked_songs") {
                                Color(0xFF1DB954)
                            } else {
                                Color.White
                            }
                        )
                    },
                    label = {
                        Text(
                            text = destination.title,
                            color = Color.White
                        )
                    },
                    badge = if (destination.badge != null) {
                        {
                            Badge {
                                Text(text = destination.badge)
                            }
                        }
                    } else null,
                    selected = currentRoute == destination.route,
                    onClick = { onDestinationClick(destination.route) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF1DB954).copy(alpha = 0.3f),
                        unselectedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Push footer to bottom
            Spacer(modifier = Modifier.weight(1f))

            // In this point there will be a Footer: TODO (reminder)
        }
    }
}

@Composable
fun DrawerHeader() {
    //header section of the Drawer
    Row(
        modifier = Modifier
            .fillMaxWidth() //entire drawer width
            .background(Color(0xFF1DB954)) //Spotify-style green
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        //Profile picture placeholder section
        Box(
            modifier = Modifier
                .size(60.dp) //profile image area
                .clip(CircleShape) //Clip the box into a circle shape
                .background(Color.White),
            contentAlignment = Alignment.Center //Center the icon inside the circle
        ) {
            //avatar placeholder
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile", // Accessibility description
                tint = Color(0xFF1DB954),
                modifier = Modifier.size(40.dp)
            )
        }

        // Spacer adds horizontal space between the profile icon and the text
        Spacer(modifier = Modifier.width(16.dp))

        //User information
        Column {
            // Display the user's name
            Text(
                text = "John Doe", // Placeholder username
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Display the user type or subscription plan
            Text(
                text = "Premium User", // Placeholder user role or plan
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}



// The NavigationHost defines all app routes and screens
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DrawerDestination.Home.route,
        modifier = modifier
    ) {
        composable(DrawerDestination.Home.route) {
            // This will be the route for the home screen with tabs
        }
        composable(DrawerDestination.Search.route) {
            ScreenContent("Search", "Search for music, podcasts and more")
        }
        composable(DrawerDestination.Library.route) {
            ScreenContent("Your Library", "Your playlists and saved content")
        }
        composable(DrawerDestination.LikedSongs.route) {
            ScreenContent("Liked Songs", "All your favorite tracks in one place")
        }
        composable(DrawerDestination.CreatePlaylist.route) {
            ScreenContent("Create Playlist", "Create a new playlist")
        }
        composable(DrawerDestination.YourEpisodes.route) {
            ScreenContent("Your Episodes", "Your saved podcast episodes")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithTabs() {
    //separate NavController just for navigating between tab screens
    val tabNavController = rememberNavController()

    //list of tabs to represents screens (Songs, Albums, Artists)
    val tabs = listOf(
        TabDestination.Songs,
        TabDestination.Albums,
        TabDestination.Artists
    )

    //Observe the current back stack entry to know which tab is active
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine which tab index corresponds to the current route
    // If no match is found, default to the first tab using the elvis
    val selectedTabIndex = tabs.indexOfFirst { it.route == currentRoute }
        .takeIf { it != -1 } ?: 0

    //Root layout for the entire Home screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {

        // TAB ROW SECTION (Top navigation)
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,  // Highlights the active tab
            containerColor = Color(0xFF1DB954),
            contentColor = Color.White
        ) {
            // Create a Tab composable for each item in the `tabs` list
            tabs.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        //When the user clicks a tab, navigate to its route
                        tabNavController.navigate(destination.route) {
                            // Pop back up to the first tab to prevent deep navigation stack buildup
                            popUpTo(tabs[0].route) {
                                saveState = true // Keep previous state if revisiting
                            }
                            launchSingleTop = true // Avoid multiple copies of the same destination
                            restoreState = true    // Restore saved state of tab if available
                        }
                    },
                    text = {
                        // Display the tab's title text
                        Text(
                            text = destination.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTabIndex == index)
                                FontWeight.Bold else FontWeight.Normal // Highlight selected tab
                        )
                    }
                )
            }
        }

        // TAB CONTENT SECTION
        NavHost(
            navController = tabNavController,             // Controller for tab navigation
            startDestination = TabDestination.Songs.route, // Default tab when opening the screen
            modifier = Modifier.fillMaxSize()
        ) {

            //SONGS TAB
            composable(TabDestination.Songs.route) {
                TabScreenContent(
                    title = "Songs",
                    items = listOf(
                        "Bohemian Rhapsody - Queen",
                        "Stairway to Heaven - Led Zeppelin",
                        "Imagine - John Lennon",
                        "Smells Like Teen Spirit - Nirvana",
                        "Billie Jean - Michael Jackson",
                        "Hotel California - Eagles",
                        "Sweet Child O' Mine - Guns N' Roses"
                    )
                )
            }

            //ALBUMS TAB
            composable(TabDestination.Albums.route) {
                TabScreenContent(
                    title = "Albums",
                    items = listOf(
                        "Abbey Road - The Beatles",
                        "The Dark Side of the Moon - Pink Floyd",
                        "Thriller - Michael Jackson",
                        "Back in Black - AC/DC",
                        "Rumours - Fleetwood Mac",
                        "Led Zeppelin IV - Led Zeppelin"
                    )
                )
            }

            //ARTISTS TAB
            composable(TabDestination.Artists.route) {
                TabScreenContent(
                    title = "Artists",
                    items = listOf(
                        "The Beatles",
                        "Queen",
                        "Led Zeppelin",
                        "Pink Floyd",
                        "The Rolling Stones",
                        "AC/DC",
                        "Nirvana"
                    )
                )
            }
        }
    }
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
