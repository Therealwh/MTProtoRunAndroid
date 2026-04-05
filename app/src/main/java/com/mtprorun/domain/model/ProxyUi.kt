package com.mtprorun.domain.model

data class ProxyUi(
    val id: String,
    val host: String,
    val port: Int,
    val secret: String,
    val tgLink: String,
    val pingMs: Int?,
    val countryCode: String,
    val isOnline: Boolean,
    val lastChecked: Long,
    val regionHint: String? = null,
    val method: String? = null
)
