package com.oceantech.tracking.adapters

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.oceantech.tracking.data.models.Data
import com.oceantech.tracking.data.models.Home
import com.oceantech.tracking.model.RecentlyActiveItemModel
import com.oceantech.tracking.model.RecentlyActiveItemModel_
import com.oceantech.tracking.model.headerItem

class HomeItemsController : AsyncEpoxyController() {
    var recentlyActive: Data = Data()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        headerItem {
            id("header")
            title1("Action")
        }
        val models = recentlyActive.items.map {
            RecentlyActiveItemModel_()
                .id(it.Id)
                .items(it)
        }
        carousel {
            id("recent")
            padding(Carousel.Padding.dp(0, 4, 0, 16, 8))
            models(models)
        }
    }
}