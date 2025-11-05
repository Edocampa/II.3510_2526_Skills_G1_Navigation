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


// Defines a sealed class for drawer destinations
// Sealed classes allow having a limited and controlled number of subtypes

sealed class DrawerDestination(
    val route: String, //navigation route ID to display screens
    // The unique identifier used for navigation between screens

    val title: String, //name of the shown screen
    // The name displayed in the UI for this destination

    val icon: ImageVector, //icons to show near the screen name
    // The icon to display next to the screen name in the drawer
) {
    // Singleton object representing the Home screen
    object Home : DrawerDestination("home", "Home", Icons.Default.Home)

    // Singleton object for the search screen
    object Search : DrawerDestination("search", "Search", Icons.Default.Search)

    // Singleton object for the user's music library
    object Library : DrawerDestination("library", "Your Library", Icons.Default.List)

    // Singleton object for the user's liked songs
    object LikedSongs : DrawerDestination("liked", "Liked Songs", Icons.Default.Favorite)

    // Singleton object for creating a new playlist
    object CreatePlaylist : DrawerDestination("create", "Create Playlist", Icons.Default.Add)

    // Singleton object for the user's podcasts (likely podcasts)
    object YourPodcasts : DrawerDestination("podcasts", "Your Podcasts", Icons.Default.PlayArrow)
}

/**
 * This is the sealed class that manage the routes for tabs in the home screen
 */
// Sealed class to manage tabs in the home screen (Songs, Albums, Artists)
sealed class TabDestination(val route: String, val title: String) {
    // Tab to display all songs
    object Songs : TabDestination("songs", "Songs")

    // Tab to display albums
    object Albums : TabDestination("albums", "Albums")

    // Tab to display artists
    object Artists : TabDestination("artists", "Artists")

