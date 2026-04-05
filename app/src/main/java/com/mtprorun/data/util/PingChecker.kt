package com.mtprorun.data.util

import com.mtprorun.data.model.ProxyPingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class PingChecker @Inject constructor() {

    suspend fun checkProxy(host: String, port: Int, timeoutMs: Long = 5000): ProxyPingResult =
        withContext(Dispatchers.IO) {
            try {
                val socket = Socket()
                val start = System.currentTimeMillis()
                socket.connect(InetSocketAddress(host, port), timeoutMs.toInt())
                val end = System.currentTimeMillis()
                socket.close()
                val pingMs = (end - start).toInt()
                ProxyPingResult(isSuccess = true, pingMs = pingMs)
            } catch (e: Exception) {
                ProxyPingResult(
                    isSuccess = false,
                    pingMs = null,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }

    companion object {
        fun isIpAddress(host: String): Boolean {
            val ipPattern = Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
            return ipPattern.matches(host)
        }
    }
}
