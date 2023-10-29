package dev.son.movie.ui.login

import dev.son.movie.core.NimpeViewModelAction
import dev.son.movie.network.models.user.UserId

sealed class LoginViewAction : NimpeViewModelAction {
    data class createUser(val user: UserId): LoginViewAction()
    data class getUser(val userId: String): LoginViewAction()
    data class SaveDataUser(val userId: UserId): LoginViewAction()
}
