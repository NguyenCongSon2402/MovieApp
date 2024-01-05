package dev.son.movie.ui.more

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.TrackingApplication


import dev.son.movie.adapters.FavoriteListAdapter
import dev.son.movie.adapters.MyListAdapter
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.data.local.UserPreferences

import dev.son.movie.databinding.FragmentMoreBinding
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.AccountActivity
import dev.son.movie.ui.AdminActivity
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.ui.login.AuthViewAction
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.rateApp.RateAppActivity
import dev.son.movie.ui.search.SearchActivity
import dev.son.movie.utils.DialogUtil
import dev.son.movie.utils.checkStatusApiRes
import dev.son.movie.utils.hide
import dev.son.movie.utils.setSingleClickListener
import dev.son.movie.utils.show
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class MoreFragment : TrackingBaseFragment<FragmentMoreBinding>() {
    private val authViewModel: AuthViewModel by activityViewModel()

    @Inject
    lateinit var userPreferences: UserPreferences


    private lateinit var myListAdapter: MyListAdapter
    private lateinit var favoriteListAdapter: FavoriteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as TrackingApplication).trackingComponent.inject(
            this
        )
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //fectchData()
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        fectchData()
    }

    private fun fectchData() {
        lifecycleScope.launch {
            userPreferences.user.collect {
                if (it != null) {
                    Glide.with(views.imgUser).load(it.photoURL).centerCrop()
                        .error(getDrawable(requireContext(), R.drawable.ic_person))
                        .into(views.imgUser)
                    views.txtName.text = it.name
                    authViewModel.handle(AuthViewAction.getCommentedMovies)
                    authViewModel.handle(AuthViewAction.getFavoriteList)
                    Log.e("ADMIN", "${it.isAdmin}")
                    if (it.isAdmin == true) {
                        views.layoutAdmin.show()
                        views.layoutAdmin.setSingleClickListener {
                            startActivity(Intent(requireActivity(), AdminActivity::class.java))
                        }
                    } else {
                        views.layoutAdmin.hide()
                    }
                }
            }
        }
    }

    private fun setupUI() {
        myListAdapter = MyListAdapter(this::handleMediaClick)
        views.mylis.adapter = myListAdapter
        favoriteListAdapter = FavoriteListAdapter(this::handleMediaClick)
        views.favoriteList.adapter = favoriteListAdapter
        views.layoutSignOut.setSingleClickListener {
            DialogUtil.showAlertDialogLogOut(requireActivity()) {
                lifecycleScope.launch {
                    val isCleared = async { userPreferences.clear() }.await()
                    if (isCleared) {
                        startActivity(Intent(activity, LoginActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Hãy thử lại", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        views.layoutManager.setSingleClickListener {
            startActivity(Intent(activity, AccountActivity::class.java))
        }
        views.searchButton.setSingleClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        views.searchButton2.setSingleClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        views.layoutSupport.setOnClickListener {
            startActivity(Intent(requireContext(), RateAppActivity::class.java))
        }

    }

    private fun handleMediaClick(items: Movie, itemView: View) {
        val intent = Intent(requireActivity(), MovieDetailsActivity::class.java)

        intent.putExtra("movie", items)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

//    private fun handleItemClick(items: ViewingHistory, itemView: View) {
//        val intent = Intent(requireActivity(), MovieDetailsActivity::class.java)
//
//        intent.putExtra("movie", items)
//
//        val options = ActivityOptions.makeSceneTransitionAnimation(
//            requireActivity(),
//            itemView,
//            "my_shared_element"
//        )
//        startActivity(intent, options.toBundle())
//    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMoreBinding {
        return FragmentMoreBinding.inflate(inflater, container, false)
    }

    override fun onFirstDisplay() {
        //fetchData()
    }

    override fun invalidate(): Unit = withState(authViewModel) {
        when (it.getCommentedMovies) {
            is Success -> {
                val data = it.getCommentedMovies.invoke()
                if (data.data.isNullOrEmpty()) {
                    views.mylis.hide()
                    views.layoutEmptyList.show()
                } else {
                    views.mylis.show()
                    views.layoutEmptyList.hide()
                    myListAdapter.submitList(data.data)
                }
                authViewModel.handleRemoveStateGetCommentedMovies()
            }

            is Fail -> {
                views.mylis.hide()
                views.layoutEmptyList.show()
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.getCommentedMovies)),
                    Toast.LENGTH_SHORT
                ).show()
                authViewModel.handleRemoveStateGetCommentedMovies()
            }

            else -> {}
        }
        when (it.getFavoriteList) {
            is Success -> {
                val data = it.getFavoriteList.invoke()
                if (data.data.isNullOrEmpty()) {
                    views.favoriteList.hide()
                    views.layoutEmptyLike.show()
                } else {
                    views.favoriteList.show()
                    views.layoutEmptyLike.hide()
                    favoriteListAdapter.submitList(data.data)
                }
                authViewModel.handleRemoveStateGetFavorite()
            }

            is Fail -> {
                views.mylis.hide()
                views.layoutEmptyList.show()
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.getFavoriteList)),
                    Toast.LENGTH_SHORT
                ).show()
                authViewModel.handleRemoveStateGetFavorite()
            }

            else -> {}
        }
//        when (it.getHistoryList) {
//            is Success -> {
//                val data = it.getHistoryList.invoke()
//                if (data.isNullOrEmpty()) {
//                    views.historyList.hide()
//                    views.layoutEmptyHistory.show()
//                } else {
//                    views.historyList.show()
//                    views.layoutEmptyHistory.hide()
//                    historyListAdapter.submitList(data)
//                }
//                loginViewModel.handleRemoveStateGetHistory()
//            }
//
//            is Fail -> {
//                views.mylis.hide()
//                views.layoutEmptyList.show()
//                Toast.makeText(
//                    requireContext(),
//                    getString(checkStatusApiRes(it.getHistoryList)),
//                    Toast.LENGTH_SHORT
//                ).show()
//                loginViewModel.handleRemoveStateGetHistory()
//            }
//
//            else -> {}
//        }
    }
}