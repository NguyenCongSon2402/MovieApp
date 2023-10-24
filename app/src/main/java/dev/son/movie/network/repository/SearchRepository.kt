package dev.son.movie.network.repository

import dev.son.movie.network.models.Slug.Slug
import dev.son.movie.network.models.search.Search
import dev.son.movie.network.service.HomeApi
import dev.son.movie.network.service.SearchApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class SearchRepository(val api: SearchApi) {
    fun search(name: String): Observable<Search> =
        api.searchMovies(name).subscribeOn(Schedulers.io())
}