package com.mtprorun.domain.model

data class FilterState(
    val selectedCountries: Set<String> = emptySet(),
    val maxPingMs: Int? = null,
    val onlyOnline: Boolean = false,
    val searchQuery: String = "",
    val sortBy: SortOption = SortOption.PING_ASC
)

enum class SortOption { PING_ASC, PING_DESC, COUNTRY, ADDED }
