package dev.son.movie.network.service

import dev.son.movie.network.models.AddMovieRequestBody
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.search.Search
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchApi {
    @GET("movies/search/{query}")
    fun searchMovies(@Path("query") query: String): Observable<ApiResponse<List<Movie>>>


    @GET("movies/latest")
    fun getMoviesLatest(): Observable<ApiResponse<List<Movie>>>

    @POST("movies")
    fun addMovie(@Body movie:AddMovieRequestBody): Observable<ApiResponse<String>>


    @PUT("movies/{movieId}")
    fun updateMovie(@Path("movieId") movieId:String,@Body movie:AddMovieRequestBody): Observable<ApiResponse<Movie>>


    @DELETE("movies/{movieId}")
    fun deleteMovie(@Path("movieId") movieId:String): Observable<ApiResponse<String>>
}