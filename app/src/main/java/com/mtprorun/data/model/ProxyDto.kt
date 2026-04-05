package com.mtprorun.data.model

import com.google.gson.annotations.SerializedName

data class ProxyDto(
    @SerializedName("host")
    val host: String,
    @SerializedName("port")
    val port: Int,
    @SerializedName("secret")
    val secret: String,
    @SerializedName("link")
    val link: String? = null,
    @SerializedName("ping")
    val ping: Double? = null,
    @SerializedName("region")
    val region: String? = null,
    @SerializedName("domain")
    val domain: String? = null,
    @SerializedName("method")
    val method: String? = null
)
