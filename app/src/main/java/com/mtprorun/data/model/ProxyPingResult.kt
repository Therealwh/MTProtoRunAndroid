package com.mtprorun.data.model

data class ProxyPingResult(
    val isSuccess: Boolean,
    val pingMs: Int?,
    val errorMessage: String? = null
)
