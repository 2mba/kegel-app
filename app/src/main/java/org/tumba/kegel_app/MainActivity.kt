package org.tumba.kegel_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.tumba.kegel_app.analytics.ReminderNotificationTracker
import org.tumba.kegel_app.analytics.ScreenTracker
import org.tumba.kegel_app.di.appComponent
import org.tumba.kegel_app.ui.ad.InterstitialAdManager
import org.tumba.kegel_app.ui.home.ProgressViewedStore
import org.tumba.kegel_app.utils.gone
import org.tumba.kegel_app.utils.observeEvent
import org.tumba.kegel_app.utils.show
import org.tumba.kegel_app.worker.ReminderNotificationManager
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val navBarScreens = listOf(
        R.id.screenHome,
        R.id.screenSettings
    )

    private val mainScope = MainScope()

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var reminderNotificationTracker: ReminderNotificationTracker

    @Inject
    lateinit var progressViewedStore: ProgressViewedStore

    @Inject
    lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        setContentView(R.layout.activity_main)
        screenTracker.init(findNavController(R.id.navFragment))
        initNavigation()

        if (savedInstanceState == null) {
            progressViewedStore.isProgressViewed = false
            handleReminderNotificationIntent(intent)
        }

        findViewById<View>(R.id.navView)?.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
        initAds()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleReminderNotificationIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
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

    private fun handleReminderNotificationIntent(intent: Intent? = null) {
        val activityIntent = intent ?: getIntent()

        if (activityIntent.action == ReminderNotificationManager.REMINDER_NOTIFICATION_ACTION) {
            reminderNotificationTracker.trackReminderNotificationClicked()
        }
    }

    private fun initAds() {
        MobileAds.initialize(this) {}

        interstitialAdManager.interstitialAdShowEvent
            .asLiveData(mainScope.coroutineContext)
            .observeEvent(this) { ad -> ad?.show(this) }

        findNavController(R.id.navFragment).addOnDestinationChangedListener(interstitialAdManager)
    }
}
