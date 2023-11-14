package dev.son.movie.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

    val userId: Flow<UserId?>
        get() = mContext.dataStore.data.map { preferences ->
            UserId(
                userId = preferences[USER_ID],
                name = preferences[USER_NAME],
                dateOfBirth = preferences[USER_DATE_OF_BIRTH],
                email = preferences[USER_EMAIL],
                avatar = preferences[USER_AVATAR],
                coins = preferences[USER_COINS]
            )
        }

    suspend fun saveUserFullname(fullname: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ID] = fullname
        }
    }


    val userCoins: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }

    suspend fun saveMyList(myList: ArrayList<MovieId1>) {
        val idSet = myList.map { it.movieId1.toString() }.toSet()
        mContext.dataStore.edit { preferences ->
            preferences[WATCHED_MOVIES] = idSet
        }
    }

    suspend fun saveLikeList(myList: ArrayList<MovieId1>) {
        val idSet = myList.map { it.movieId1.toString() }.toSet()
        mContext.dataStore.edit { preferences ->
            preferences[FAVORITE_MOVIES] = idSet
        }
    }

    suspend fun saveUserData(user: UserId) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId.toString()
            preferences[USER_NAME] = user.name.toString()
            preferences[USER_EMAIL] = user.email.toString()
            preferences[USER_AVATAR] = user.avatar.toString()
            preferences[USER_DATE_OF_BIRTH] = user.dateOfBirth.toString()
            preferences[USER_COINS] = user.coins ?: 0
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

    suspend fun toggleFavoriteMovie(movieId: String) {
        mContext.dataStore.edit { preferences ->
            // Nếu userId trùng khớp, lấy danh sách watchedMovies hiện tại
            val currentFavoriteMovies = preferences[FAVORITE_MOVIES] ?: emptySet()

            // Kiểm tra xem movieId đã tồn tại trong danh sách chưa
            if (movieId in currentFavoriteMovies) {
                // Nếu đã tồn tại, xoá nó đi
                preferences[FAVORITE_MOVIES] = currentFavoriteMovies - movieId
            } else {
                // Nếu chưa tồn tại, thêm nó vào
                preferences[FAVORITE_MOVIES] = currentFavoriteMovies + movieId
            }

        }
    }
    suspend fun upDateCoins(coins:Int,type:Boolean) {
        mContext.dataStore.edit { preferences ->
            // Nếu userId trùng khớp, lấy danh sách watchedMovies hiện tại
            var currentCoins = preferences[USER_COINS] ?: 0
            if (type){
                preferences[USER_COINS]=currentCoins+coins
            }else{
                preferences[USER_COINS] = (currentCoins - coins).coerceAtLeast(0)

            }

        }
    }

    suspend fun checkWatchedMovie(movieId: String): Flow<Boolean> = flow {
        val movieExists = mContext.dataStore.data.map { preferences ->
            val currentWatchedMovies = preferences[WATCHED_MOVIES] ?: emptySet()
            movieId in currentWatchedMovies
        }.first() // Lấy giá trị đầu tiên từ Flow

        emit(movieExists) // Trả về kết quả kiểm tra
    }

    suspend fun checkFavoriteMovie(movieId: String): Flow<Boolean> = flow {
        val movieExists = mContext.dataStore.data.map { preferences ->
            val currentWatchedMovies = preferences[FAVORITE_MOVIES] ?: emptySet()
            movieId in currentWatchedMovies
        }.first() // Lấy giá trị đầu tiên từ Flow

        emit(movieExists) // Trả về kết quả kiểm tra
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
        private val USER_COINS = intPreferencesKey("key_user_coins")
        const val APP_PREFERENCES = "nimpe_data_store"
    }
}