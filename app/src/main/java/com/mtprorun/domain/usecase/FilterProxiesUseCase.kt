package com.mtprorun.domain.usecase

import com.mtprorun.domain.model.FilterState
import com.mtprorun.domain.model.ProxyUi
import com.mtprorun.domain.repository.ProxyRepository
import javax.inject.Inject

class FilterProxiesUseCase @Inject constructor(
    private val repository: ProxyRepository
) {
    operator fun invoke(filters: FilterState): List<ProxyUi> =
        repository.applyFilters(filters)
}
