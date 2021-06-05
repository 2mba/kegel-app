package org.tumba.kegel_app.di.module

import dagger.Module
import dagger.Provides
import org.tumba.kegel_app.BuildConfig
import org.tumba.kegel_app.config.AppBuildConfig

@Module
class ConfigModule {

    @Provides
    fun provideAppBuildConfig(): AppBuildConfig {
        return AppBuildConfig(
            interstitialAdsUnitId = BuildConfig.ADMOB_INTERSTITIAL_ADS_UNIT_ID,
            exerciseBannerAppAdsUnitId = BuildConfig.ADMOB_EXERCISE_BANNER_ADS_UNIT_ID
        )
    }
}