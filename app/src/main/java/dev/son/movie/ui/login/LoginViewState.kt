package dev.son.movie.ui.login

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.models.user.ViewingHistory


data class LoginViewState(
    val user: Async<UserId> = Uninitialized,
    val dataUser: Async<UserId> = Uninitialized,
    val addTolist: Async<String> = Uninitialized,
    val addToFavorite: Async<String> = Uninitialized,
    val getComments: Async<MutableList<UserIdComment>> = Uninitialized,
    val addComments: Async<UserIdComment> = Uninitialized,
    val getMyList: Async<ArrayList<MovieId1>> = Uninitialized,
    val getFavoriteList: Async<ArrayList<MovieId1>> = Uninitialized,
    val getHistoryList: Async<ArrayList<ViewingHistory>> = Uninitialized,
) : MvRxState {
    fun isLoadding() = user is Loading
}
