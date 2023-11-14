package dev.son.movie.network.service

import android.net.Uri
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.models.user.ViewingHistory
import io.reactivex.Observable
import io.reactivex.Single

interface FirebaseService {
    fun register(userId: UserId): Observable<UserId>
    fun getUser(userId: String): Observable<UserId>

    fun addToList(id: MovieId1, idUser: String): Observable<String>
    fun addToFavorite(id: MovieId1, idUser: String): Observable<String>
    fun getComment(idUser: String): Single<MutableList<UserIdComment>>
    fun addComment(idMovie: String, userIdComment: UserIdComment): Observable<UserIdComment>
    fun getMyList(idUser: String): Observable<ArrayList<MovieId1>>
    fun getFavoriteList(idUser: String): Observable<ArrayList<MovieId1>>
    fun getHistory(idUser: String): Observable<ArrayList<ViewingHistory>>
    fun upDateUser(idUser: String?, idUser1: HashMap<String, Any>): Observable<UserId>
    fun upLoadImage(img: Uri, id: String): Observable<String>
}