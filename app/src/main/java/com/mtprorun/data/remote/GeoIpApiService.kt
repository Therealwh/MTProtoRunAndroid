package com.mtprorun.data.remote

import com.mtprorun.data.remote.GeoIpResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GeoIpApiService {
    @GET("json/{ip}")
    suspend fun getCountryByIp(@Path("ip") ip: String): GeoIpResponse
}
