package com.example.movieapp.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.R
import com.example.movieapp.movieList.presentaion.MovieListUiEvent
import com.example.movieapp.movieList.presentaion.MovieListViewModel
import com.example.movieapp.movieList.presentaion.PopularMoviesScreen
import com.example.movieapp.movieList.presentaion.SearchMoviesScreen
import com.example.movieapp.movieList.presentaion.UpcomingMoviesScreen
import com.example.movieapp.movieList.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    val movieListViewModel = hiltViewModel<MovieListViewModel>()
    val movieListState = movieListViewModel.movieListState.collectAsState().value
    val bottomNavController = rememberNavController()
    val selectedTab = rememberSaveable { mutableStateOf(0) }

    // Observe the selected tab and clear the search query when switching tabs
    LaunchedEffect(selectedTab.value) {
        if (selectedTab.value in listOf(0, 1)) { // If switching to Popular or Upcoming
            movieListViewModel.onEvent(MovieListUiEvent.SearchQueryChanged("")) // Clear search query
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab.value,
                onTabSelected = { index ->
                    selectedTab.value = index
                    when (index) {
                        0 -> bottomNavController.navigate(Screen.PopularMovieList.rout) {
                            popUpTo(Screen.PopularMovieList.rout) { inclusive = true }
                        }
                        1 -> bottomNavController.navigate(Screen.UpcomingMovieList.rout) {
                            popUpTo(Screen.UpcomingMovieList.rout) { inclusive = true }
                        }
                        2 -> bottomNavController.navigate(Screen.Search.rout) {
                            popUpTo(Screen.Search.rout) { inclusive = true }
                        }
                    }
                },
                onEvent = movieListViewModel::onEvent
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    if (selectedTab.value == 2) { // Only show TextField on Search tab
                        TextField(
                            value = movieListState.searchQuery,
                            onValueChange = { query ->
                                movieListViewModel.onEvent(MovieListUiEvent.SearchQueryChanged(query))
                            },
                            placeholder = { Text("Search movies") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Text(
                            text = if (selectedTab.value == 0)
                                stringResource(R.string.popular_movies)
                            else
                                stringResource(R.string.upcoming_movies),
                            fontSize = 20.sp
                        )
                    }
                },
                modifier = Modifier.shadow(2.dp),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    MaterialTheme.colorScheme.inverseOnSurface
                )
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = Screen.PopularMovieList.rout
            ) {
                composable(Screen.PopularMovieList.rout) {
                    PopularMoviesScreen(
                        navController = navController,
                        movieListState = movieListState,
                        onEvent = movieListViewModel::onEvent
                    )
                }
                composable(Screen.UpcomingMovieList.rout) {
                    UpcomingMoviesScreen(
                        navController = navController,
                        movieListState = movieListState,
                        onEvent = movieListViewModel::onEvent
                    )
                }
                composable(Screen.Search.rout) {
                    SearchMoviesScreen(
                        movieListState = movieListState,
                        navController = navController,
                        onEvent = movieListViewModel::onEvent
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onEvent: (MovieListUiEvent) -> Unit
) {
    val items = listOf(
        BottomItem(
            title = stringResource(R.string.popular),
            icon = Icons.Rounded.Movie
        ),
        BottomItem(
            title = stringResource(R.string.upcoming),
            icon = Icons.Rounded.Upcoming
        ),
        BottomItem(
            title = stringResource(R.string.search),
            icon = Icons.Filled.Search
        )
    )

    NavigationBar {
        items.forEachIndexed { index, bottomItem ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = bottomItem.icon,
                        contentDescription = bottomItem.title,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                label = {
                    Text(
                        text = bottomItem.title,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    }
}

data class BottomItem(
    val title: String, val icon: ImageVector
)
