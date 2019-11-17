package com.sample.knowquake

import androidx.work.Configuration
import androidx.work.WorkManager
import com.sample.knowquake.di.DaggerAppComponent
import com.sample.knowquake.worker.DaggerWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class QuakeApplication : DaggerApplication() {

    @Inject
    lateinit var workerFactory: DaggerWorkerFactory

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }

}