package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.RedNeon
import com.example.ui.theme.White
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPanel(
    transactions: List<Transaction>,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    
    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }
    
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }
    
    // Split data
    val incomeList = transactions.filter { it.type == "Pemasukan" || it.type == "Tabungan" }.sortedByDescending { it.timestamp }
    val expenseList = transactions.filter { it.type == "Pengeluaran" || it.type == "Ambil Tabungan" }.sortedByDescending { it.timestamp }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {
                // background tap to close? Let's leave it as is.
            },
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
                    text = "RIWAYAT TRANSAKSI",
                    color = EmeraldNeon,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = White)
                }
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = EmeraldNeon,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = EmeraldNeon
                    )
                },
                divider = { HorizontalDivider(color = Color.DarkGray) }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("📈 PEMASUKAN", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("📉 PENGELUARAN", fontWeight = FontWeight.Bold) }
                )
            }
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val list = if (page == 0) incomeList else expenseList
                val icon = if (page == 0) "💰" else "💸"
                val neonColor = if (page == 0) EmeraldNeon else RedNeon
                
                if (list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum ada riwayat transaksi.",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(list) { transaction ->
                            val desc = transaction.description.replace(Regex("^(?i)(pemasukan|pengeluaran|tabungan|ambil)\\s+\\d+\\s*"), "")
                            val displayDesc = if (desc.isNotBlank()) desc else transaction.type
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(icon, fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = formatRupiah.format(transaction.amount).replace("Rp", "Rp "),
                                        color = neonColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Kategori : ${displayDesc.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
                                        color = White,
                                        fontSize = 14.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = dateFormat.format(Date(transaction.timestamp)),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = timeFormat.format(Date(transaction.timestamp)),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
