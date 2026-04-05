package com.mtprorun.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreManager @Inject constructor(private val context: Context) {

    companion object {
        val AUTO_UPDATE_INTERVAL = intPreferencesKey("auto_update_interval")
        val PING_THRESHOLD = intPreferencesKey("ping_threshold")
        val SELECTED_SOURCES = stringPreferencesKey("selected_sources")
        val LANGUAGE = stringPreferencesKey("language")
        val LAST_UPDATE = longPreferencesKey("last_update")
        val ENABLE_GEOIP_LOOKUP = booleanPreferencesKey("enable_geoip_lookup")
        val PROXY_CACHE = stringPreferencesKey("proxy_cache")
        val PROXY_CACHE_TIMESTAMP = longPreferencesKey("proxy_cache_timestamp")
    }

    val autoUpdateInterval: Flow<Int> = context.settingsStore.data.map { prefs ->
        prefs[AUTO_UPDATE_INTERVAL] ?: 15
    }

    val pingThreshold: Flow<Int> = context.settingsStore.data.map { prefs ->
        prefs[PING_THRESHOLD] ?: 1000
    }

    val selectedSources: Flow<String> = context.settingsStore.data.map { prefs ->
        prefs[SELECTED_SOURCES] ?: "github"
    }

    val language: Flow<String> = context.settingsStore.data.map { prefs ->
        prefs[LANGUAGE] ?: "auto"
    }

    val enableGeoIpLookup: Flow<Boolean> = context.settingsStore.data.map { prefs ->
        prefs[ENABLE_GEOIP_LOOKUP] ?: true
    }

    val proxyCache: Flow<String> = context.settingsStore.data.map { prefs ->
        prefs[PROXY_CACHE] ?: ""
    }

    val proxyCacheTimestamp: Flow<Long> = context.settingsStore.data.map { prefs ->
        prefs[PROXY_CACHE_TIMESTAMP] ?: 0L
    }

    suspend fun setAutoUpdateInterval(minutes: Int) {
        context.settingsStore.edit { prefs -> prefs[AUTO_UPDATE_INTERVAL] = minutes }
    }

    suspend fun setPingThreshold(ms: Int) {
        context.settingsStore.edit { prefs -> prefs[PING_THRESHOLD] = ms }
    }

    suspend fun setSelectedSources(sources: String) {
        context.settingsStore.edit { prefs -> prefs[SELECTED_SOURCES] = sources }
    }

    suspend fun setLanguage(lang: String) {
        context.settingsStore.edit { prefs -> prefs[LANGUAGE] = lang }
    }

    suspend fun setEnableGeoIpLookup(enabled: Boolean) {
        context.settingsStore.edit { prefs -> prefs[ENABLE_GEOIP_LOOKUP] = enabled }
    }

    suspend fun setProxyCache(json: String) {
        context.settingsStore.edit { prefs ->
            prefs[PROXY_CACHE] = json
            prefs[PROXY_CACHE_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun setLastUpdate(timestamp: Long) {
        context.settingsStore.edit { prefs -> prefs[LAST_UPDATE] = timestamp }
    }

    suspend fun clearCache() {
        context.settingsStore.edit { prefs ->
            prefs.remove(PROXY_CACHE)
            prefs.remove(PROXY_CACHE_TIMESTAMP)
            prefs.remove(LAST_UPDATE)
        }
    }
}
