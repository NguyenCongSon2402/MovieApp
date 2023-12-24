package dev.son.movie.network.repository


import dev.son.movie.network.models.Favorite.addFavorite
import dev.son.movie.network.models.countries.Countries
import dev.son.movie.network.models.countries.CountriesMovie
import dev.son.movie.network.models.movie.ApiResponse
import dev.son.movie.network.models.movie.Genre
import dev.son.movie.network.models.movie.Movie
import dev.son.movie.network.models.postcomment.Comment
import dev.son.movie.network.models.rate.RateRespone
import dev.son.movie.network.service.HomeApi
import javax.inject.Singleton
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


@Singleton
class HomeRepository(val api: HomeApi) {
    fun getGenre(): Observable<ApiResponse<List<Genre>>> = api.getGenres().subscribeOn(Schedulers.io())
    fun getCountries(): Observable<ApiResponse<List<Genre>>> = api.getCountries().subscribeOn(Schedulers.io())
    fun getMoviePhimBo(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMoviesPhimBo(genreCode).subscribeOn(Schedulers.io())
    fun getMoviePhimLe(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMoviesPhimLe(genreCode).subscribeOn(Schedulers.io())
    fun getMovieHoatHinh(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieHoatHinh(genreCode).subscribeOn(Schedulers.io())
    fun getMovieTvShow(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieTvShow(genreCode).subscribeOn(Schedulers.io())
    fun getMovieVietSub(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieVietSub(genreCode).subscribeOn(Schedulers.io())
    fun getMovieThuyetMinh(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieThuyetMinh(genreCode).subscribeOn(Schedulers.io())
    fun getMovieDangChieu(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieDangChieu(genreCode).subscribeOn(Schedulers.io())
    fun getMovieHoanThanh(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieHoanThanh(genreCode).subscribeOn(Schedulers.io())
    fun getMovieSimilar(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieSimilar(genreCode).subscribeOn(Schedulers.io())
    fun getMovieRateRes(movieId: String): Observable<ApiResponse<RateRespone>> =
        api.getMovieRateRes(movieId).subscribeOn(Schedulers.io())
    fun setMovieRate(movieId: String,rating:Int): Observable<ApiResponse<RateRespone>> =
        api.setMovieRate(movieId,rating).subscribeOn(Schedulers.io())
    fun addFavorite(movieId: String): Observable<ApiResponse<addFavorite>> =
        api.addFavorite(movieId).subscribeOn(Schedulers.io())
    fun removeFavorite(movieId: String): Observable<ApiResponse<String>> =
        api.removeFavorite(movieId).subscribeOn(Schedulers.io())
    fun getMovieById(movieId: String): Observable<ApiResponse<Movie>> =
        api.getMovieById(movieId).subscribeOn(Schedulers.io())
    fun getCommentByMovie(movieId: String): Observable<ApiResponse<List<Comment>>> =
        api.getCommentByMovie(movieId).subscribeOn(Schedulers.io())
    fun createComment(movieId: String,comment: String): Observable<ApiResponse<Comment>> =
        api.createComment(movieId,comment).subscribeOn(Schedulers.io())
    fun getMovieByCategory(genreCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMovieByCategory(genreCode).subscribeOn(Schedulers.io())
    fun getMoviesRecommendation(movieId: String): Observable<ApiResponse<List<Movie>>> =
        api.getMoviesRecommendation(movieId).subscribeOn(Schedulers.io())
    fun getMoviesByCountry(countryCode: String): Observable<ApiResponse<List<Movie>>> =
        api.getMoviesByCountry(countryCode).subscribeOn(Schedulers.io())



    fun getCountriesMovie(): Observable<Countries> =
        api.getCountriesMovies().subscribeOn(Schedulers.io())

    fun countriesMovies(name: String): Observable<CountriesMovie> =
        api.CountriesMovies(name).subscribeOn(Schedulers.io())
}