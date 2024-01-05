package dev.son.movie.ui.home

import com.airbnb.mvrx.Async

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import dev.son.movie.network.models.Favorite.addFavorite

import dev.son.movie.network.models.countries.Countries
import dev.son.movie.network.models.countries.CountriesMovie

import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.postcomment.Comment
import dev.son.movie.network.models.rate.RateRespone

data class HomeViewState(
    val genre: Async<ApiResponse<List<Genre>>> = Uninitialized,
    val movieComingSoon: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val moviePhimBo: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val moviePhimLe: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val phimHoatHinh: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieTvShow: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieVietSub: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieThuyetMinh: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieDangChieu: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieHoanThanh: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val listMovieSimilar: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val movieRateRes: Async<ApiResponse<RateRespone>> = Uninitialized,
    val setmovieRate: Async<ApiResponse<RateRespone>> = Uninitialized,
    val addFavorite: Async<ApiResponse<addFavorite>> = Uninitialized,
    val removeFavorite: Async<ApiResponse<String>> = Uninitialized,
    val movieById: Async<ApiResponse<Movie>> = Uninitialized,
    val getCommentByMovie: Async<ApiResponse<List<Comment>>> = Uninitialized,
    val createComment: Async<ApiResponse<Comment>> = Uninitialized,
    val getMovieByCategory: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val getMoviesRecommendation: Async<ApiResponse<List<Movie>>> = Uninitialized,
    val getMovieByCountry: Async<ApiResponse<List<Movie>>> = Uninitialized,

    val countriesMovies: Async<ApiResponse<List<Genre>>> = Uninitialized,

    ) : MvRxState {

}
