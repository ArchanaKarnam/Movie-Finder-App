package com.example.movieapp.movieList.presentaion

import com.example.movieapp.movieList.domain.model.Movie


data class MovieListState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false, // Add this line

    val popularMovieListPage: Int = 1,
    val upcomingMovieListPage: Int = 1,

    val isCurrentPopularScreen: Boolean = true,

    val popularMovieList: List<Movie> = emptyList(),
    val upcomingMovieList: List<Movie> = emptyList(),

    val searchResults: List<Movie> = emptyList(),
    val searchQuery: String = "", // Add this for the search query

)