package dev.son.movie.ui.comingsoon

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import dev.son.movie.adapters.CommingSoonMovieAdapter
import dev.son.movie.core.TrackingBaseFragment
import dev.son.movie.databinding.FragmentComingSoonBinding
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel



class ComingSoonFragment : TrackingBaseFragment<FragmentComingSoonBinding>() {
    private val TAG = this.javaClass.simpleName

    private val homeViewModel: HomeViewModel by activityViewModel()
    private lateinit var commingSoonMoviesAdapter: CommingSoonMovieAdapter

    private var genres: List<Genre>? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentComingSoonBinding {
        return FragmentComingSoonBinding.inflate(inflater, container, false)
    }

    private fun setupUI() {
        commingSoonMoviesAdapter = CommingSoonMovieAdapter(this::handleMediaClick)
        views.upcomingMoviesList.adapter = commingSoonMoviesAdapter


        val snapHelper = GravitySnapHelper(Gravity.CENTER)
        snapHelper.scrollMsPerInch = 40.0f
        snapHelper.maxFlingSizeFraction = 0.2f
        snapHelper.attachToRecyclerView(views.upcomingMoviesList)

        //addListScrollListener()
    }

    private fun handleMediaClick(items: Movie, itemView: View) {
        val intent = Intent(requireActivity(), MovieDetailsActivity::class.java)

        intent.putExtra("movie", items)
        intent.putExtra("comingson", true)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

//    private fun addListScrollListener() {
//        views.upcomingMoviesList.addOnScrollListener(object : AbsListView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                synchronized(this) {
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        val newPosition =
//                            (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//                        if (newPosition == -1) {
//                            return
//                        }
//                        val oldPosition = commingSoonMoviesAdapter.firstVisibleItemPosition
//                        Log.v(
//                            "__SCROLL_STATE_IDLE",
//                            "oldPosition: $oldPosition, newPosition: $newPosition"
//                        )
//
//                        if (newPosition != oldPosition) {
//                            val oldPositionItem =
//                                (recyclerView.findViewHolderForAdapterPosition(oldPosition) as UpcomingMovieViewHolder?)
//                            val newPositionItem =
//                                (recyclerView.findViewHolderForAdapterPosition(newPosition) as UpcomingMovieViewHolder?)
//
//                            oldPositionItem?.binding?.overlay?.show()
//                            newPositionItem?.binding?.overlay?.hide()
//                            upcomingMoviesAdapter.firstVisibleItemPosition = newPosition
//                        }
//                    }
//                }
//            }
//        })
//
//        lifecycleScope.launch {
//
//        }
//    }

    private fun setupViewModel() {
//        homeViewModel.handle(HomeViewAction.getPhimSapChieu)
    }

    override fun invalidate(): Unit = withState(homeViewModel) {
        when (it.genre) {
            is Success -> {
                genres = it.genre.invoke().data
                val (genresWithAt, genresWithoutAt) = genres?.partition { genre ->
                    genre.code?.contains("@") == true
                } ?: Pair(emptyList(), emptyList())

// In danh sách có chứa "@"
                Log.e("Genres with @", genresWithAt.toString())

// In danh sách không chứa "@"
                Log.e("Genres without @", genresWithoutAt.toString())

                genresWithAt.forEach { genresWithAt ->
                    when (genresWithAt.code) {
                        "@comingsoon" -> {
                            homeViewModel.handle(HomeViewAction.getMovieComingSoon(genresWithAt.code))
                        }
                    }
                }
            }

            is Fail -> {
//                Toast.makeText(
//                    activity,
//                    "genre " +
//                            checkStatusApiRes(it.genre),
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            else -> {}
        }
        when (it.movieComingSoon) {
            is Success -> {
                commingSoonMoviesAdapter.submitList(it.movieComingSoon.invoke().data)
                homeViewModel.handleRemoveStateGetComingSoon()
            }

            is Fail -> {
                homeViewModel.handleRemoveStateGetComingSoon()
                //Log.d(TAG, checkStatusApiRes(it.movieComingSoon).toString())
            }

            else -> {}
        }
    }
}