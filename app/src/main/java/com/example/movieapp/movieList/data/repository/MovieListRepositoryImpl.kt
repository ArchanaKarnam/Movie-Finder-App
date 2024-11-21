package com.example.movieapp.movieList.data.repository

import com.example.movieapp.movieList.data.local.movie.MovieDatabase
import com.example.movieapp.movieList.data.mappers.toMovie
import com.example.movieapp.movieList.data.mappers.toMovieEntity
import com.example.movieapp.movieList.data.remote.MovieApi
import com.example.movieapp.movieList.domain.model.Movie
import com.example.movieapp.movieList.domain.repository.MovieListRepository
import com.example.movieapp.movieList.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject


class MovieListRepositoryImpl @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDatabase: MovieDatabase
) : MovieListRepository {

    private val apiKey = "3ca0eed060ff67e83846d597f2a93672" // Make sure this is your valid API key

    override suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {

            emit(Resource.Loading(true))

            val localMovieList = movieDatabase.movieDao.getMovieListByCategory(category)

            val shouldLoadLocalMovie = localMovieList.isNotEmpty() && !forceFetchFromRemote

            if (shouldLoadLocalMovie) {
                emit(Resource.Success(
                    data = localMovieList.map { movieEntity ->
                        movieEntity.toMovie(category)
                    }
                ))

                emit(Resource.Loading(false))
                return@flow
            }

            val movieListFromApi = try {
                movieApi.getMoviesList(category, page)
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading movies"))
                return@flow
            }

            val movieEntities = movieListFromApi.results.let {
                it.map { movieDto ->
                    movieDto.toMovieEntity(category)
                }
            }

            movieDatabase.movieDao.upsertMovieList(movieEntities)

            emit(Resource.Success(
                movieEntities.map { it.toMovie(category) }
            ))
            emit(Resource.Loading(false))

        }
    }

    override suspend fun searchMovie(
        query: String,
        forceFetchFromRemote: Boolean,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return flow {
            // Emit loading state
            emit(Resource.Loading(true))

            // Try to get search results from the local database
            val localSearchResults = movieDatabase.movieDao.searchMovies(query)

            // Determine if we should use local results
            val useLocalResults = localSearchResults.isNotEmpty() && !forceFetchFromRemote


            if (useLocalResults) {
                // Convert local results to Movie and emit success
                val movies = localSearchResults.map { movieEntity ->
                    movieEntity.toMovie(category = "search") // Ensure you are passing the correct category
                }
                emit(Resource.Success(data = movies))
                emit(Resource.Loading(false))
                return@flow
            }

            // Fetch search results from the remote API
            val searchResultsFromApi = try {
                movieApi.searchMovies(apiKey,query, page)
            } catch (e: IOException) {
                // Handle IO exception
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading search results"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                // Handle HTTP exception
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading search results"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                // Handle any other exceptions
                e.printStackTrace()
                emit(Resource.Error(message = "Error loading search results"))
                emit(Resource.Loading(false))
                return@flow
            }

            // Convert API results to MovieEntity and update local database
            val searchResultEntities = searchResultsFromApi.results.map { movieDto ->
                movieDto.toMovieEntity(category = "search") // Adjust category as needed
            }

            movieDatabase.movieDao.upsertMovieList(searchResultEntities)

            // Convert MovieEntity to Movie and emit success
            val movies = searchResultEntities.map { movieEntity ->
                movieEntity.toMovie(category = "search") // Ensure correct category
            }
            emit(Resource.Success(data = movies))
            emit(Resource.Loading(false))
        }
    }


    override suspend fun getMovie(id: Int): Flow<Resource<Movie>> {
        return flow {

            emit(Resource.Loading(true))

            val movieEntity = movieDatabase.movieDao.getMovieById(id)

            if (movieEntity != null) {
                emit(
                    Resource.Success(movieEntity.toMovie(movieEntity.category))
                )

                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error("Error no such movie"))

            emit(Resource.Loading(false))


        }
    }
}

