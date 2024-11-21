import com.example.movieapp.movieList.domain.model.Movie

data class MovieDto(
    val adult: Boolean?,
    val backdrop_path: String?,
    val genre_ids: List<Int>?,
    val id: Int?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Int?,
    val category: String?
) {

    // Add category parameter to the function
    fun toMovie(): Movie {
        return Movie(
            adult = adult ?: false,
            backdrop_path = backdrop_path ?: "",
            genre_ids = genre_ids ?: emptyList(),
            id = id ?: -1,
            original_language = original_language ?: "",
            original_title = original_title ?: "",
            overview = overview ?: "",
            popularity = popularity ?: 0.0,
            poster_path = poster_path ?: "",
            release_date = release_date ?: "",
            title = title ?: "",
            video = video ?: false,
            vote_average = vote_average ?: 0.0,
            vote_count = vote_count ?: 0,
            category = category ?:"" // Use the provided category parameter
        )
    }
}
