package dev.son.movie.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivitySearchBinding
import dev.son.movie.utils.hide
import dev.son.movie.utils.show


class SearchActivity : TrackingBaseActivity<ActivitySearchBinding>() {
//    private val searchResultsViewModel: SearchResultsViewModel by viewModels()
//    private lateinit var topSearchesController: TopMoviesController
//    private lateinit var searchResultsAdapter: MediaItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewModel()
        fetchData()
    }

    override fun getBinding(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        views.toolbar.setNavigationOnClickListener { finish() }
        views.clearSearchIcon.setOnClickListener {
            views.searchTextInput.setText("")
        }
        views.searchTextInput.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                //searchResultsViewModel.fetchSearchResults(query)
            }
            updateUI()
        }

        //topSearchesController = TopMoviesController(this::handleMovieClick)
        //views.topSearchesList.adapter = topSearchesController.adapter
        views.topSearchesList.setOnTouchListener { _, _ ->
            //hideKeyboard()
            views.searchTextInput.clearFocus()
            false
        }

        views.resultsList.setOnTouchListener { _, _ ->
            //hideKeyboard()
            views.searchTextInput.clearFocus()
            false
        }

        //searchResultsAdapter = MediaItemsAdapter(this::handleMediaClick)
        //views.resultsList.adapter = searchResultsAdapter
    }

//    private fun handleMovieClick(movie: Movie) {
//        //hideKeyboard()
//        views.searchTextInput.clearFocus()
//        MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
//            .show(supportFragmentManager, movie.id.toString())
//    }

//    private fun handleMediaClick(media: Media) {
//        hideKeyboard()
//        binding.searchTextInput.clearFocus()
//        if (media is Media.Movie) {
//            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
//                .show(supportFragmentManager, media.id.toString())
//        } else if (media is Media.Tv) {
//            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
//                .show(supportFragmentManager, media.id.toString())
//        }
//    }

    private fun setupViewModel() {
//        searchResultsViewModel.popularMoviesLoading.observe(this) { }
//        searchResultsViewModel.popularMovies.observe(this) {
//            if (it != null) {
//                topSearchesController.setData(it)
//            }
//        }
//        searchResultsViewModel.searchResultsLoading.observe(this) { loading ->
//            val searchResults = searchResultsViewModel.searchResults.value
//            if (loading && searchResults == null) {
//                binding.searchResultsLoader.show()
//            } else {
//                binding.searchResultsLoader.hide()
//            }
//        }
//        searchResultsViewModel.searchResults.observe(this) {
//            searchResultsAdapter.submitList(it)
//        }
    }

    private fun updateUI() {
        val query = views.searchTextInput.text.trim().toString()
        if (query.isEmpty()) {
            views.emptySearchContent.show()
            views.searchResultsContent.hide()
        } else {
//            val searchResultsLoading = searchResultsViewModel.searchResultsLoading.value!!
//            val searchResults = searchResultsViewModel.searchResults.value
            views.emptySearchContent.hide()
            views.searchResultsContent.show()

//            if (searchResultsLoading && searchResults == null) {
//                binding.searchResultsLoader.show()
//            } else {
//                binding.searchResultsLoader.hide()
//            }
        }
    }


    private fun fetchData() {
        //searchResultsViewModel.fetchPopularMovies()
    }
}