package dev.son.movie.ui.login

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading

import com.airbnb.mvrx.MvRxViewModelFactory

import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import dev.son.movie.core.TrackingViewModel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.son.movie.network.models.Register
import dev.son.movie.network.models.user.LoginRequest
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.repository.UserRepository
import kotlinx.coroutines.async


class AuthViewModel @AssistedInject constructor(
    @Assisted state: AuthViewState, private val userRepository: UserRepository
) : TrackingViewModel<AuthViewState, AuthViewAction, AuthViewEvent>(state) {
    //    private var job: Job? = null
    override fun handle(action: AuthViewAction) {
        when (action) {
            is AuthViewAction.auth -> handleLogin(action.loginRequest)
            is AuthViewAction.register -> handleRegister(action.registrationInfo)
            is AuthViewAction.saveToken -> handleSaveToken(action.token)
            is AuthViewAction.getCurrentUser -> handleGetCurrentUser()
            is AuthViewAction.upDateUser -> handleUpDateUser(action.user)
            is AuthViewAction.getFavoriteList -> handleFavoriteList()
            is AuthViewAction.getCommentedMovies -> handleGetCommentedMovies()
            else -> {}
        }
    }

    private fun handleGetCommentedMovies() {
        setState { copy(getCommentedMovies = Loading()) }
        userRepository.getCommentedMovies().execute {
            copy(getCommentedMovies = it)
        }
    }

    private fun handleFavoriteList() {
        setState { copy(getFavoriteList = Loading()) }
        userRepository.getFavoriteList().execute {
            copy(getFavoriteList = it)
        }
    }

    private fun handleUpDateUser(user: HashMap<String, Any>) {
        setState { copy(upDateUser = Loading()) }
        userRepository.upDateUser(user).execute {
            copy(upDateUser = it)
        }
    }

    private fun handleGetCurrentUser() {
        setState { copy(currentUser = Loading()) }
        userRepository.getCurrentUser().execute {
            copy(currentUser = it)
        }
    }

    private fun handleSaveToken(token: String) {
        this.viewModelScope.async {
            userRepository.saveToken(token)
        }
    }

    private fun handleSaveDataUser(data: UserId) {
        this.viewModelScope.async {
            //firebaseRepository.saveDataUser(data)
        }
    }

    private fun handleLogin(loginRequest: LoginRequest) {
        setState { copy(tokenResponse = Loading()) }
        userRepository.login(loginRequest).execute {
            copy(tokenResponse = it)
        }
    }
    private fun handleRegister(registrationInfo: Register) {
        setState { copy(register = Loading()) }
        userRepository.register(registrationInfo).execute {
            copy(register = it)
        }
    }


    fun handleRemoveStateGetFavorite() =
        setState { copy(getFavoriteList = Uninitialized) }
    fun handleRemoveStateGetCommentedMovies() =
        setState { copy(getCommentedMovies = Uninitialized) }


    fun handleRemoveUpdateUser() =
        setState { copy(upDateUser = Uninitialized) }

    fun handleRemoveUpLoadr() =
        setState { copy(upLoadImage = Uninitialized) }
    fun handleRemoveRegister() =
        setState { copy(register = Uninitialized) }


    @AssistedFactory
    interface Factory {
        fun create(initialState: AuthViewState): AuthViewModel
    }

    companion object : MvRxViewModelFactory<AuthViewModel, AuthViewState> {
        override fun create(
            viewModelContext: ViewModelContext, state: AuthViewState
        ): AuthViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as Factory
                is ActivityViewModelContext -> viewModelContext.activity as Factory
            }
            return fatory.create(state)
        }
    }
}