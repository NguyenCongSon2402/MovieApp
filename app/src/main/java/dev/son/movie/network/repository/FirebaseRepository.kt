package dev.son.movie.network.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.service.FirebaseService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class FirebaseRepository(val api: FirebaseDatabase,private val userPreferences: UserPreferences) :
    FirebaseService {
    private val usersRef = api.getReference("users")

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

    suspend fun saveDataUser(data: UserId) {
        userPreferences.saveUserData(data)
    }
}

