package dev.son.movie.network.repository


import dev.son.movie.network.models.AddMovieRequestBody
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.service.SearchApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SearchRepository(val api: SearchApi) {
    fun search(name: String): Observable<ApiResponse<List<Movie>>> =
        api.searchMovies(name).subscribeOn(Schedulers.io())

    fun getMoviesLatest(): Observable<ApiResponse<List<Movie>>> =
        api.getMoviesLatest().subscribeOn(Schedulers.io())
    fun addMovie(movie: AddMovieRequestBody): Observable<ApiResponse<String>> =
        api.addMovie(movie).subscribeOn(Schedulers.io())
    fun updateMovie(movieId: String,movie: AddMovieRequestBody): Observable<ApiResponse<Movie>> =
        api.updateMovie(movieId,movie).subscribeOn(Schedulers.io())
    fun deleteMovie(movieId: String): Observable<ApiResponse<String>> =
        api.deleteMovie(movieId).subscribeOn(Schedulers.io())
}