package com.mtprorun.domain.usecase

import com.mtprorun.data.model.ProxyPingResult
import com.mtprorun.domain.repository.ProxyRepository
import javax.inject.Inject

class CheckProxyPingUseCase @Inject constructor(
    private val repository: ProxyRepository
) {
    suspend operator fun invoke(proxyId: String): Result<ProxyPingResult> =
        repository.checkProxyPing(proxyId)
}
