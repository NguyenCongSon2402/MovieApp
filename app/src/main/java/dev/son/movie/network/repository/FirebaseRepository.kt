package dev.son.movie.network.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.models.user.ViewingHistory
import dev.son.movie.network.service.FirebaseService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class FirebaseRepository(
    val api: FirebaseDatabase,
    val storage: FirebaseStorage,
    private val userPreferences: UserPreferences
) :
    FirebaseService {
    private val usersRef = api.getReference("users")
    private val commentsRef = api.getReference("comment")
    private val storageRef = storage

    override fun register(user: UserId): Observable<UserId> {
        val userId = user.userId.toString()
        return Observable.create { emitter ->
            usersRef.child(userId).setValue(user)
                .addOnSuccessListener { void ->
                    emitter.onNext(user)
                    emitter.onComplete()
                }
                .addOnFailureListener { exception ->
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

    override fun addToList(id: MovieId1, idUser: String): Observable<String> {
        return Observable.create { emitter ->
            val watchedMoviesRef = usersRef.child(idUser).child("watched_movies")

            // Kiểm tra xem ID đã tồn tại trong danh sách chưa
            watchedMoviesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(id.movieId1.toString())) {
                        watchedMoviesRef.child(id.movieId1.toString()).removeValue()
                    } else {
                        watchedMoviesRef.child(id.movieId1.toString()).setValue(id)
                    }
                    emitter.onNext(id.movieId1.toString())
                    emitter.onComplete()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Xử lý lỗi nếu cần
                    emitter.onError(databaseError.toException())
                }
            })
        }.subscribeOn(Schedulers.io())
    }

    override fun addToFavorite(id: MovieId1, idUser: String): Observable<String> {
        return Observable.create { emitter ->
            val favoriteMoviesRef = usersRef.child(idUser).child("favorite_movies")

            // Kiểm tra xem ID đã tồn tại trong danh sách chưa
            favoriteMoviesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(id.movieId1.toString())) {
                        favoriteMoviesRef.child(id.movieId1.toString()).removeValue()
                    } else {
                        favoriteMoviesRef.child(id.movieId1.toString()).setValue(id)
                    }
                    emitter.onNext(id.movieId1.toString())
                    emitter.onComplete()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Xử lý lỗi nếu cần
                    emitter.onError(databaseError.toException())
                }
            })
        }.subscribeOn(Schedulers.io())
    }


    override fun getComment(idMovie: String): Single<MutableList<UserIdComment>> {
        return Single.create { emitter ->
            val getCommentRef = commentsRef.child(idMovie)
            getCommentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val comments = mutableListOf<UserIdComment>()
                        snapshot.children.forEach { userSnapshot ->
                            val userIdComment = userSnapshot.getValue(UserIdComment::class.java)
                            userIdComment?.let { comments.add(it) }
                        }
                        emitter.onSuccess(comments)
                    } else {
                        emitter.onSuccess(mutableListOf()) // Trả về danh sách trống nếu không có dữ liệu
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi nếu cần
                    emitter.onError(error.toException())
                }
            })
        }
    }


    override fun addComment(
        idMovie: String,
        userIdComment: UserIdComment
    ): Observable<UserIdComment> {
        return Observable.create { emitter ->
            commentsRef.child(idMovie).child(userIdComment.commentId.toString())
                .setValue(userIdComment).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        emitter.onNext(userIdComment) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete()
                    } else {
                        val exception = task.exception ?: Exception("Unknown error")
                        emitter.onError(exception) // Gửi lỗi nếu có lỗi xảy ra
                    }
                }
        }
    }

    override fun getMyList(idUser: String): Observable<ArrayList<MovieId1>> {
        return Observable.create { emitter ->
            val getMyListRef = usersRef.child(idUser).child("watched_movies")
            getMyListRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myList = ArrayList<MovieId1>()
                    myList.clear()
                    if (snapshot.exists()) {
                        snapshot.children.forEach { item ->
                            val movieId1 = item.getValue(MovieId1::class.java)
                            movieId1?.let { myList.add(it) }
                        }
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete()
                    } else {
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete() // Trả về danh sách trống nếu không có dữ liệu
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

            })
        }.subscribeOn(Schedulers.io())
    }

    override fun getFavoriteList(idUser: String): Observable<ArrayList<MovieId1>> {
        return Observable.create { emitter ->
            val getMyListRef = usersRef.child(idUser).child("favorite_movies")
            getMyListRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myList = ArrayList<MovieId1>()
                    myList.clear()
                    if (snapshot.exists()) {
                        snapshot.children.forEach { item ->
                            val movieId1 = item.getValue(MovieId1::class.java)
                            movieId1?.let { myList.add(it) }
                        }
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete()
                    } else {
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete() // Trả về danh sách trống nếu không có dữ liệu
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

            })
        }.subscribeOn(Schedulers.io())
    }

    override fun getHistory(idUser: String): Observable<ArrayList<ViewingHistory>> {
        return Observable.create { emitter ->
            val getMyListRef = usersRef.child(idUser).child("viewing_history")
            getMyListRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val myList = ArrayList<ViewingHistory>()
                    myList.clear()
                    if (snapshot.exists()) {
                        snapshot.children.forEach { item ->
                            val movieId1 = item.getValue(ViewingHistory::class.java)
                            movieId1?.let { myList.add(it) }
                        }
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete()
                    } else {
                        emitter.onNext(myList) // Gửi commentId về khi thêm dữ liệu thành công
                        emitter.onComplete() // Trả về danh sách trống nếu không có dữ liệu
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

            })
        }.subscribeOn(Schedulers.io())
    }

    override fun upDateUser(idUser: String?, User: HashMap<String, Any>): Observable<UserId> {
        return Observable.create { emitter ->
            val updateUserRef = idUser?.let { usersRef.child(it) }
            updateUserRef?.updateChildren(User)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    usersRef.child(idUser).get().addOnSuccessListener { data ->
                        // Parse the retrieved data into a UserId object (replace with actual parsing code)
                        val user = data.getValue(UserId::class.java)

                        if (user != null) {
                            emitter.onNext(user)
                            emitter.onComplete()
                        } else {
                            emitter.onError(Exception("Failed to parse UserId"))
                        }
                    }.addOnFailureListener { exception ->
                            // Registration failed
                            emitter.onError(exception)
                        }
                } else {
                    emitter.onError(Exception("Cập nhật người dùng không thành công. Xin vui Lòng thử lại!"))
                }
            }
        }.subscribeOn(Schedulers.io())
    }

    override fun upLoadImage(img: Uri, id: String): Observable<String> {
        return Observable.create { emitter ->
            val uploadTask = storageRef.getReference("avatars/$id")
            uploadTask.putFile(img).addOnSuccessListener { taskSnapshot ->
                // Lấy đường dẫn ảnh sau khi tải lên thành công
                uploadTask.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUri = uri.toString()
                    emitter.onNext(downloadUri) // Trả về đường link
                    emitter.onComplete()
                }.addOnFailureListener { exception ->
                    emitter.onError(exception) // Gửi lỗi đến observable nếu có lỗi xảy ra khi lấy đường dẫn
                }
            }.addOnFailureListener { exception ->
                emitter.onError(exception) // Gửi lỗi đến observable nếu có lỗi xảy ra khi tải ảnh lên Firebase Storage
            }
        }.subscribeOn(Schedulers.io())
    }


    suspend fun saveDataUser(data: UserId) {
        userPreferences.saveUserData(data)
    }
}

