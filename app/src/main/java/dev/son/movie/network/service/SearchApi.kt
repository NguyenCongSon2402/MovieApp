package dev.son.movie.network.service

import dev.son.movie.network.models.search.Search
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("/v1/api/tim-kiem")
    fun searchMovies(@Query("keyword") keyword: String): Observable<Search>
}