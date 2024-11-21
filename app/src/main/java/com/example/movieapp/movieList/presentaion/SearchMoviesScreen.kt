package com.example.movieapp.movieList.presentaion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.movieapp.movieList.presentaion.componentes.MovieItem

@Composable
fun SearchMoviesScreen(
    movieListState: MovieListState,
    navController: NavHostController,
    onEvent: (MovieListUiEvent) -> Unit,
) {
    // Extract searchQuery directly from movieListState
    val searchQuery = movieListState.searchQuery

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (movieListState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        onEvent(MovieListUiEvent.SearchQueryChanged(query))
                    },
                    placeholder = { Text("Search movies") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (movieListState.searchResults.isEmpty()) {
                    Text(
                        text = "No results found",
//                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(movieListState.searchResults) { movie ->
                            MovieItem(
                                movie = movie,
                                navHostController = navController
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
