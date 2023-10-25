package dev.son.movie.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.netflixclone.constants.CATEGORIES
import com.netflixclone.constants.COUNTRIES
import dev.son.movie.TrackingApplication
import dev.son.movie.adapters.TypeMoviesAdapter
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityCategoryMoviesBinding
import dev.son.movie.network.models.home.Items
import dev.son.movie.ui.home.HomeViewAction
import dev.son.movie.ui.home.HomeViewModel
import dev.son.movie.ui.home.HomeViewState
import javax.inject.Inject

class CategoryMoviesActivity : TrackingBaseActivity<ActivityCategoryMoviesBinding>(),
    HomeViewModel.Factory {
    private val homeViewModel: HomeViewModel by viewModel()

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    private lateinit var typeMoviesAdapter: TypeMoviesAdapter
    private val categorySlug: String?
        get() = intent.extras?.getString("slug")
    private val categoryName: String?
        get() = intent.extras?.getString("name")

    private val categoryTitle: String?
        get() = intent.extras?.getString("title")

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setUpViewModel()
        setupUI()
        fetchData()
        homeViewModel.subscribe(this) {
            when (it.categoriesMovies) {
                is Success -> {
                    typeMoviesAdapter.submitList(it.categoriesMovies.invoke().data?.items)
                }

                is Fail -> {
                    Toast.makeText(this, "Vui lòng thử lại sau", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
            when (it.countriesMovies) {
                is Success -> {
                    typeMoviesAdapter.submitList(it.countriesMovies.invoke().data?.items)
                }

                is Fail -> {
                    Toast.makeText(this, "Vui lòng thử lại sau", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun setUpViewModel() {
        if (categoryTitle.equals(CATEGORIES)) {
            categorySlug?.let { HomeViewAction.getCategoriesMovies(it) }
                ?.let { homeViewModel.handle(it) }
        } else if (categoryTitle.equals(COUNTRIES)) {
            categorySlug?.let { HomeViewAction.getCountriesMovies(it) }
                ?.let { homeViewModel.handle(it) }
        }
    }

    override fun getBinding(): ActivityCategoryMoviesBinding {
        return ActivityCategoryMoviesBinding.inflate(layoutInflater)
    }

    private fun setupUI() {
        views.toolbar.title = categoryName
        views.toolbar.setNavigationOnClickListener { finish() }
        typeMoviesAdapter = TypeMoviesAdapter(this::handleMovieClick)
        views.popularMoviesList.adapter = typeMoviesAdapter
    }

    private fun fetchData() {

    }

    fun handleMovieClick(items: Items, itemView: View) {
        val categoryList = items.category
        val shuffledIndices = categoryList.indices.shuffled()
        val randomIndex = shuffledIndices.first()
        val randomCategory = categoryList[randomIndex]
        val randomSlug = randomCategory.slug
        val intent: Intent
        if (items.type == "single") {
            intent = Intent(this, MovieDetailsActivity::class.java)
        } else {
            intent = Intent(this, TvDetailsActivity::class.java)
            intent.putExtra("thumbUrl", items.thumbUrl)
        }
        intent.putExtra("name", items.slug)
        intent.putExtra("category", randomSlug)


        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            itemView,
            "my_shared_element"
        )
        startActivity(intent, options.toBundle())
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

}