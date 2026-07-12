package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NoxExpression {
    Normal, Happy, Sad, Angry, Thinking, Surprised, Sleepy, Sleep, Yawning, Proud,
    IncomeSuccess, ExpenseSuccess, TargetAchieved, Warning, Danger
}

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings: StateFlow<com.example.data.Settings?> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateSettings(newSettings: com.example.data.Settings) {
        viewModelScope.launch {
            repository.insertSettings(newSettings)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearTransactions()
        }
    }

    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _noxExpression = MutableStateFlow(NoxExpression.Normal)
    val noxExpression: StateFlow<NoxExpression> = _noxExpression.asStateFlow()
    
    private val _noxMessage = MutableStateFlow("SYSTEM ONLINE")
    val noxMessage: StateFlow<String> = _noxMessage.asStateFlow()

    private var lastInteractionTime = System.currentTimeMillis()
    
    fun registerInteraction() {
        lastInteractionTime = System.currentTimeMillis()
        if (_noxExpression.value == NoxExpression.Sleep || _noxExpression.value == NoxExpression.Sleepy || _noxExpression.value == NoxExpression.Yawning) {
            _noxExpression.value = NoxExpression.Normal
            _noxMessage.value = "SYSTEM ONLINE"
        }
    }

    fun setNoxMessage(message: String) {
        _noxMessage.value = message
    }

    private val _showParticleEffect = MutableStateFlow(false)
    val showParticleEffect: StateFlow<Boolean> = _showParticleEffect.asStateFlow()

    private val _totalBalance = MutableStateFlow(0L)
    val totalBalance: StateFlow<Long> = _totalBalance.asStateFlow()

    private val _uiAction = kotlinx.coroutines.flow.MutableSharedFlow<String>()
    val uiAction = _uiAction.asSharedFlow()

    private val quotes = listOf(
        "SCANNING FINANCIAL SECTOR...",
        "STAY CYBER SECURE.",
        "UPGRADING SAVINGS PROTOCOL.",
        "ENCRYPTING YOUR WEALTH.",
        "KEEP GRINDING IN THE MATRIX.",
        "CREDITS OPTIMIZED.",
        "SYSTEM RUNNING AT 100%.",
        "ROUTING MORE FUNDS...",
        "SAVINGS MODULE ACTIVE.",
        "FUNDS SECURED.",
        "ANALYZING MARKET TRENDS...",
        "STAY FROSTY.",
        "AWAITING INPUT."
    )

    private val _noxQuote = MutableStateFlow<String?>(null)
    val noxQuote: StateFlow<String?> = _noxQuote.asStateFlow()

    init {
        startClock()
        startQuotes()
        viewModelScope.launch {
            transactions.collect { list ->
                val balance = list.sumOf { 
                    when(it.type) {
                        "Pemasukan", "Tabungan" -> it.amount
                        "Pengeluaran", "Ambil Tabungan" -> -it.amount
                        else -> 0L
                    }
                }
                _totalBalance.value = balance
                
                if (list.isNotEmpty() && balance < 50000 && balance > 0) { // arbitrary threshold for almost empty
                    setExpression(NoxExpression.Warning, 3000)
                }
            }
        }
    }

    private fun startClock() {
        viewModelScope.launch {
            val formatter = SimpleDateFormat("EEEE\ndd MMMM yyyy\nHH:mm:ss", Locale("id", "ID"))
            while (true) {
                _currentTime.value = formatter.format(Date())
                
                val idleTime = (System.currentTimeMillis() - lastInteractionTime) / 1000
                if (idleTime >= 20) {
                    if (_noxExpression.value != NoxExpression.Sleep) {
                        _noxExpression.value = NoxExpression.Sleep
                        if (_noxMessage.value == "SYSTEM ONLINE") _noxMessage.value = "tidur dulu bro"
                    }
                } else if (idleTime in 18..19) {
                    if (_noxExpression.value != NoxExpression.Yawning) {
                        _noxExpression.value = NoxExpression.Yawning
                    }
                } else if (idleTime in 15..17) {
                    if (_noxExpression.value == NoxExpression.Normal) {
                        _noxExpression.value = NoxExpression.Sleepy
                    }
                }
                
                delay(1000)
            }
        }
    }

    private fun startQuotes() {
        viewModelScope.launch {
            while (true) {
                delay((10000L..25000L).random())
                if (_noxExpression.value == NoxExpression.Normal || _noxExpression.value == NoxExpression.Thinking) {
                    _noxQuote.value = quotes.random()
                    delay(5000L)
                    _noxQuote.value = null
                }
            }
        }
    }

    fun processCommand(command: String) {
        val lowerCommand = command.lowercase(Locale.getDefault())
        if (lowerCommand.contains("riwayat") || lowerCommand.contains("history")) {
            viewModelScope.launch { _uiAction.emit("SHOW_HISTORY") }
            return
        }
        if (lowerCommand.contains("pengaturan") || lowerCommand.contains("setting")) {
            viewModelScope.launch { _uiAction.emit("SHOW_SETTINGS") }
            return
        }
        viewModelScope.launch {
            try {
                // simple NLP
                val amount = extractAmount(lowerCommand)
                if (lowerCommand.contains("menabung") || lowerCommand.contains("nabung")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Tabungan", amount = amount, description = command))
                        setExpression(NoxExpression.IncomeSuccess, 1200)
                        triggerParticleEffect()
                    }
                } else if (lowerCommand.contains("ambil tabungan") || lowerCommand.contains("mengambil")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Ambil Tabungan", amount = amount, description = command))
                        setExpression(NoxExpression.ExpenseSuccess, 1000)
                    }
                } else if (lowerCommand.contains("pemasukan")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Pemasukan", amount = amount, description = command))
                        setExpression(NoxExpression.IncomeSuccess, 1200)
                    }
                } else if (lowerCommand.contains("pengeluaran") || lowerCommand.contains("beli") || lowerCommand.contains("bayar")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Pengeluaran", amount = amount, description = command))
                        setExpression(NoxExpression.ExpenseSuccess, 1000)
                    }
                } else if (lowerCommand.contains("target")) {
                    setExpression(NoxExpression.TargetAchieved, 2000)
                } else if (lowerCommand.contains("error") || lowerCommand.contains("gagal") || lowerCommand.contains("kurang")) {
                    setExpression(NoxExpression.Warning, 2000)
                } else if (lowerCommand.contains("bahaya") || lowerCommand.contains("hapus semua")) {
                    setExpression(NoxExpression.Danger, 3000)
                } else {
                    setExpression(NoxExpression.Thinking, 2000)
                }
            } catch (e: Exception) {
                setExpression(NoxExpression.Warning, 2000)
            }
        }
    }

    private fun extractAmount(text: String): Long {
        val regex = Regex("\\d+")
        val match = regex.find(text)
        return match?.value?.toLongOrNull() ?: 0L
    }

    private fun triggerParticleEffect() {
        viewModelScope.launch {
            _showParticleEffect.value = true
            delay(2000)
            _showParticleEffect.value = false
        }
    }

    private fun setExpression(expression: NoxExpression, duration: Long) {
        viewModelScope.launch {
            _noxExpression.value = expression
            delay(duration)
            _noxExpression.value = NoxExpression.Normal
        }
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
