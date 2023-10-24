package dev.son.movie.network.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.service.FirebaseService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton


class FirebaseRepository(val api: FirebaseDatabase) : FirebaseService {
    private val auth = FirebaseAuth.getInstance()
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

}

