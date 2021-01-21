package org.tumba.kegel_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.tumba.kegel_app.analytics.ScreenTracker
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.utils.gone
import org.tumba.kegel_app.utils.show
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val navBarScreens = listOf(
        R.id.screenHome,
        R.id.screenSettings
    )

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_main)
        screenTracker.init(findNavController(R.id.navFragment))
        initNavigation()
    }

    private fun initNavigation() {
        val navController = findNavController(R.id.navFragment)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in navBarScreens) {
                navView.show()
            } else {
                navView.gone()
            }
        }
    }
}
