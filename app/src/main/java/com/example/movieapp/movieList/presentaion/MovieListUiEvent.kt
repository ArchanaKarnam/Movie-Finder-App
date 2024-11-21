package com.example.movieapp.movieList.presentaion

sealed interface MovieListUiEvent {
    data class Paginate(val category: String) : MovieListUiEvent
    object Navigate : MovieListUiEvent
    data class SearchQueryChanged(val query: String) : MovieListUiEvent
}
