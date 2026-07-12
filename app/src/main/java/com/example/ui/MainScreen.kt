package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentTime by viewModel.currentTime.collectAsState()
    val noxExpression by viewModel.noxExpression.collectAsState()
    val showParticleEffect by viewModel.showParticleEffect.collectAsState()
    val noxMessage by viewModel.noxMessage.collectAsState()
    
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showHistoryPanel by remember { mutableStateOf(false) }
    var showSettingsPanel by remember { mutableStateOf(false) }
    var commandText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    val transactions by viewModel.transactions.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val noxQuote by viewModel.noxQuote.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiAction.collect { action ->
            if (action == "SHOW_HISTORY") {
                showHistoryPanel = true
            } else if (action == "SHOW_SETTINGS") {
                showSettingsPanel = true
            }
        }
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Backup", color = EmeraldNeon) },
            text = { Text("Backup database sekarang?", color = White) },
            containerColor = SurfaceDark,
            confirmButton = {
                TextButton(onClick = { 
                    showBackupDialog = false
                    viewModel.setNoxMessage("Database berhasil di-backup.")
                }) {
                    Text("Ya", color = EmeraldNeon)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Batal", color = White)
                }
            }
        )
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore", color = EmeraldNeon) },
            text = { Text("Pilih file backup.", color = White) },
            containerColor = SurfaceDark,
            confirmButton = {
                TextButton(onClick = { 
                    showRestoreDialog = false
                    viewModel.setNoxMessage("Database berhasil di-restore.")
                }) {
                    Text("Tutup", color = EmeraldNeon)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.type == PointerEventType.Press || 
                                event.type == PointerEventType.Scroll) {
                                viewModel.registerInteraction()
                            }
                        }
                    }
                }
        ) {
            // Particle Effect
            AnimatedVisibility(
                visible = showParticleEffect,
                enter = fadeIn(tween(500)),
                exit = fadeOut(tween(1000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(EmeraldNeon.copy(alpha = 0.1f))
                )
            }

            // Top Left Clock
            val parts = currentTime.split("\n")
            if (parts.size == 3) {
                Column(modifier = Modifier.padding(top = 32.dp, start = 24.dp)) {
                    Text(
                        text = parts[0].uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = White.copy(alpha = 0.9f),
                            letterSpacing = 0.2.em,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = EmeraldNeon.copy(alpha = 0.3f),
                                blurRadius = 4f
                            )
                        )
                    )
                    Text(
                        text = parts[1].uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = White.copy(alpha = 0.9f),
                            letterSpacing = 0.2.em,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = EmeraldNeon.copy(alpha = 0.3f),
                                blurRadius = 4f
                            )
                        )
                    )
                    Text(
                        text = parts[2],
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = EmeraldNeon.copy(alpha = 0.5f),
                                blurRadius = 8f
                            )
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = White,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = EmeraldNeon,
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier.padding(top = 32.dp, start = 24.dp)
                )
            }

            // Center: Nox
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = noxQuote != null,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 },
                    exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .background(
                                color = SurfaceDark.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, EmeraldNeon.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = noxQuote ?: "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = EmeraldNeon,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = EmeraldNeon.copy(alpha = 0.5f),
                                    blurRadius = 6f
                                )
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    NoxFace(expression = noxExpression)
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nox",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            fontSize = 24.sp,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = EmeraldNeon.copy(alpha = 0.4f),
                                blurRadius = 5f
                            )
                        )
                    )
                    Text(
                        text = "Tabungku",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = EmeraldNeon,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp,
                            fontSize = 24.sp,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = EmeraldNeon.copy(alpha = 0.8f),
                                blurRadius = 12f
                            )
                        )
                    )
                }
                Text(
                    text = noxMessage.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmeraldNeon.copy(alpha = 0.6f),
                        letterSpacing = 0.2.em,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp)
                )
            }

            // Bottom Command Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(com.example.ui.theme.SurfaceLight, RoundedCornerShape(16.dp))
                            .border(1.dp, com.example.ui.theme.BorderLight, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showBottomSheet = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = EmeraldNeon
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(com.example.ui.theme.SurfaceDark, RoundedCornerShape(24.dp))
                        .border(1.dp, com.example.ui.theme.BorderDark, RoundedCornerShape(24.dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(EmeraldNeon, RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)))
                    
                    BasicTextField(
                        value = commandText,
                        onValueChange = { 
                            commandText = it
                            viewModel.registerInteraction()
                        },
                        textStyle = TextStyle(color = White, fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        cursorBrush = SolidColor(EmeraldNeon),
                        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (commandText.isNotBlank()) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.processCommand(commandText)
                                    commandText = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp, end = 16.dp)
                            .focusRequester(focusRequester),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (commandText.isEmpty()) {
                                    Text("Ketik perintah...", color = com.example.ui.theme.GrayText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    innerTextField()
                                }
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(44.dp)
                            .shadow(15.dp, RoundedCornerShape(16.dp), spotColor = EmeraldNeon, ambientColor = EmeraldNeon)
                            .background(EmeraldNeon, RoundedCornerShape(16.dp))
                            .clickable {
                                if (commandText.isNotBlank()) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.processCommand(commandText)
                                    commandText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = SurfaceDark,
            scrimColor = Color.Black.copy(alpha = 0.8f)
        ) {
            CommandList { prefix, prompt ->
                showBottomSheet = false
                viewModel.registerInteraction()
                if (prefix == "DIALOG_BACKUP") {
                    showBackupDialog = true
                } else if (prefix == "DIALOG_RESTORE") {
                    showRestoreDialog = true
                } else if (prefix == "SHOW_HISTORY") {
                    showHistoryPanel = true
                } else if (prefix == "SHOW_SETTINGS") {
                    showSettingsPanel = true
                } else if (prefix.isNotEmpty()) {
                    commandText = prefix
                    viewModel.setNoxMessage(prompt)
                    focusRequester.requestFocus()
                } else {
                    viewModel.setNoxMessage(prompt)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showHistoryPanel,
        enter = fadeIn(tween(300)) + androidx.compose.animation.scaleIn(tween(300), initialScale = 0.9f),
        exit = fadeOut(tween(300)) + androidx.compose.animation.slideOutVertically(tween(300)) { it / 2 }
    ) {
        HistoryPanel(transactions = transactions, onClose = { showHistoryPanel = false })
    }

    AnimatedVisibility(
        visible = showSettingsPanel,
        enter = fadeIn(tween(250)) + androidx.compose.animation.scaleIn(tween(250), initialScale = 0.95f),
        exit = fadeOut(tween(250)) + androidx.compose.animation.slideOutVertically(tween(250)) { it / 2 }
    ) {
        SettingsPanel(
            settings = settings,
            onUpdateSettings = { viewModel.updateSettings(it) },
            onBackup = { showBackupDialog = true },
            onRestore = { showRestoreDialog = true },
            onExport = { viewModel.setNoxMessage("Fitur export akan segera hadir.") },
            onClearHistory = { viewModel.clearHistory() },
            onClose = { showSettingsPanel = false }
        )
    }
}

@Composable
fun CommandList(onCommandSelected: (String, String) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val commands = listOf(
        "Tambah Pemasukan", "Tambah Pengeluaran", "Tambah Tabungan", "Ambil Tabungan",
        "Riwayat", "Statistik", "Target Tabungan", "Cari Transaksi", "Pengaturan",
        "Backup", "Restore", "Bantuan"
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(commands) { command ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        when(command) {
                            "Tambah Pemasukan" -> onCommandSelected("pemasukan ", "Masukkan nominal pemasukan.\nContoh: pemasukan 100000")
                            "Tambah Pengeluaran" -> onCommandSelected("pengeluaran ", "Masukkan nominal dan keterangan.\nContoh: pengeluaran 25000 makan")
                            "Tambah Tabungan" -> onCommandSelected("tabungan ", "Masukkan nominal tabungan.\nContoh: tabungan 50000")
                            "Ambil Tabungan" -> onCommandSelected("ambil ", "Masukkan nominal yang ingin diambil.\nContoh: ambil 10000")
                            "Riwayat" -> onCommandSelected("SHOW_HISTORY", "")
                            "Pengaturan" -> onCommandSelected("SHOW_SETTINGS", "")
                            "Backup" -> onCommandSelected("DIALOG_BACKUP", "")
                            "Restore" -> onCommandSelected("DIALOG_RESTORE", "")
                            else -> onCommandSelected("", "Fitur $command akan segera hadir.")
                        }
                    }
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(8.dp).background(EmeraldNeon, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = command,
                    style = MaterialTheme.typography.bodyLarge.copy(color = White, fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}
