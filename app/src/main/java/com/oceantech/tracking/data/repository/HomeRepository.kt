package com.oceantech.tracking.data.repository


import com.oceantech.tracking.data.models.Home
import com.oceantech.tracking.data.models.PhimBo
import com.oceantech.tracking.data.models.PhimHoatHinh
import com.oceantech.tracking.data.models.PhimLe
import com.oceantech.tracking.data.network.HomeApi
import javax.inject.Singleton
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers


@Singleton
class HomeRepository(val api: HomeApi) {
    fun getHome(): Observable<Home> = api.getHome().subscribeOn(Schedulers.io())

    fun getPhimBo(): Observable<PhimBo> = api.getPhimBo().subscribeOn(Schedulers.io())
    fun getPhimLe(): Observable<PhimLe> = api.getPhimLe().subscribeOn(Schedulers.io())
    fun getPhimHoatHinh(): Observable<PhimHoatHinh> = api.getPhimHoatHinh().subscribeOn(Schedulers.io())
}