package com.mtprorun.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.geoIpCacheStore: DataStore<Preferences> by preferencesDataStore(name = "geoip_cache")

@Singleton
class GeoIpCache @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun get(host: String): String? {
        val key = stringPreferencesKey("country_$host")
        return context.geoIpCacheStore.data.map { prefs -> prefs[key] }.first()
    }

    suspend fun save(host: String, countryCode: String) {
        val key = stringPreferencesKey("country_$host")
        val timestampKey = longPreferencesKey("country_ts_$host")
        context.geoIpCacheStore.edit { prefs ->
            prefs[key] = countryCode
            prefs[timestampKey] = System.currentTimeMillis()
        }
    }

    fun getAll(): Flow<Map<String, String>> = context.geoIpCacheStore.data.map { prefs ->
        prefs.asMap()
            .filterKeys { it.name.startsWith("country_") && !it.name.startsWith("country_ts_") }
            .associate { (key, value) ->
                key.name.removePrefix("country_") to value.toString()
            }
    }

    suspend fun clear() {
        context.geoIpCacheStore.edit { it.clear() }
    }
}
