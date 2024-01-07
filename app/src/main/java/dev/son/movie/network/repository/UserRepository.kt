package dev.son.movie.network.repository

import dev.son.movie.data.local.UserPreferences
import dev.son.movie.network.models.Register
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.user.LoginRequest
import dev.son.movie.network.models.user.TokenResponse
import dev.son.movie.network.models.user.User
import dev.son.movie.network.service.UserAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class UserRepository(val api: UserAPI, private val userPreferences: UserPreferences) {

    fun login(loginRequest: LoginRequest): Observable<TokenResponse> =
        api.login(loginRequest).subscribeOn(Schedulers.io())

    fun register(registrationInfo: Register): Observable<ApiResponse<String>> =
        api.register(registrationInfo).subscribeOn(Schedulers.io())

    fun getCurrentUser(): Observable<ApiResponse<User>> =
        api.getCurrentUser().subscribeOn(Schedulers.io())

    fun upDateUser(user: HashMap<String, Any>): Observable<ApiResponse<String>> =
        api.upDateUser(user).subscribeOn(Schedulers.io())

    fun getFavoriteList(): Observable<ApiResponse<List<Movie>>> =
        api.getFavoriteList().subscribeOn(Schedulers.io())

    fun getCommentedMovies(): Observable<ApiResponse<List<Movie>>> =
        api.getCommentedMovies().subscribeOn(Schedulers.io())

    suspend fun saveToken(token: String) {
        userPreferences.saveToken(token)
    }
}