package dev.son.movie.network.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.service.FirebaseService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class FirebaseRepository(val api: FirebaseDatabase, private val userPreferences: UserPreferences) :
    FirebaseService {
    private val usersRef = api.getReference("users")
    val userId = userPreferences.userId
    override fun register(user: UserId): Observable<UserId> {
        val userId = user.userId.toString()
        return Observable.create { emitter ->
            usersRef.child(userId).setValue(user)
                .addOnSuccessListener { void ->
                    // Đăng ký thành công
                    Log.e("result", "Success")
                    emitter.onNext(user)
                    emitter.onComplete()
                }
                .addOnFailureListener { exception ->
                    // Đăng ký thất bại
                    emitter.onError(exception)
                }
        }.subscribeOn(Schedulers.io())
    }

    override fun getUser(userId: String): Observable<UserId> {
        return Observable.create { emitter ->
            usersRef.child(userId).get()
                .addOnSuccessListener { data ->
                    // Parse the retrieved data into a UserId object (replace with actual parsing code)
                    val user = data.getValue(UserId::class.java)

                    if (user != null) {
                        emitter.onNext(user)
                        emitter.onComplete()
                    } else {
                        emitter.onError(Exception("Failed to parse UserId"))
                    }
                }
                .addOnFailureListener { exception ->
                    // Registration failed
                    emitter.onError(exception)
                }
        }.subscribeOn(Schedulers.io())
    }

    override fun addToList(idMovie: MovieId1, idUser: String): Observable<String> {
        return Observable.create { emitter ->
            val watchedMoviesRef = usersRef.child(idUser).child("watched_movies")

            // Kiểm tra xem ID đã tồn tại trong danh sách chưa
            watchedMoviesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(idMovie.movieId1.toString())){
                        watchedMoviesRef.child(idMovie.movieId1.toString()).removeValue()
                    }
                    else {
                        watchedMoviesRef.push()
                        watchedMoviesRef.child(idMovie.movieId1.toString()).setValue(idMovie)
                    }
                    emitter.onNext(idMovie.movieId1.toString())
                    emitter.onComplete()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Xử lý lỗi nếu cần
                    emitter.onError(databaseError.toException())
                }
            })
        }.subscribeOn(Schedulers.io())
    }


    suspend fun saveDataUser(data: UserId) {
        userPreferences.saveUserData(data)
    }
}

