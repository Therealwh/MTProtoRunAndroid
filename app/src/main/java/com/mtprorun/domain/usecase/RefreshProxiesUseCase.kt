package com.mtprorun.domain.usecase

import com.mtprorun.domain.repository.ProxyRepository
import javax.inject.Inject

class RefreshProxiesUseCase @Inject constructor(
    private val repository: ProxyRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.refreshProxies()
}
