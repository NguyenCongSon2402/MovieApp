package dev.son.movie.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.User
import dev.son.movie.network.models.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPreferences.APP_PREFERENCES)

@Suppress("UNCHECKED_CAST")
class UserPreferences @Inject constructor(context: Context) {

    private val mContext = context


    val coins: Flow<Int?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_COINS]
        }

    val user: Flow<User?>
        get() = mContext.dataStore.data.map { preferences ->
            User(
                id = preferences[USER_ID],
                name = preferences[USER_NAME],
                birthday = preferences[USER_DATE_OF_BIRTH],
                email = preferences[USER_EMAIL],
                isAdmin = preferences[IS_ADMIN],
                photoURL = preferences[USER_AVATAR],
                coins = preferences[USER_COINS]
            )
        }
    val token: String?
        get() = runBlocking {
            mContext.dataStore.data.first()[TOKEN]
        }


    suspend fun saveToken(token: String): Boolean {
        return try {
            mContext.dataStore.edit { preferences ->
                preferences[TOKEN] = token
            }
            true // Trả về true nếu lưu thành công
        } catch (e: Exception) {
            // Xử lý lỗi khi lưu không thành công (ví dụ: log lỗi)
            e.printStackTrace()
            false // Trả về false nếu có lỗi
        }
    }


    val userCoins: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }


    suspend fun saveUserData(user: User): Boolean {
        return try {
            mContext.dataStore.edit { preferences ->
                preferences[USER_ID] = user.id ?: -1
                preferences[USER_NAME] = user.name.toString()
                preferences[USER_EMAIL] = user.email.toString()
                preferences[USER_AVATAR] = user.photoURL.toString()
                preferences[USER_DATE_OF_BIRTH] = user.birthday.toString()
                preferences[USER_COINS] = user.coins ?: 0
                preferences[IS_ADMIN] = user.isAdmin ?: false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }


    suspend fun upDateCoins(coins: Int, type: Boolean) {
        mContext.dataStore.edit { preferences ->
            // Nếu userId trùng khớp, lấy danh sách watchedMovies hiện tại
            var currentCoins = preferences[USER_COINS] ?: 0
            if (type) {
                preferences[USER_COINS] = currentCoins + coins
            } else {
                preferences[USER_COINS] = (currentCoins - coins).coerceAtLeast(0)

            }

        }
    }



    suspend fun clear(): Boolean = try {
        mContext.dataStore.edit { preferences ->
            preferences.clear()
        }
        true // Xóa thành công
    } catch (e: Exception) {
        false // Xóa thất bại
    }


    companion object {
        // Use setPreferencesKey to save the lists as Set
        // Use stringSetPreferencesKey to save the lists as String Sets

        private val USER_ID = intPreferencesKey("key_user_id")
        private val TOKEN = stringPreferencesKey("key_user_token")
        private val USER_DATE_OF_BIRTH = stringPreferencesKey("user_dob")
        private val USER_NAME = stringPreferencesKey("key_user_name")
        private val USER_AVATAR = stringPreferencesKey("key_user_avatar")
        private val USER_EMAIL = stringPreferencesKey("key_user_email")
        private val IS_ADMIN = booleanPreferencesKey("key_is_admin")
        private val USER_COINS = intPreferencesKey("key_user_coins")
        const val APP_PREFERENCES = "nimpe_data_store"
    }
}