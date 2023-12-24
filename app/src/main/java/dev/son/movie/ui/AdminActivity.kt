package dev.son.movie.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.AllMoviesAdapter
import dev.son.movie.adapters.SearchMovieAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityAdminBinding
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import dev.son.movie.ui.search.SearchViewAction
import dev.son.movie.ui.search.SearchViewModel
import dev.son.movie.ui.search.SearchViewState
import dev.son.movie.utils.DialogUtil
import dev.son.movie.utils.hide
import dev.son.movie.utils.hideKeyboard
import dev.son.movie.utils.setSingleClickListener
import dev.son.movie.utils.show
import javax.inject.Inject

class AdminActivity : TrackingBaseActivity<ActivityAdminBinding>(), HomeViewModel.Factory,
    SearchViewModel.Factory {


    private val searchViewModel: SearchViewModel by viewModel()

    @Inject
    lateinit var searchViewModelFactory: SearchViewModel.Factory

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    private val homeViewModel: HomeViewModel by viewModel()

    private var idMovieEdit = -1

    private lateinit var allMoviesAdapter: AllMoviesAdapter
    private lateinit var searchResultsAdapter: SearchMovieAdapter

    private var allMovie: MutableList<Movie> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        fetchData()
        setupUI()

        resultData()

    }

    private fun fetchData() {
        searchViewModel.handle(SearchViewAction.getMoviesLatest)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        views.toolbar.setSingleClickListener { finish() }
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

        views.addMovie.setSingleClickListener {
            startActivity(Intent(this, AddMovieActivity::class.java))
        }

        views.listAllMovie.setOnTouchListener { _, _ ->
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
        allMoviesAdapter = AllMoviesAdapter(this::handleMediaClick, this::handleEdtClick)
        views.listAllMovie.adapter = allMoviesAdapter.adapter

    }

    private fun handleEdtClick(items: Movie, event: Boolean) {
        // event= true -> edit else delete
        if (event == true) {
            val intent = Intent(this, AddMovieActivity::class.java)
            intent.putExtra("movie", items)
            startActivity(intent)
        } else {
            DialogUtil.showDialogDelete(this) {
                searchViewModel.handle(SearchViewAction.deleteMovie(items.id.toString()))
                idMovieEdit = items.id
            }
        }
    }

    private fun handleMediaClick(items: Movie, itemView: View) {
        hideKeyboard()
        views.searchTextInput.clearFocus()
        val intent = Intent(this, MovieDetailsActivity::class.java)

        intent.putExtra("movie", items)

        val options = ActivityOptions.makeSceneTransitionAnimation(
            this@AdminActivity,
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    private fun resultData() {
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
            when (it.getMoviesLatest) {
                is Success -> {
                    allMovie = it.getMoviesLatest.invoke().data.toMutableList()
                    allMoviesAdapter.setData(it.getMoviesLatest.invoke().data)
                    searchViewModel.handleRemoveStateMoviesLatest()
                    views.searchResultsLoader.hide()
                }

                is Loading -> {
                    views.searchResultsLoader.show()
                }

                is Fail -> {
                    views.searchResultsLoader.hide()
                    searchViewModel.handleRemoveStateMoviesLatest()
                }

                else -> {}
            }
            when (it.deleteMovie) {
                is Success -> {
                    if (it.deleteMovie.invoke().code == 200) {
                        allMovie.removeIf { it1 -> it1.id == idMovieEdit }
                        allMoviesAdapter.setData(allMovie)
                    }
                    Toast.makeText(this, it.deleteMovie.invoke().message, Toast.LENGTH_SHORT).show()
                    searchViewModel.handleRemoveStateDeleteMovies()
                    views.searchResultsLoader.hide()
                }

                is Loading -> {
                    views.searchResultsLoader.show()
                }

                is Fail -> {
                    Toast.makeText(this, "${it.deleteMovie.error}", Toast.LENGTH_SHORT).show()
                    views.searchResultsLoader.hide()
                    searchViewModel.handleRemoveStateDeleteMovies()
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


    override fun getBinding(): ActivityAdminBinding {
        return ActivityAdminBinding.inflate(layoutInflater)
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(initialState: SearchViewState): SearchViewModel {
        return searchViewModelFactory.create(initialState)
    }
}