    // TODO - EXERCISE 1: Add a new "Playlists" tab object
    // Follow the same pattern as Songs, Albums and Artists
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

// Annotation indicating the use of experimental Material3 APIs
@OptIn(ExperimentalMaterial3Api::class)
// Main composable function that manages the entire app structure
@Composable
fun MusicDrawerApp() {
    //navController for navigation and drawerState to open/close drawer
    // Creates and remembers the NavController to manage navigation between screens
    val navController = rememberNavController()

    // Creates and remembers the drawer state (open/closed)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Creates a coroutine scope tied to the composable's lifecycle
    val scope = rememberCoroutineScope()

    // Observes the current entry in the navigation back stack
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Extracts the current route from the current destination
    val currentRoute = currentBackStackEntry?.destination?.route //Tracks current route

    // Side drawer with menu items
    // Creates a modal navigation drawer (side menu that overlays the content)
    ModalNavigationDrawer(
        // Sets the drawer's state
        drawerState = drawerState,

        // Defines the content of the drawer (the menu)
        drawerContent = {
            // Calls the composable that displays drawer menu items
            MusicDrawerContent(
                // Passes the current route to highlight the active item
                currentRoute = currentRoute,

                // Lambda function called when a menu item is clicked
                onDestinationClick = { route ->
                    // Launches a coroutine in the defined scope
                    scope.launch {
                        // Closes the drawer
                        drawerState.close()
                    }
                    // Navigates to the selected route
                    navController.navigate(route) {
                        // Pops up to the start destination to avoid stack buildup
                        popUpTo(navController.graph.findStartDestination().id) {
                            // Saves the state of the popped destinations
                            saveState = true
                        }
                        // Ensures only one instance of the destination exists in the stack
                        launchSingleTop = true
                        // Restores the saved state when returning to a destination
                        restoreState = true
                    }
                }
            )
        }
    ) {
        // Scaffold provides the basic Material Design layout structure
        Scaffold(
            // TopBar with title and hamburger icon to open drawer
            // Defines the top app bar
            topBar = {
                // Creates a top app bar
                TopAppBar(
                    // Sets the title of the app bar
                    title = { Text("Music Demo") },

                    // Defines the navigation icon (hamburger menu)
                    navigationIcon = {
                        // Creates a clickable icon button
                        IconButton(onClick = {
                            // Launches a coroutine when clicked
                            scope.launch {
                                // Checks if drawer is closed
                                if (drawerState.isClosed) {
                                    // Opens the drawer
                                    drawerState.open()
                                } else {
                                    // Closes the drawer if it's open
                                    drawerState.close()
                                }
                            }
                        }) {
                            // Displays the menu icon
                            Icon(
                                // Uses the default menu icon (three horizontal lines)
                                imageVector = Icons.Default.Menu,
                                // Accessibility description for screen readers
                                contentDescription = "Menu"
                            )
                        }
                    },
                    // Customizes the top app bar colors
                    colors = TopAppBarDefaults.topAppBarColors(
                        // Sets the background color
                        containerColor = Color(0xFF1DB954),
                        // Sets the title text color to white
                        titleContentColor = Color.White,
                        // Sets the navigation icon color to white
                        navigationIconContentColor = Color.White
                    )
                )
            }

            // TODO - EXERCISE 2: Add a bottomBar parameter here
            // Create the BottomNavigationBar function first --> TODO at the end of the file

        ) { paddingValues ->
            //This is the main content with all screens
            // Hosts the navigation graph with all app screens
            NavigationHost(
                // Passes the nav controller for navigation
                navController = navController,
                // Applies padding to avoid overlap with top bar
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}


// The Drawer Content that represents the side menu
// Composable function that builds the content of the navigation drawer
@Composable
fun MusicDrawerContent(
    // The current active route to highlight the selected item
    currentRoute: String?,
    // Callback function invoked when a menu item is clicked, receives the route as parameter
    onDestinationClick: (String) -> Unit
) {
    // Material3 component that provides the drawer sheet with predefined styling
    ModalDrawerSheet(
        // Sets the background color of the drawer to dark gray
        drawerContainerColor = Color(0xFF121212)
    ) {
        // Column layout to stack items vertically
        Column(
            // This is the main navigation items like Home,Search,Library
            // Modifier to configure the column's appearance and behavior
            modifier = Modifier
                // Makes the column fill all available space
                .fillMaxSize()
                // Enables vertical scrolling if content exceeds screen height
                .verticalScroll(rememberScrollState())
        ) {

            // Displays the custom header at the top of the drawer
            DrawerHeader()

            // Adds vertical spacing of 16 density-independent pixels
            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: Main Navigation
            // Creates a list containing the main navigation destinations
            val mainDestinations = listOf(
                // Home screen destination
                DrawerDestination.Home,
                // Search screen destination
                DrawerDestination.Search,
                // Library screen destination
                DrawerDestination.Library
            )

            // Iterates through each main destination to create menu items
            mainDestinations.forEach { destination ->
                // Material3 component for drawer menu items
                NavigationDrawerItem(
                    // Lambda that defines the icon for this item
                    icon = {
                        // Icon composable to display the destination's icon
                        Icon(
                            // Uses the icon defined in the destination object
                            imageVector = destination.icon,
                            // Accessibility description for screen readers
                            contentDescription = destination.title,
                            // Sets the icon color to white
                            tint = Color.White
                        )
                    },
                    // Lambda that defines the text label for this item
                    label = {
                        // Text composable to display the destination's title
                        Text(
                            // Uses the title defined in the destination object
                            text = destination.title,
                            // Sets the text color to white
                            color = Color.White
                        )
                    },
                    // Boolean that determines if this item is currently selected/active
                    selected = currentRoute == destination.route,
                    // Lambda invoked when the item is clicked, triggers navigation
                    onClick = { onDestinationClick(destination.route) },
                    // Customizes the colors for selected and unselected states
                    colors = NavigationDrawerItemDefaults.colors(
                        // Color for selected item background
                        selectedContainerColor = Color(0xFF1DB954).copy(alpha = 0.3f),
                        // Transparent background for unselected items
                        unselectedContainerColor = Color.Transparent
                    ),
                    // Adds horizontal and vertical padding around the item
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Horizontal line divider to separate sections
            HorizontalDivider(
                // Adds vertical and horizontal padding around the divider
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                // Semi-transparent gray color for the divider line
                color = Color.Gray.copy(alpha = 0.3f)
            )

            // SECTION 2: Library
            // Section header text for the library section
            Text(
                // Uppercase text for section title
                text = "YOUR LIBRARY",
                // Uses small label typography style from Material theme
                style = MaterialTheme.typography.labelSmall,
                // Gray color to differentiate from menu items
                color = Color.Gray,
                // Adds padding around the section title
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                // Bold font weight for emphasis
                fontWeight = FontWeight.Bold
            )

            //This is the library section like Liked songs,Create Playlist, Podcasts
            // Creates a list containing the library-related destinations
            val libraryDestinations = listOf(
                // Liked songs destination
                DrawerDestination.LikedSongs,
                // Create playlist destination
                DrawerDestination.CreatePlaylist,
                // Your podcasts destination
                DrawerDestination.YourPodcasts
            )

            // Iterates through each library destination to create menu items
            libraryDestinations.forEach { destination ->
                // Material3 component for drawer menu items
                NavigationDrawerItem(
                    // Lambda that defines the icon for this item
                    icon = {
                        // Icon composable to display the destination's icon
                        Icon(
                            // Uses the icon defined in the destination object
                            imageVector = destination.icon,
                            // Accessibility description for screen readers
                            contentDescription = destination.title,
                            // Conditional tint color based on the route
                            tint = if (destination.route == "liked_songs") {
                                // Color for the liked songs icon (heart)
                                Color(0xFF1DB954)
                            } else {
                                // White for all other icons
                                Color.White
                            }
                        )
                    },
                    // Lambda that defines the text label for this item
                    label = {
                        // Text composable to display the destination's title
                        Text(
                            // Uses the title defined in the destination object
                            text = destination.title,
                            // Sets the text color to white
                            color = Color.White
                        )
                    },
                    // Boolean that determines if this item is currently selected/active
                    selected = currentRoute == destination.route,
                    // Lambda invoked when the item is clicked, triggers navigation
                    onClick = { onDestinationClick(destination.route) },
                    // Customizes the colors for selected and unselected states
                    colors = NavigationDrawerItemDefaults.colors(
                        // Color for selected item background
                        selectedContainerColor = Color(0xFF1DB954).copy(alpha = 0.3f),
                        // Transparent background for unselected items
                        unselectedContainerColor = Color.Transparent
                    ),
                    // Adds horizontal and vertical padding around the item
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Push footer to bottom
            // Spacer with weight pushes all subsequent content to the bottom of the screen
            Spacer(modifier = Modifier.weight(1f))

            // Displays the custom footer at the bottom of the drawer
            DrawerFooter()
        }
    }
}

@Composable
fun DrawerHeader() {
    //header section of the Drawer
    Row(
        modifier = Modifier
            .fillMaxWidth() //entire drawer width
            .background(Color(0xFF1DB954))
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

// Composable function that creates the footer section at the bottom of the drawer
@Composable
fun DrawerFooter() {
    // Column layout to stack footer elements vertically
    Column(
        // Modifier to configure the column's appearance
        modifier = Modifier
            // Makes the column span the full width of its container
            .fillMaxWidth()
            // Sets a slightly lighter dark background color than the drawer
            .background(Color(0xFF181818))
            // Adds 16dp padding on all sides
            .padding(16.dp)
    ) {
        // Horizontal divider line at the top of the footer
        HorizontalDivider(
            // Semi-transparent gray color for the divider
            color = Color.Gray.copy(alpha = 0.3f),
            // Adds 12dp padding below the divider
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Row layout to arrange items horizontally
        Row(
            // Makes the row span the full width
            modifier = Modifier.fillMaxWidth(),
            // Distributes space between items
            horizontalArrangement = Arrangement.SpaceBetween,
            // Centers items vertically within the row
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Material button with text styling
            TextButton(onClick = { /* Settings action */ }) {
                // Icon for the settings button
                Icon(
                    // Uses the default settings icon
                    imageVector = Icons.Default.Settings,
                    // Accessibility description for screen readers
                    contentDescription = "Settings",
                    // Gray color for the icon
                    tint = Color.Gray,
                    // Sets the icon size to 20dp
                    modifier = Modifier.size(20.dp)
                )
                // Adds horizontal spacing between icon and text
                Spacer(modifier = Modifier.width(8.dp))
                // Text label for the settings button
                Text(
                    // Button text
                    text = "Settings",
                    // Gray color for the text
                    color = Color.Gray,
                    // Text size of 14 scaled pixels
                    fontSize = 14.sp
                )
            }
        }

        // Adds vertical spacing of 8dp
        Spacer(modifier = Modifier.height(8.dp))

        // Text displaying the app version number
        Text(
            // Version information text
            text = "Version 1.0.0",
            // Semi-transparent gray color (60% opacity)
            color = Color.Gray.copy(alpha = 0.6f),
            // Small text size of 12sp
            fontSize = 12.sp,
            // Centers the text horizontally
            textAlign = TextAlign.Center,
            // Makes the text span the full width to center properly
            modifier = Modifier.fillMaxWidth()
        )

        // Text displaying copyright information
        Text(
            // Copyright notice text
            text = "© 2025 Music Demo",
            // Semi-transparent gray color (60% opacity)
            color = Color.Gray.copy(alpha = 0.6f),
            // Very small text size of 10sp
            fontSize = 10.sp,
            // Centers the text horizontally
            textAlign = TextAlign.Center,
            // Makes the text span the full width to center properly
            modifier = Modifier.fillMaxWidth()
        )
    }
}




// The NavigationHost defines all app routes and screens
// Composable function that sets up the navigation graph for the entire app
@Composable
fun NavigationHost(
    // Navigation controller to manage navigation between screens
    navController: NavHostController,
    // Optional modifier with default empty value for customization
    modifier: Modifier = Modifier
) {
    // NavHost is the container that hosts all navigation destinations
    NavHost(
        // Assigns the navigation controller to manage navigation
        navController = navController,
        // Sets the initial screen that appears when the app starts (Home)
        startDestination = DrawerDestination.Home.route,
        // Applies the modifier passed from parent composable
        modifier = modifier
    ) {
        // Defines a composable destination for the Home route
        composable(DrawerDestination.Home.route) {
            // Displays the Home screen with tabs (Songs, Albums, Artists)
            HomeScreenWithTabs()
        }
        // Defines a composable destination for the Search route
        composable(DrawerDestination.Search.route) {
            // Displays the Search screen with title and description
            ScreenContent("Search", "Search for music, podcasts and more")
        }
        // Defines a composable destination for the Library route
        composable(DrawerDestination.Library.route) {
            // Displays the Library screen with title and description
            ScreenContent("Your Library", "Your playlists and saved content")
        }
        // Defines a composable destination for the Liked Songs route
        composable(DrawerDestination.LikedSongs.route) {
            // Displays the Liked Songs screen with title and description
            ScreenContent("Liked Songs", "All your favorite tracks in one place")
        }
        // Defines a composable destination for the Create Playlist route
        composable(DrawerDestination.CreatePlaylist.route) {
            // Displays the Create Playlist screen with title and description
            ScreenContent("Create Playlist", "Create a new playlist")
        }
        // Defines a composable destination for the Your Podcasts route
        composable(DrawerDestination.YourPodcasts.route) {
            // Displays the Your Podcasts screen with title and description
            ScreenContent("Your Podcasts", "Your saved podcasts")
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

        // TODO EXERCISE 1: Add "Playlists" tab
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

            //TODO - EXERCISE 1: Add a composable for "Playlists" route
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
                        tint = Color(0xFF1DB954),
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

// TODO - EXERCISE 2: Create the BottomNavigationBar composable function
// This function should:
// - Accept currentRoute: String? and onNavigate: (String)
// - Include 3 items for: Home, Search, Library
