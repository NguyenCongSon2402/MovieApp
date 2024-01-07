package dev.son.movie.network.service


import dev.son.movie.network.models.Register
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.user.LoginRequest
import dev.son.movie.network.models.user.TokenResponse
import dev.son.movie.network.models.user.User

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserAPI {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Observable<TokenResponse>
    @POST("signup")
    fun register(
        @Body registrationInfo: Register
    ): Observable<ApiResponse<String>>


    @GET("profile")
    fun getCurrentUser():Observable<ApiResponse<User>>

    @FormUrlEncoded
    @PUT("profile")
    fun upDateUser(@FieldMap updateData: HashMap<String, Any>):Observable<ApiResponse<String>>

    @GET("movies/favorites")
    fun getFavoriteList():Observable<ApiResponse<List<Movie>>>
    @GET("movies/comments")
    fun getCommentedMovies():Observable<ApiResponse<List<Movie>>>
}
