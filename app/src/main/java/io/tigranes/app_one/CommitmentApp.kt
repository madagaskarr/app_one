package io.tigranes.app_one

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.tigranes.app_one.workers.WorkManagerInitializer
import javax.inject.Inject

@HiltAndroidApp
class CommitmentApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var workManagerInitializer: WorkManagerInitializer
    
    override fun onCreate() {
        super.onCreate()
        // Initialize periodic work
        workManagerInitializer.initialize()
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}