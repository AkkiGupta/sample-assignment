package assignment.sample.coding

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * class: SampleApplication
 * Desc: Application class to implant Application wide resources and injections
 */
@HiltAndroidApp
class SampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        Timber.plant(Timber.DebugTree())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}