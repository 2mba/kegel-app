package org.tumba.kegel_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import org.tumba.kegel_app.analytics.ScreenTracker
import org.tumba.kegel_app.di.appComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var screenTracker: ScreenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_main)
        screenTracker.init(findNavController(R.id.navFragment))
    }
}
