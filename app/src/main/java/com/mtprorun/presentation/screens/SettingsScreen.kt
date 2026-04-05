package com.mtprorun.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mtprorun.presentation.components.GlassSurface
import com.mtprorun.presentation.theme.*
import com.mtprorun.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundStart, BackgroundEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            GlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("General", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsRow(
                        title = "Auto-update interval",
                        value = when (uiState.autoUpdateInterval) {
                            5 -> "5 min"
                            15 -> "15 min"
                            30 -> "30 min"
                            else -> "Disabled"
                        },
                        onClick = {
                            val intervals = listOf(0, 5, 15, 30)
                            val currentIndex = intervals.indexOf(uiState.autoUpdateInterval)
                            val nextIndex = (currentIndex + 1) % intervals.size
                            viewModel.setAutoUpdateInterval(intervals[nextIndex])
                        }
                    )

                    Divider(color = SurfaceBorder, thickness = 1.dp)

                    SettingsRow(
                        title = "Ping threshold",
                        value = "${uiState.pingThreshold} ms",
                        onClick = {
                            val thresholds = listOf(100, 200, 500, 1000)
                            val currentIndex = thresholds.indexOf(uiState.pingThreshold)
                            val nextIndex = (currentIndex + 1) % thresholds.size
                            viewModel.setPingThreshold(thresholds[nextIndex])
                        }
                    )

                    Divider(color = SurfaceBorder, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setEnableGeoIp(!uiState.enableGeoIp) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GeoIP lookup", color = TextPrimary)
                        Switch(
                            checked = uiState.enableGeoIp,
                            onCheckedChange = { viewModel.setEnableGeoIp(it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cache", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.clearCache() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isClearingCache,
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                    ) {
                        if (uiState.isClearingCache) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Clear Cache")
                        }
                    }

                    if (uiState.cacheCleared) {
                        LaunchedEffect(Unit) {
                            viewModel.resetCacheClearedFlag()
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cache cleared!", color = SuccessColor, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About", style = MaterialTheme.typography.labelLarge, color = PrimaryColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsRow(
                        title = "Version",
                        value = "1.0.0",
                        onClick = {}
                    )

                    Divider(color = SurfaceBorder, thickness = 1.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { uriHandler.openUri("https://github.com/mtprorun/MTProtoRun") }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("GitHub", color = TextPrimary)
                        Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary)
        Text(value, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}
