package dev.son.movie.ui.search

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.SearchMovieAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivitySearchBinding
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.utils.hide
import dev.son.movie.utils.hideKeyboard
import dev.son.movie.utils.show
import javax.inject.Inject


class SearchActivity : TrackingBaseActivity<ActivitySearchBinding>(), SearchViewModel.Factory {
    private val searchViewModel: SearchViewModel by viewModel()

    @Inject
    lateinit var searchViewModelFactory: SearchViewModel.Factory

    //    private lateinit var topSearchesController: TopMoviesController
    private lateinit var searchResultsAdapter: SearchMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewModel()
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
                searchViewModel.handle(SearchViewAction.searchMovie(query))
            }
            updateUI()
        }

        //topSearchesController = TopMoviesController(this::handleMovieClick)
        //views.topSearchesList.adapter = topSearchesController.adapter
        views.topSearchesList.setOnTouchListener { _, _ ->
            hideKeyboard()
            views.searchTextInput.clearFocus()
            false
        }

        views.resultsList.setOnTouchListener { _, _ ->
            hideKeyboard()
            views.searchTextInput.clearFocus()
            false
        }

        searchResultsAdapter = SearchMovieAdapter(this::handleMediaClick)
        views.resultsList.adapter = searchResultsAdapter
    }


    private fun handleMediaClick(items: Movie,itemView:View) {
        hideKeyboard()
        views.searchTextInput.clearFocus()
        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movie", items)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            this@SearchActivity,
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    private fun setupViewModel() {
        searchViewModel.subscribe(this) {
            when (it.search) {
                is Success -> {
                    searchResultsAdapter.submitList(it.search.invoke().data)
                    views.searchResultsLoader.hide()
                    searchViewModel.handleRemoveState()
                }

                is Loading -> {
                    views.emptySearchContent.hide()
                    views.searchResultsContent.show()
                    views.searchResultsLoader.show()
                }

                is Fail -> {
                    views.searchResultsLoader.hide()
                    searchViewModel.handleRemoveState()
                }

                else -> {}
            }
        }
    }

    private fun updateUI() {
        val query = views.searchTextInput.text.trim().toString()
        if (query.isEmpty()) {
            views.emptySearchContent.show()
            views.searchResultsContent.hide()
        }
    }

    override fun create(initialState: SearchViewState): SearchViewModel {
        return searchViewModelFactory.create(initialState)
    }
}