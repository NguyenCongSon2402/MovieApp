package dev.son.movie.ui.login

import dev.son.movie.core.NimpeViewModelAction
import dev.son.movie.network.models.Register
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.LoginRequest
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.UserId

sealed class AuthViewAction : NimpeViewModelAction {
    data class createUser(val user: UserId) : AuthViewAction()
    data class getUser(val userId: String) : AuthViewAction()
    data class register(val registrationInfo: Register) : AuthViewAction()

    data class SaveDataUser(val userId: UserId) : AuthViewAction()
    data class saveToken(val token: String) : AuthViewAction()
    data class addToList(val IdMovie: MovieId1, val IdUser: String) : AuthViewAction()
    data class addToFavorite(val IdMovie: MovieId1, val IdUser: String) : AuthViewAction()
    data class getComment(val movieId1: String) : AuthViewAction()
    data class addComment(val movieId1: String, val userIdComment: UserIdComment) :
        AuthViewAction()

    object getFavoriteList : AuthViewAction()
    object getCommentedMovies : AuthViewAction()
    data class upDateUser(val user: HashMap<String, Any>) : AuthViewAction()
    data class auth(val loginRequest: LoginRequest) : AuthViewAction()
    object getCurrentUser : AuthViewAction()
}
