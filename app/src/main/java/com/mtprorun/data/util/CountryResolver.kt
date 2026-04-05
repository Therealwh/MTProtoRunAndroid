package com.mtprorun.data.util

import com.mtprorun.data.local.GeoIpCache
import com.mtprorun.data.remote.GeoIpApiService
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountryResolver @Inject constructor(
    private val geoIpApi: GeoIpApiService,
    private val geoIpCache: GeoIpCache,
    private val dnsResolver: DnsResolver
) {

    suspend fun resolveCountry(host: String, regionHint: String?): String {
        geoIpCache.get(host)?.let { return it }

        RegionToCountryMapper.mapRegion(regionHint)?.takeIf { it != "EU" }?.let {
            return it
        }

        val ipToCheck = if (PingChecker.isIpAddress(host)) {
            host
        } else {
            dnsResolver.resolveToIp(host)
        }

        if (!ipToCheck.isNullOrBlank()) {
            try {
                delay(150)
                val response = geoIpApi.getCountryByIp(ipToCheck)
                if (response.status == "success" && !response.countryCode.isNullOrBlank()) {
                    val code = response.countryCode.uppercase()
                    geoIpCache.save(host, code)
                    return code
                }
            } catch (e: Exception) {
                // Fallback to region hint
            }
        }

        return RegionToCountryMapper.mapRegion(regionHint)?.uppercase() ?: "??"
    }
}
