package dev.son.movie.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.son.movie.network.models.user.MovieTrackinglist
import dev.son.movie.network.models.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(UserPreferences.APP_PREFERENCES)

class UserPreferences @Inject constructor(context: Context) {

    private val mContext = context


    val username: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USERNAME]
        }

    val userFullname: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_FULLNAME]
        }

    suspend fun saveUserFullname(fullname: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_FULLNAME] = fullname
        }
    }


    val userEmail: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_EMAIL]
        }
    val userRole: Flow<String?>
        get() = mContext.dataStore.data.map { preferences ->
            preferences[USER_ROLE]
        }

    suspend fun saveUserRole(authority: String) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ROLE] = authority
        }
    }


    fun getUserDataFlow(): Flow<UserId> {
        return mContext.dataStore.data
            .map { preferences ->
                UserId(
                    userId = preferences[USER_ID] ?: "",
                    email = preferences[USER_EMAIL] ?: "email@gmail.com",
                    movieTrackinglist = MovieTrackinglist(
                        watchedMovies = (preferences[WATCHED_MOVIES]
                            ?: emptySet()) as ArrayList<String>,
                        favoriteMovies = (preferences[FAVORITE_MOVIES]
                            ?: emptySet()) as ArrayList<String>
                    )
                )
            }
    }

    suspend fun saveUserData(user: UserId) {
        mContext.dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId ?: ""
            preferences[USER_ID] = user.email ?: ""
            preferences[USER_EMAIL] = user.email ?: "email@gmail.com"
            // Lưu danh sách watched_movies và favorite_movies dưới dạng Set
            preferences[WATCHED_MOVIES] = user.movieTrackinglist?.watchedMovies?.toSet() ?: emptySet()
            preferences[FAVORITE_MOVIES] = user.movieTrackinglist?.favoriteMovies?.toSet()?: emptySet()
        }
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
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USERNAME = stringPreferencesKey("key_user_name")
        private val USER_FULLNAME = stringPreferencesKey("key_user_fullname")
        private val USER_EMAIL = stringPreferencesKey("key_user_email")
        const val APP_PREFERENCES = "nimpe_data_store"
    }

}