package io.tigranes.app_one.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.tigranes.app_one.data.backup.BackupManager
import io.tigranes.app_one.data.backup.ImportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()
    
    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null) }
            
            backupManager.exportData(uri).fold(
                onSuccess = {
                    _uiState.update { 
                        it.copy(
                            isExporting = false,
                            exportSuccess = true,
                            message = "Data exported successfully"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isExporting = false,
                            error = "Export failed: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, error = null) }
            
            backupManager.importData(uri).fold(
                onSuccess = { result ->
                    _uiState.update { 
                        it.copy(
                            isImporting = false,
                            importSuccess = true,
                            message = "Imported ${result.tasksImported} tasks and ${result.moodsImported} moods",
                            lastImportResult = result
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isImporting = false,
                            error = "Import failed: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearing = true, error = null) }
            
            try {
                backupManager.clearAllData()
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        clearSuccess = true,
                        message = "All data cleared successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isClearing = false,
                        error = "Clear failed: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun dismissMessage() {
        _uiState.update { 
            it.copy(
                message = null,
                error = null,
                exportSuccess = false,
                importSuccess = false,
                clearSuccess = false
            )
        }
    }
}

data class BackupUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val isClearing: Boolean = false,
    val exportSuccess: Boolean = false,
    val importSuccess: Boolean = false,
    val clearSuccess: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val lastImportResult: ImportResult? = null
)