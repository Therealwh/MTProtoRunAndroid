package com.mtprorun.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtprorun.data.local.DataStoreManager
import com.mtprorun.data.local.GeoIpCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoUpdateInterval: Int = 15,
    val pingThreshold: Int = 1000,
    val enableGeoIp: Boolean = true,
    val cacheSize: String = "0 proxies",
    val isClearingCache: Boolean = false,
    val cacheCleared: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val geoIpCache: GeoIpCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        dataStoreManager.autoUpdateInterval
            .onEach { interval ->
                _uiState.update { it.copy(autoUpdateInterval = interval) }
            }
            .launchIn(viewModelScope)

        dataStoreManager.pingThreshold
            .onEach { threshold ->
                _uiState.update { it.copy(pingThreshold = threshold) }
            }
            .launchIn(viewModelScope)

        dataStoreManager.enableGeoIpLookup
            .onEach { enabled ->
                _uiState.update { it.copy(enableGeoIp = enabled) }
            }
            .launchIn(viewModelScope)
    }

    fun setAutoUpdateInterval(minutes: Int) {
        viewModelScope.launch {
            dataStoreManager.setAutoUpdateInterval(minutes)
        }
    }

    fun setPingThreshold(ms: Int) {
        viewModelScope.launch {
            dataStoreManager.setPingThreshold(ms)
        }
    }

    fun setEnableGeoIp(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setEnableGeoIpLookup(enabled)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(isClearingCache = true) }
            dataStoreManager.clearCache()
            geoIpCache.clear()
            _uiState.update {
                it.copy(
                    isClearingCache = false,
                    cacheCleared = true,
                    cacheSize = "0 proxies"
                )
            }
        }
    }

    fun resetCacheClearedFlag() {
        _uiState.update { it.copy(cacheCleared = false) }
    }
}
