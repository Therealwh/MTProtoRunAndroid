package com.mtprorun.data.remote

import com.google.gson.annotations.SerializedName

data class GeoIpResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("message")
    val message: String?
)
