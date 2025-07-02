package io.tigranes.app_one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.tigranes.app_one.data.preferences.PreferencesRepository
import io.tigranes.app_one.data.preferences.Theme
import io.tigranes.app_one.ui.CommitmentApp
import io.tigranes.app_one.ui.theme.App_oneTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesRepository: PreferencesRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val userPreferences = preferencesRepository.userPreferences
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
        
        setContent {
            val preferences by userPreferences.collectAsState()
            val systemDarkTheme = isSystemInDarkTheme()
            
            val darkTheme = when (preferences?.theme) {
                Theme.LIGHT -> false
                Theme.DARK -> true
                Theme.SYSTEM, null -> systemDarkTheme
            }
            
            App_oneTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CommitmentApp()
                }
            }
        }
    }
}