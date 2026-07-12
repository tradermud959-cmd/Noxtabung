package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Settings
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.RedNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.White

@Composable
fun SettingsPanel(
    settings: Settings?,
    onUpdateSettings: (Settings) -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onExport: () -> Unit,
    onClearHistory: () -> Unit,
    onClose: () -> Unit
) {
    val currentSettings = settings ?: Settings()
    var showClearConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = EmeraldNeon)
                .border(1.dp, EmeraldNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .background(Color(0xFF121212).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENGATURAN",
                    color = EmeraldNeon,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = White)
                }
            }

            HorizontalDivider(color = Color.DarkGray)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Tampilan
                item {
                    SectionHeader("🎨 Tampilan")
                    SettingsSwitch("Mode Gelap", currentSettings.darkTheme) {
                        onUpdateSettings(currentSettings.copy(darkTheme = it))
                    }
                    SettingsSwitch("Efek Glow Neon", currentSettings.neonGlow) {
                        onUpdateSettings(currentSettings.copy(neonGlow = it))
                    }
                    SettingsSwitch("Animasi Mata Nox", currentSettings.noxEyeAnimation) {
                        onUpdateSettings(currentSettings.copy(noxEyeAnimation = it))
                    }
                    SettingsSwitch("Animasi Idle", currentSettings.idleAnimation) {
                        onUpdateSettings(currentSettings.copy(idleAnimation = it))
                    }
                    DividerSection()
                }

                // Audio
                item {
                    SectionHeader("🔊 Audio")
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text("Volume Suara Nox", color = White, fontSize = 16.sp)
                        Slider(
                            value = currentSettings.noxVolume,
                            onValueChange = { onUpdateSettings(currentSettings.copy(noxVolume = it)) },
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldNeon,
                                activeTrackColor = EmeraldNeon,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                    }
                    SettingsSwitch("Efek Suara", currentSettings.soundEffects) {
                        onUpdateSettings(currentSettings.copy(soundEffects = it))
                    }
                    SettingsSwitch("Getar Saat Berhasil", currentSettings.hapticFeedback) {
                        onUpdateSettings(currentSettings.copy(hapticFeedback = it))
                    }
                    DividerSection()
                }

                // Keamanan
                item {
                    SectionHeader("🔒 Keamanan")
                    SettingsSwitch("Aktifkan PIN Aplikasi", currentSettings.pinEnabled) {
                        onUpdateSettings(currentSettings.copy(pinEnabled = it))
                    }
                    SettingsAction("Ubah PIN") {
                        // TODO
                    }
                    SettingsSwitch("Login Sidik Jari", currentSettings.fingerprintLogin) {
                        onUpdateSettings(currentSettings.copy(fingerprintLogin = it))
                    }
                    DividerSection()
                }

                // Data
                item {
                    SectionHeader("💾 Data")
                    SettingsAction("Backup Data", onClick = onBackup)
                    SettingsAction("Restore Data", onClick = onRestore)
                    SettingsAction("Export Riwayat (.json)", onClick = onExport)
                    SettingsAction("Hapus Semua Riwayat", color = RedNeon) {
                        showClearConfirm = true
                    }
                    DividerSection()
                }

                // Tentang
                item {
                    SectionHeader("🤖 Tentang Nox")
                    InfoRow("Versi Aplikasi", "1.0.0")
                    InfoRow("Versi Database", "v1")
                    InfoRow("Developer", "Nox Studio")
                    InfoRow("Lisensi", "MIT")
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Hapus Semua Riwayat", color = RedNeon, fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus seluruh riwayat transaksi?\nTindakan ini tidak dapat dibatalkan.", color = White) },
            containerColor = SurfaceDark,
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    onClearHistory()
                }) {
                    Text("Hapus", color = RedNeon)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Batal", color = White)
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = EmeraldNeon,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitch(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = White, fontSize = 16.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EmeraldNeon,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
}

@Composable
fun SettingsAction(title: String, color: Color = White, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = color, fontSize = 16.sp)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = White, fontSize = 14.sp)
    }
}

@Composable
fun DividerSection() {
    HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
}
