package dev.son.movie.ui.login

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success

import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import dev.son.movie.core.TrackingViewModel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.repository.FirebaseRepository
import kotlinx.coroutines.async


class LoginViewModel @AssistedInject constructor(
    @Assisted state: LoginViewState, private val firebaseRepository: FirebaseRepository
) : TrackingViewModel<LoginViewState, LoginViewAction, LoginViewEvent>(state) {
    //    private var job: Job? = null
    override fun handle(action: LoginViewAction) {
        when (action) {

            is LoginViewAction.createUser -> handleCreateUser(action.user)
            is LoginViewAction.getUser -> handleGetUser(action.userId)
            is LoginViewAction.SaveDataUser -> handleSaveDataUser(action.userId)
            is LoginViewAction.addToList -> handleAddToList(action.IdMovie, action.idUser)
        }
    }

    private fun handleSaveDataUser(data: UserId) {
        this.viewModelScope.async {
            firebaseRepository.saveDataUser(data)
        }
    }

    @SuppressLint("CheckResult")
    private fun handleAddToList(idMovie: String, idUser: String) {
        setState { copy(addTolist = Loading()) }
        firebaseRepository.addToList(idMovie, idUser)
            .subscribe({ add ->
                // Đăng ký thành công, bạn có thể cập nhật trạng thái ở đây
                setState { copy(addTolist = Success(add)) }
            }, { error ->
                // Xử lý trường hợp lỗi ở đây
                setState { copy(addTolist = Fail(error)) }
            })
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

    @SuppressLint("CheckResult")
    private fun handleGetUser(userId: String) {
        setState { copy(dataUser = Loading()) }
        firebaseRepository.getUser(userId)
            .subscribe({ get ->
                // lấy dữ liệu thành công
                setState { copy(dataUser = Success(get)) }
            }, { error ->
                // Xử lý trường hợp lỗi ở đây
                setState { copy(dataUser = Fail(error)) }
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