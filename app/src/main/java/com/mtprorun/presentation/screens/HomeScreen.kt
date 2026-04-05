package com.mtprorun.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mtprorun.domain.model.FilterState
import com.mtprorun.presentation.components.*
import com.mtprorun.presentation.theme.*
import com.mtprorun.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundStart, BackgroundEnd)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("MTProtoRun", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { viewModel.refreshProxiesAction() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextPrimary)
                    }
                    IconButton(onClick = { viewModel.showFilters() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = TextPrimary)
                    }
                }
            )

            OutlinedTextField(
                value = uiState.filters.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search proxies...", color = TextSecondary) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = SurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (uiState.isLoading && uiState.proxies.isEmpty()) {
                LoadingSkeleton()
            } else if (uiState.error != null && uiState.proxies.isEmpty()) {
                ErrorView(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadProxies() }
                )
            } else if (uiState.filteredProxies.isEmpty() && uiState.proxies.isNotEmpty()) {
                EmptyState("No proxies match your filters")
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.filteredProxies,
                        key = { it.id }
                    ) { proxy ->
                        ProxyCard(
                            proxy = proxy,
                            isFavorite = uiState.favorites.contains(proxy.id),
                            onCheckPing = { viewModel.checkPingForProxy(proxy.id) },
                            onClick = { viewModel.onProxyClick(proxy) },
                            onOpen = {
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(proxy.tgLink)
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Telegram not installed",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onCopy = {
                                android.widget.Toast.makeText(
                                    context,
                                    "Copied!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(proxy.id) }
                        )
                    }
                }
            }
        }

        if (uiState.showFilters) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissFilters() },
                containerColor = Color(0xFF0f172a),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .padding(8.dp)
                            .background(TextSecondary.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )
                }
            ) {
                FilterBottomSheet(
                    countries = uiState.countries,
                    currentFilters = uiState.filters,
                    onDismiss = { viewModel.dismissFilters() },
                    onApply = { viewModel.updateFilters(it) }
                )
            }
        }

        if (uiState.showDetails && uiState.selectedProxy != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissDetails() },
                containerColor = Color(0xFF0f172a),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .padding(8.dp)
                            .background(TextSecondary.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )
                }
            ) {
                ProxyDetailsBottomSheet(
                    proxy = uiState.selectedProxy!!,
                    onDismiss = { viewModel.dismissDetails() }
                )
            }
        }
    }
}
