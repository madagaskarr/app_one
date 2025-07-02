package io.tigranes.app_one

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.tigranes.app_one.notifications.NotificationChannels
import io.tigranes.app_one.notifications.NotificationScheduler
import io.tigranes.app_one.workers.WorkManagerInitializer
import javax.inject.Inject

@HiltAndroidApp
class CommitmentApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var workManagerInitializer: WorkManagerInitializer
    
    @Inject
    lateinit var notificationScheduler: NotificationScheduler
    
    override fun onCreate() {
        super.onCreate()
        
        // Create notification channels
        NotificationChannels.createChannels(this)
        
        // Initialize periodic work
        workManagerInitializer.initialize()
        
        // NotificationScheduler will automatically schedule notifications based on preferences
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}