package dev.son.movie.network.service

import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import io.reactivex.Observable

interface FirebaseService {
    fun register(userId: UserId): Observable<UserId>
    fun getUser(userId: String): Observable<UserId>

    fun addToList(id: MovieId1, idUser: String): Observable<String>
}