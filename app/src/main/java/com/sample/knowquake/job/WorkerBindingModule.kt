package com.sample.knowquake.job

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerBindingModule {

    @Binds
    @IntoMap
    @WorkerKey(NewQuakeUpdateJob::class)
    abstract fun newQuakeUpdateWorker(factory: NewQuakeUpdateJob.Factory):
            DaggerWorkerFactory.ChildWorkerFactory
}