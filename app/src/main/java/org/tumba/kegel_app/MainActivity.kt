package org.tumba.kegel_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.tumba.kegel_app.utils.gone
import org.tumba.kegel_app.utils.show

class MainActivity : AppCompatActivity() {

    private val navBarScreens = listOf(
        R.id.screen_home,
        R.id.screen_notifications
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
