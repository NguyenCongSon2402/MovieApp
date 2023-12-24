package dev.son.movie.network.service

import dev.son.movie.network.models.Favorite.addFavorite
import dev.son.movie.network.models.countries.Countries
import dev.son.movie.network.models.countries.CountriesMovie
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.postcomment.Comment
import dev.son.movie.network.models.rate.RateRespone
import io.reactivex.Observable
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeApi {

    @GET("/v1/api/quoc-gia")
    fun getCountriesMovies(): Observable<Countries>


    @GET("/v1/api/quoc-gia/{countries}")
    fun CountriesMovies(@Path("countries") name: String): Observable<CountriesMovie>

    @GET("genres")
    fun getGenres(): Observable<ApiResponse<List<Genre>>>
    @GET("countries")
    fun getCountries(): Observable<ApiResponse<List<Genre>>>

    @GET("movies/genre/{genreId}")
    fun getMoviesPhimBo(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMoviesPhimLe(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieHoatHinh(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieTvShow(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieVietSub(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieThuyetMinh(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieDangChieu(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieHoanThanh(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/genre/{genreId}")
    fun getMovieSimilar(@Path("genreId") genreId: String): Observable<ApiResponse<List<Movie>>>

    @GET("movies/{movieId}/ratings")
    fun getMovieRateRes(@Path("movieId") movieId: String): Observable<ApiResponse<RateRespone>>

    @FormUrlEncoded
    @POST("movies/{movieId}/ratings")
    fun setMovieRate(
        @Path("movieId") movieId: String,
        @Field("rating") rating: Int
    ): Observable<ApiResponse<RateRespone>>

    @POST("movies/{movieId}/favorites")
    fun addFavorite(@Path("movieId") movieId: String): Observable<ApiResponse<addFavorite>>

    @DELETE("movies/{movieId}/favorites")
    fun removeFavorite(@Path("movieId") movieId: String): Observable<ApiResponse<String>>

    @GET("movies/{id}")
    fun getMovieById(@Path("id") movieId: String): Observable<ApiResponse<Movie>>

    @GET("movies/{movieId}/comments")
    fun getCommentByMovie(@Path("movieId") movieId: String): Observable<ApiResponse<List<Comment>>>
    @GET("movies/genre/{genreId}")
    fun getMovieByCategory(@Path("genreId") genreCode: String): Observable<ApiResponse<List<Movie>>>
    @GET("movies/{movieId}/recommendations")
    fun getMoviesRecommendation(@Path("movieId") movieId: String): Observable<ApiResponse<List<Movie>>>
    @GET("movies/country/{countryId}")
    fun getMoviesByCountry(@Path("countryId") countryCode: String): Observable<ApiResponse<List<Movie>>>

    @FormUrlEncoded
    @POST("movies/{movieId}/comments")
    fun createComment(
        @Path("movieId") movieId: String,
        @Field("comment") comment: String
    ): Observable<ApiResponse<Comment>>
//    @GET("countries")
//    fun getCountries(): Call<ApiResponse<List<Country?>?>?>?


}