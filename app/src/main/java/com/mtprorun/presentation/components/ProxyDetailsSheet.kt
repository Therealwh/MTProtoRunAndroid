package com.mtprorun.presentation.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mtprorun.R
import com.mtprorun.domain.model.ProxyUi
import com.mtprorun.presentation.theme.*
import com.mtprorun.ui.utils.toFlagEmoji
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProxyDetailsBottomSheet(
    proxy: ProxyUi,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showToast by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.proxy_details), style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(
                text = proxy.countryCode.toFlagEmoji(),
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(stringResource(R.string.host), proxy.host)
                DetailRow(stringResource(R.string.port), proxy.port.toString())
                DetailRow(stringResource(R.string.country), "${proxy.countryCode.toFlagEmoji()} ${proxy.countryCode}")
                DetailRow(stringResource(R.string.method), proxy.method ?: "N/A")
                DetailRow(stringResource(R.string.ping), if (proxy.pingMs != null) "${proxy.pingMs} ms" else stringResource(R.string.not_checked))
                DetailRow(stringResource(R.string.status), if (proxy.isOnline) stringResource(R.string.online) else stringResource(R.string.offline))
                if (proxy.lastChecked > 0) {
                    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    DetailRow(stringResource(R.string.last_checked), format.format(Date(proxy.lastChecked)))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.proxy_link), style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0f172a))
                .padding(12.dp)
        ) {
            Text(
                text = proxy.tgLink,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(proxy.tgLink))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        showToast = context.getString(R.string.telegram_not_installed)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.open))
            }

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Proxy Link", proxy.tgLink))
                    showToast = context.getString(R.string.copied)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.copy))
            }

            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, proxy.tgLink)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }

    showToast?.let { message ->
        LaunchedEffect(message) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
            showToast = null
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
