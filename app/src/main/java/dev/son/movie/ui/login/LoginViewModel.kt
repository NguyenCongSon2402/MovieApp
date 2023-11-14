package dev.son.movie.ui.login

import android.annotation.SuppressLint
import android.net.Uri
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
import dev.son.movie.network.models.postcomment.UserIdComment
import dev.son.movie.network.models.user.MovieId1
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
            is LoginViewAction.addToList -> handleAddToList(action.IdMovie, action.IdUser)
            is LoginViewAction.addToFavorite -> handleAddToFavorite(action.IdMovie, action.IdUser)
            is LoginViewAction.getComment -> handleGetComment(action.movieId1)
            is LoginViewAction.addComment -> handleAddComment(action.movieId1, action.userIdComment)
            is LoginViewAction.getMyList -> handleGetMyList(action.idUser)
            is LoginViewAction.getFavoriteList -> handlegetFavoriteList(action.idUser)
            is LoginViewAction.getHistoryList -> handlegetHistoryList(action.idUser)
            is LoginViewAction. upDateUser -> handleUpDateUser(action.id, action.idUser)
            is LoginViewAction.upLoadImage -> handleUpLoadImage(action.img,action.id)
        }
    }

    private fun handleSaveDataUser(data: UserId) {
        this.viewModelScope.async {
            firebaseRepository.saveDataUser(data)
        }
    }

    @SuppressLint("CheckResult")
    private fun handleAddToList(idMovie: MovieId1, idUser: String) {
        setState { copy(addTolist = Loading()) }
        firebaseRepository.addToList(idMovie, idUser)
            .subscribe({ add ->
                setState { copy(addTolist = Success(add)) }
            }, { error ->
                setState { copy(addTolist = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleAddToFavorite(idMovie: MovieId1, idUser: String) {
        setState { copy(addToFavorite = Loading()) }
        firebaseRepository.addToFavorite(idMovie, idUser)
            .subscribe({ add ->
                setState { copy(addToFavorite = Success(add)) }
            }, { error ->
                setState { copy(addToFavorite = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleGetComment(idMovie: String) {
        setState { copy(getComments = Loading()) }
        firebaseRepository.getComment(idMovie)
            .subscribe({ add ->
                setState { copy(getComments = Success(add)) }
            }, { error ->
                setState { copy(getComments = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleAddComment(idMovie: String, userIdComment: UserIdComment) {
        setState { copy(addComments = Loading()) }
        firebaseRepository.addComment(idMovie, userIdComment)
            .subscribe({ add ->
                setState { copy(addComments = Success(add)) }
            }, { error ->
                setState { copy(addComments = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleGetMyList(idUser: String) {
        setState { copy(getMyList = Loading()) }
        firebaseRepository.getMyList(idUser)
            .subscribe({ add ->
                setState { copy(getMyList = Success(add)) }
            }, { error ->
                setState { copy(getMyList = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handlegetFavoriteList(idUser: String) {
        setState { copy(getFavoriteList = Loading()) }
        firebaseRepository.getFavoriteList(idUser)
            .subscribe({ add ->
                setState { copy(getFavoriteList = Success(add)) }
            }, { error ->
                setState { copy(getFavoriteList = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handlegetHistoryList(idUser: String) {
        setState { copy(getHistoryList = Loading()) }
        firebaseRepository.getHistory(idUser)
            .subscribe({ add ->
                setState { copy(getHistoryList = Success(add)) }
            }, { error ->
                setState { copy(getHistoryList = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleUpDateUser(id: String?, idUser1: HashMap<String, Any>) {
        setState { copy(upDateUser = Loading()) }
        firebaseRepository.upDateUser(id, idUser1)
            .subscribe({ add ->
                setState { copy(upDateUser = Success(add)) }
            }, { error ->
                setState { copy(upDateUser = Fail(error)) }
            })
    }

    @SuppressLint("CheckResult")
    private fun handleUpLoadImage(img: Uri, id: String) {
        setState { copy(upLoadImage = Loading()) }
        firebaseRepository.upLoadImage(img,id)
            .subscribe({ add ->
                setState { copy(upLoadImage = Success(add)) }
            }, { error ->
                setState { copy(upLoadImage = Fail(error)) }
            })
    }


    fun handleRemoveStateHome() =
        setState { copy(user = Uninitialized) }

    fun handleRemoveStateAddToList() =
        setState { copy(addTolist = Uninitialized) }

    fun handleRemoveStateAddToFavorite() =
        setState { copy(addToFavorite = Uninitialized) }

    fun handleRemoveStateGetComment() =
        setState { copy(getComments = Uninitialized) }

    fun handleRemoveStateAddComment() =
        setState { copy(addComments = Uninitialized) }

    fun handleRemoveStateGetMyList() =
        setState { copy(getMyList = Uninitialized) }

    fun handleRemoveStateGetFavorite() =
        setState { copy(getFavoriteList = Uninitialized) }

    fun handleRemoveStateGetHistory() =
        setState { copy(getHistoryList = Uninitialized) }


    fun handleRemoveUpdateUser() =
        setState { copy(upDateUser = Uninitialized) }
    fun handleRemoveUpLoadr() =
        setState { copy(upLoadImage = Uninitialized)}

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