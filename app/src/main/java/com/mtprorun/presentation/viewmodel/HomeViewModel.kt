package com.mtprorun.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mtprorun.data.model.ProxyPingResult
import com.mtprorun.domain.model.FilterState
import com.mtprorun.domain.model.ProxyUi
import com.mtprorun.domain.usecase.CheckProxyPingUseCase
import com.mtprorun.domain.usecase.FilterProxiesUseCase
import com.mtprorun.domain.usecase.GetProxiesUseCase
import com.mtprorun.domain.usecase.RefreshProxiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val proxies: List<ProxyUi> = emptyList(),
    val filteredProxies: List<ProxyUi> = emptyList(),
    val filters: FilterState = FilterState(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val countries: List<String> = emptyList(),
    val selectedProxy: ProxyUi? = null,
    val showFilters: Boolean = false,
    val showDetails: Boolean = false,
    val favorites: Set<String> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProxies: GetProxiesUseCase,
    private val refreshProxies: RefreshProxiesUseCase,
    private val checkPing: CheckProxyPingUseCase,
    private val filterProxies: FilterProxiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var filterJob: Job? = null

    init {
        observeProxies()
    }

    private fun observeProxies() {
        getProxies()
            .onEach { proxies ->
                _uiState.update { state ->
                    state.copy(
                        proxies = proxies,
                        countries = proxies.map { it.countryCode }.distinct().sorted(),
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                applyFilters()
            }
            .launchIn(viewModelScope)
    }

    fun loadProxies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = refreshProxies()
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message, isLoading = false) }
            }
        }
    }

    fun refreshProxiesAction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            val result = refreshProxies()
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message, isRefreshing = false) }
            }
        }
    }
        }
    }

    fun refreshProxiesAction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            val result = refreshProxies()
            if (result.isFailure) {
                _uiState.update { it.copy(error = result.exceptionOrNull()?.message, isRefreshing = false) }
            }
        }
    }

    fun updateFilters(filters: FilterState) {
        _uiState.update { it.copy(filters = filters) }
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(filters = it.filters.copy(searchQuery = query)) }
        applyFiltersDebounced()
    }

    @OptIn(FlowPreview::class)
    private fun applyFiltersDebounced() {
        filterJob?.cancel()
        filterJob = _uiState
            .debounce(300)
            .onEach { applyFilters() }
            .launchIn(viewModelScope)
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = filterProxies(state.filters)
        _uiState.update { it.copy(filteredProxies = filtered) }
    }

    fun checkPingForProxy(proxyId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = checkPing(proxyId)
            if (result.isFailure) {
                _uiState.update { state ->
                    state.copy(
                        filteredProxies = filterProxies(state.filters)
                    )
                }
            } else {
                _uiState.update { state ->
                    state.copy(
                        filteredProxies = filterProxies(state.filters)
                    )
                }
            }
        }
    }

    fun onProxyClick(proxy: ProxyUi) {
        _uiState.update { it.copy(selectedProxy = proxy, showDetails = true) }
    }

    fun dismissDetails() {
        _uiState.update { it.copy(showDetails = false, selectedProxy = null) }
    }

    fun showFilters() {
        _uiState.update { it.copy(showFilters = true) }
    }

    fun dismissFilters() {
        _uiState.update { it.copy(showFilters = false) }
    }

    fun toggleFavorite(proxyId: String) {
        _uiState.update { state ->
            val favorites = state.favorites.toMutableSet()
            if (favorites.contains(proxyId)) {
                favorites.remove(proxyId)
            } else {
                favorites.add(proxyId)
            }
            state.copy(favorites = favorites)
        }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error, isLoading = false) }
    }
}
