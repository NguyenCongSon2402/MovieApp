package dev.son.movie.ui.login

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.TokenResponse
import dev.son.movie.network.models.user.User
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.models.user.ViewingHistory


data class AuthViewState(
    val user: Async<UserId> = Uninitialized,
    val dataUser: Async<UserId> = Uninitialized,
    val addTolist: Async<String> = Uninitialized,
    val addToFavorite: Async<String> = Uninitialized,
    val getComments: Async<MutableList<UserIdComment>> = Uninitialized,
    val addComments: Async<UserIdComment> = Uninitialized,
    val getFavoriteList: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val getCommentedMovies: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val getHistoryList: Async<ArrayList<ViewingHistory>> = Uninitialized,
    val upDateUser: Async<ApiResponse<String>> = Uninitialized,
    val upLoadImage: Async<String> = Uninitialized,

    val tokenResponse: Async<TokenResponse> = Uninitialized,
    val currentUser: Async<ApiResponse<User>> = Uninitialized
) : MvRxState {
    fun isLoadding() = user is Loading
}
