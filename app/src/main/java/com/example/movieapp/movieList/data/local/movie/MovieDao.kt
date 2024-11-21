package com.example.movieapp.movieList.data.local.movie

import android.graphics.Movie
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MovieDao {
    @Upsert
    suspend fun upsertMovieList(movieList: List<MovieEntity>)

    @Query("SELECT * FROM MovieEntity WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity

    @Query("SELECT * FROM MovieEntity WHERE category = :category")
    suspend fun getMovieListByCategory(category: String): List<MovieEntity>

    @Query("SELECT * FROM MovieEntity WHERE title LIKE :query")
    suspend fun searchMovies(query: String): List<MovieEntity>
}
