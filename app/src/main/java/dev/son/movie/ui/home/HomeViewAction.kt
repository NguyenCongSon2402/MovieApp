package dev.son.movie.ui.home

import dev.son.movie.core.NimpeViewModelAction
import dev.son.movie.network.models.postcomment.Comment

sealed class HomeViewAction : NimpeViewModelAction {



    object getGenre : HomeViewAction()
    object getCountries : HomeViewAction()
    data class  getMovieComingSoon(val genreCode:String):HomeViewAction()
    data class  getMoviePhimBo(val genreCode:String):HomeViewAction()
    data class  getMoviePhimLe(val genreCode:String):HomeViewAction()
    data class  getMoviePhimHoatHinh(val genreCode:String):HomeViewAction()
    data class  getMovieTvShow(val genreCode:String):HomeViewAction()
    data class  getMovieVietSub(val genreCode:String):HomeViewAction()
    data class  getMovieThuyetMinh(val genreCode:String):HomeViewAction()
    data class  getMovieDangChieu(val genreCode:String):HomeViewAction()
    data class  getMovieHoanThanh(val genreCode:String):HomeViewAction()
    data class  getMovieSimilar(val genreCode:String):HomeViewAction()
    data class  getMovieByCategory(val genreCode:String):HomeViewAction()
    data class  getMovieByCountry(val countryCode:String):HomeViewAction()
    data class  getMovieRate(val movieId:String):HomeViewAction()
    data class  setRateMovie(val movieId:String,val rating:Int):HomeViewAction()
    data class  addFavorite(val movieId:String):HomeViewAction()
    data class  removeFavorite(val movieId:String):HomeViewAction()
    data class  movieById(val movieId:String):HomeViewAction()
    data class  getCommentByMovie(val movieId:String):HomeViewAction()
    data class  getMoviesRecommendation(val movieId:String):HomeViewAction()
    data class  createComment(val movieId:String,val comment:String):HomeViewAction()


}
