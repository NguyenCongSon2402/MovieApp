package com.oceantech.tracking.adapters

import com.airbnb.epoxy.AsyncEpoxyController
import com.oceantech.tracking.model.headerItem

class HomeItemsController : AsyncEpoxyController() {
    override fun buildModels() {
        headerItem {
            id("header")
            title1("Action")
        }
    }
}