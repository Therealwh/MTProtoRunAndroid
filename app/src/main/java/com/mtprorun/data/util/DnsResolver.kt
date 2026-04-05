package com.mtprorun.data.util

import java.net.InetAddress
import javax.inject.Inject

class DnsResolver @Inject constructor() {

    suspend fun resolveToIp(host: String): String? = try {
        InetAddress.getByName(host).hostAddress
    } catch (e: Exception) {
        null
    }
}
