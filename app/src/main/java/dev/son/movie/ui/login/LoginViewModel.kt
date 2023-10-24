package dev.son.movie.ui.login

import android.annotation.SuppressLint
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success

import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import dev.son.movie.core.TrackingViewModel

import dev.son.movie.network.repository.HomeRepository

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.repository.FirebaseRepository


class LoginViewModel @AssistedInject constructor(
    @Assisted state: LoginViewState, private val firebaseRepository: FirebaseRepository
) : TrackingViewModel<LoginViewState, LoginViewAction, LoginViewEvent>(state) {
    //    private var job: Job? = null
    override fun handle(action: LoginViewAction) {
        when (action) {

            is LoginViewAction.createUser -> handleCreateUser(action.userId)
            else -> {}
        }
    }


    fun handleRemoveStateHome() =
        setState { copy(user = Uninitialized) }

    @SuppressLint("CheckResult")
    private fun handleCreateUser(userId: UserId) {
        setState { copy(user = Loading()) }
        firebaseRepository.register(userId)
            .subscribe({ registeredUserId ->
                // Đăng ký thành công, bạn có thể cập nhật trạng thái ở đây
                setState { copy(user = Success(registeredUserId)) }
            }, { error ->
                // Xử lý trường hợp lỗi ở đây
                setState { copy(user = Fail(error)) }
            })
    }


    @AssistedFactory
    interface Factory {
        fun create(initialState: LoginViewState): LoginViewModel
    }

    companion object : MvRxViewModelFactory<LoginViewModel, LoginViewState> {
        override fun create(
            viewModelContext: ViewModelContext, state: LoginViewState
        ): LoginViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as Factory
                is ActivityViewModelContext -> viewModelContext.activity as Factory
            }
            return fatory.create(state)
        }
    }
}