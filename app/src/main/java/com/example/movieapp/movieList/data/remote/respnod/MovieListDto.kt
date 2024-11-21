package com.example.movieapp.movieList.data.remote.respnod

import MovieDto

data class MovieListDto(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int,
    val total_results: Int
)
