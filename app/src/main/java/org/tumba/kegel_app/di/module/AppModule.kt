package org.tumba.kegel_app.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.KegelApplication
import org.tumba.kegel_app.analytics.Analytics
import org.tumba.kegel_app.analytics.FirebaseAnalytics
import org.tumba.kegel_app.config.RemoteConfigFetcher
import org.tumba.kegel_app.config.RemoteConfigFetcherImpl
import org.tumba.kegel_app.core.system.*
import javax.inject.Singleton

@Module
class AppModule(private val application: KegelApplication) {

    @Provides
    fun provideApplication(): KegelApplication = application

    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideResourceProvider(context: Context): ResourceProvider =
        ResourceProviderImpl(context.applicationContext.resources)

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAnalytics(analytics: FirebaseAnalytics): Analytics = analytics

    @Provides
    fun provideWorkerManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideReviewManagerFactory(context: Context): ReviewManager {
        return ReviewManagerFactory.create(context)
    }

    @Provides
    fun provideNightModeManager(impl: NightModeManagerImpl): NightModeManager = impl

    @Provides
    fun providePermissionProvider(impl: PermissionProviderImpl): PermissionProvider = impl

    @Provides
    fun provideRemoteConfigFetcher(impl: RemoteConfigFetcherImpl): RemoteConfigFetcher = impl

    companion object {
        private const val APP_PREFERENCES_NAME = "APP_PREFERENCES_NAME"
    }
}