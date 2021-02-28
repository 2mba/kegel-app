package org.tumba.kegel_app.ui.home

import javax.inject.Inject

// TODO this is workaround of fragment recreation when switching tabs
class ProgressViewedStore @Inject constructor() {
    var isProgressViewed: Boolean = false
}