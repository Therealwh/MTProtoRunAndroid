package com.mtprorun.domain.usecase

import com.mtprorun.domain.repository.ProxyRepository
import kotlinx.coroutines.flow.Flow
import com.mtprorun.domain.model.ProxyUi
import javax.inject.Inject

class GetProxiesUseCase @Inject constructor(
    private val repository: ProxyRepository
) {
    operator fun invoke(): Flow<List<ProxyUi>> = repository.getProxiesStream()
}
