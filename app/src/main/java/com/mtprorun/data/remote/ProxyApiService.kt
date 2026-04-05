package com.mtprorun.data.remote

import com.mtprorun.data.model.ProxyDto
import retrofit2.http.GET

interface ProxyApiService {
    @GET("Therealwh/MTPproxyLIST/refs/heads/main/verified/proxy_all_verified.json")
    suspend fun getProxies(): List<ProxyDto>
}
