package github.psicodes.ktxpy

import com.google.android.material.color.DynamicColors
import timber.log.Timber

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Khởi tạo Timber - chỉ log trong debug build
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}