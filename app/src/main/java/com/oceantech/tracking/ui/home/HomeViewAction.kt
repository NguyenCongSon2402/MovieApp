package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class HomeViewAction : NimpeViewModelAction {
    object getHome : HomeViewAction()

}
