package dev.son.movie.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.son.movie.network.models.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPreferences.APP_PREFERENCES)

@Suppress("UNCHECKED_CAST")
class UserPreferences @Inject constructor(context: Context) {

    private val mContext = context


    val username: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_NAME]
        }

    val userId: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }


    suspend fun saveUserFullname(fullname: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ID] = fullname
        }
    }


    val userEmail: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
//    val getUserId: Flow<UserId?>
//        get() = mContext.dataStore.data.map { preferences ->
//            UserId(
//                userId = preferences[USER_ID],
//                movieTrackinglist = MovieTrackinglist(
//                    watchedMovies = ArrayList(preferences[WATCHED_MOVIES] ?: emptySet()),
//                    favoriteMovies = ArrayList(
//                        preferences[FAVORITE_MOVIES] ?: emptySet()
//                    )
//                )
//            )
//        }
//
//
//    fun getUserDataFlow(): Flow<UserId> {
//        return mContext.dataStore.data
//            .map { preferences ->
//                UserId(
//                    userId = preferences[USER_ID] ?: "",
//                    email = preferences[USER_EMAIL] ?: "email@gmail.com",
//                    movieTrackinglist = MovieTrackinglist(
//                        watchedMovies = (preferences[WATCHED_MOVIES]
//                            ?: emptySet()) as ArrayList<String>,
//                        favoriteMovies = (preferences[FAVORITE_MOVIES]
//                            ?: emptySet()) as ArrayList<String>
//                    )
//                )
//            }
//    }

    suspend fun saveUserData(user: UserId) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId.toString()
            preferences[USER_NAME] = user.name.toString()
            preferences[USER_EMAIL] = user.email.toString()
            preferences[USER_AVATAR] = user.avatar.toString()
            preferences[USER_DATE_OF_BIRTH] = user.dateOfBirth.toString()
            // Lưu danh sách yêu thích phim (favoriteMovies) - chỉ lưu các khóa
        }
    }
    suspend fun saveMyList(idList: List<String>) {
        mContext.dataStore.edit { preferences ->
            preferences[WATCHED_MOVIES] = idList.toSet()
        }
    }

    suspend fun toggleWatchedMovie(movieId: String) {
        mContext.dataStore.edit { preferences ->
            // Nếu userId trùng khớp, lấy danh sách watchedMovies hiện tại
            val currentWatchedMovies = preferences[WATCHED_MOVIES] ?: emptySet()

            // Kiểm tra xem movieId đã tồn tại trong danh sách chưa
            if (movieId in currentWatchedMovies) {
                // Nếu đã tồn tại, xoá nó đi
                preferences[WATCHED_MOVIES] = currentWatchedMovies - movieId
            } else {
                // Nếu chưa tồn tại, thêm nó vào
                preferences[WATCHED_MOVIES] = currentWatchedMovies + movieId
            }

        }
    }

    suspend fun checkWatchedMovie(movieId: String): Boolean {
        var movieExists = false // Khởi tạo biến để kiểm tra xem movieId đã tồn tại hay chưa
        mContext.dataStore.edit { preferences ->
            // Nếu userId trùng khớp, lấy danh sách watchedMovies hiện tại
            val currentWatchedMovies = preferences[WATCHED_MOVIES] ?: emptySet()
            // Kiểm tra xem movieId đã tồn tại trong danh sách chưa
            if (movieId in currentWatchedMovies) {
                movieExists = true // Đánh dấu rằng movieId đã tồn tại
            }
        }
        return movieExists // Trả về kết quả kiểm tra
    }


    suspend fun clear() {
        mContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        // Use setPreferencesKey to save the lists as Set
        // Use stringSetPreferencesKey to save the lists as String Sets
        private val WATCHED_MOVIES = stringSetPreferencesKey("watched_movies")
        private val FAVORITE_MOVIES = stringSetPreferencesKey("favorite_movies")

        private val USER_ID = stringPreferencesKey("key_user_id")
        private val USER_DATE_OF_BIRTH = stringPreferencesKey("user_dob")
        private val USER_NAME = stringPreferencesKey("key_user_name")
        private val USER_AVATAR = stringPreferencesKey("key_user_avatar")
        private val USER_EMAIL = stringPreferencesKey("key_user_email")
        const val APP_PREFERENCES = "nimpe_data_store"
    }
}