package com.oceantech.tracking.ui.comingsoon

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentComingSoonBinding


class ComingSoonFragment : TrackingBaseFragment<FragmentComingSoonBinding>() {

//    private lateinit var viewModel: MediaViewModel
//    private lateinit var upcomingMoviesAdapter: UpcomingMoviesAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        //setupViewModel()
    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentComingSoonBinding {
        return FragmentComingSoonBinding.inflate(inflater, container, false)
    }

    private fun setupUI() {
        //upcomingMoviesAdapter = UpcomingMoviesAdapter()
        //views.upcomingMoviesList.adapter = upcomingMoviesAdapter


        val snapHelper = GravitySnapHelper(Gravity.CENTER)
        snapHelper.scrollMsPerInch = 40.0f
        snapHelper.maxFlingSizeFraction = 0.2f
        snapHelper.attachToRecyclerView(views.upcomingMoviesList)

        //addListScrollListener()
    }

//    private fun addListScrollListener() {
//        views.upcomingMoviesList.addOnScrollListener(object : OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                synchronized(this) {
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        val newPosition =
//                            (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//                        if (newPosition == -1) {
//                            return
//                        }
//                        val oldPosition = upcomingMoviesAdapter.firstVisibleItemPosition
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

//    private fun setupViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            Injection.provideMediaViewModelFactory()
//        ).get(MediaViewModel::class.java)
//    }

//    private fun fetchData() {
//        lifecycleScope.launchWhenCreated {
//            try {
//                viewModel.getUpcomingMovies().collectLatest {
//                    upcomingMoviesAdapter.submitData(it)
//                }
//            } catch (e: Exception) {
//            }
//        }
//    }
}