package dev.son.moviestreamhub.screens

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.firebase.auth.FirebaseAuth
import dev.son.movie.TrackingApplication


import dev.son.movie.adapters.FavoriteListAdapter
import dev.son.movie.adapters.HistoryListAdapter
import dev.son.movie.adapters.MyListAdapter
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.data.local.UserPreferences

import dev.son.movie.databinding.FragmentMoreBinding
import dev.son.movie.network.models.user.MovieId1
import dev.son.movie.network.models.user.ViewingHistory
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.TvDetailsActivity
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.ui.login.LoginViewAction
import dev.son.movie.ui.login.LoginViewModel
import dev.son.movie.ui.login.LoginViewState
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import kotlinx.coroutines.launch
import javax.inject.Inject


class MoreFragment : TrackingBaseFragment<FragmentMoreBinding>() {
    private val loginViewModel: LoginViewModel by activityViewModel()

    @Inject
    lateinit var userPreferences: UserPreferences


    private lateinit var myListAdapter: MyListAdapter
    private lateinit var favoriteListAdapter: FavoriteListAdapter
    private lateinit var historyListAdapter: HistoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as TrackingApplication).trackingComponent.inject(
            this
        )
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fectchData()
        setupUI()
    }


    private fun fectchData() {
        lifecycleScope.launch {
            userPreferences.userId.collect {
                if (it != null) {
                    val userId = it.userId.toString()
                    loginViewModel.handle(LoginViewAction.getMyList(userId))
                    loginViewModel.handle(LoginViewAction.getFavoriteList(userId))
                    loginViewModel.handle(LoginViewAction.getHistoryList(userId))
                }
            }
        }
    }

    private fun setupUI() {
        myListAdapter = MyListAdapter(this::handleMediaClick)
        views.mylis.adapter = myListAdapter
        favoriteListAdapter = FavoriteListAdapter(this::handleMediaClick)
        views.favoriteList.adapter = favoriteListAdapter
        historyListAdapter = HistoryListAdapter(this::handleItemClick)
        views.historyList.adapter = historyListAdapter
        views.layoutSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }

    private fun handleMediaClick(items: MovieId1, itemView: View) {
        val categoryList = items.category
        val shuffledIndices = categoryList.indices.shuffled()
        val randomIndex = shuffledIndices.first()
        val randomCategory = categoryList[randomIndex]
        val randomSlug = randomCategory.slug


        val intent: Intent
        if (items.type == "single") {
            intent = Intent(activity, MovieDetailsActivity::class.java)
        } else {
            intent = Intent(activity, TvDetailsActivity::class.java)
            intent.putExtra("thumbUrl", items.thumbUrl)
        }
        intent.putExtra("name", items.slug)
        intent.putExtra("category", randomSlug)
        intent.putExtra("id", items.movieId1)


        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    private fun handleItemClick(items: ViewingHistory, itemView: View) {
        val categoryList = items.category
        val shuffledIndices = categoryList.indices.shuffled()
        val randomIndex = shuffledIndices.first()
        val randomCategory = categoryList[randomIndex]
        val randomSlug = randomCategory.slug


        val intent: Intent
        if (items.type == "single") {
            intent = Intent(activity, MovieDetailsActivity::class.java)
        } else {
            intent = Intent(activity, TvDetailsActivity::class.java)
            intent.putExtra("thumbUrl", items.thumbUrl)
        }
        intent.putExtra("name", items.slug)
        intent.putExtra("category", randomSlug)
        intent.putExtra("id", items.movieId)


        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMoreBinding {
        return FragmentMoreBinding.inflate(inflater, container, false)
    }

    override fun onFirstDisplay() {
        //fetchData()
    }

    override fun invalidate(): Unit = withState(loginViewModel) {
        when (it.getMyList) {
            is Success -> {
                val data = it.getMyList.invoke()
                if (data.isNullOrEmpty()) {
                    views.mylis.hide()
                    views.layoutEmptyList.show()
                } else {
                    views.mylis.show()
                    views.layoutEmptyList.hide()
                    myListAdapter.submitList(data)
                }
                loginViewModel.handleRemoveStateGetMyList()
            }

            is Fail -> {
                views.mylis.hide()
                views.layoutEmptyList.show()
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.getMyList)), Toast.LENGTH_SHORT
                ).show()
                loginViewModel.handleRemoveStateGetMyList()
            }

            else -> {}
        }
        when (it.getFavoriteList) {
            is Success -> {
                val data = it.getFavoriteList.invoke()
                if (data.isNullOrEmpty()) {
                    views.favoriteList.hide()
                    views.layoutEmptyLike.show()
                } else {
                    views.favoriteList.show()
                    views.layoutEmptyLike.hide()
                    favoriteListAdapter.submitList(data)
                }
                loginViewModel.handleRemoveStateGetFavorite()
            }

            is Fail -> {
                views.mylis.hide()
                views.layoutEmptyList.show()
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.getFavoriteList)),
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.handleRemoveStateGetFavorite()
            }

            else -> {}
        }
        when (it.getHistoryList) {
            is Success -> {
                val data = it.getHistoryList.invoke()
                if (data.isNullOrEmpty()) {
                    views.historyList.hide()
                    views.layoutEmptyHistory.show()
                } else {
                    views.historyList.show()
                    views.layoutEmptyHistory.hide()
                    historyListAdapter.submitList(data)
                }
                loginViewModel.handleRemoveStateGetHistory()
            }

            is Fail -> {
                views.mylis.hide()
                views.layoutEmptyList.show()
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.getHistoryList)),
                    Toast.LENGTH_SHORT
                ).show()
                loginViewModel.handleRemoveStateGetHistory()
            }

            else -> {}
        }
    }
}