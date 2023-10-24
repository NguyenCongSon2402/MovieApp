package dev.son.movie.ui.login

import dev.son.movie.core.NimpeViewModelAction
import dev.son.movie.network.models.user.UserId

sealed class LoginViewAction : NimpeViewModelAction {
    data class createUser(val userId: UserId): LoginViewAction()
}
