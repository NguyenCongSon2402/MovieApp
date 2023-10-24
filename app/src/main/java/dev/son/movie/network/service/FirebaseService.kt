package dev.son.movie.network.service

import com.google.firebase.auth.AuthResult
import dev.son.movie.network.models.user.UserId
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface FirebaseService {
    fun register(userId: UserId): Observable<UserId>
}