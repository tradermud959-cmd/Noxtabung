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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class NoxExpression {
    Normal, Happy, Sad, Angry, Thinking, Surprised, Sleepy, Sleep, Yawning, Proud
}

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    init {
        startClock()
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
                    setExpression(NoxExpression.Sad, 3000)
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
                        if (_noxMessage.value == "SYSTEM ONLINE") _noxMessage.value = "Zzz..."
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

    fun processCommand(command: String) {
        val lowerCommand = command.lowercase(Locale.getDefault())
        viewModelScope.launch {
            try {
                // simple NLP
                val amount = extractAmount(lowerCommand)
                if (lowerCommand.contains("menabung") || lowerCommand.contains("nabung")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Tabungan", amount = amount, description = command))
                        setExpression(NoxExpression.Happy, 3000)
                        triggerParticleEffect()
                    }
                } else if (lowerCommand.contains("ambil tabungan") || lowerCommand.contains("mengambil")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Ambil Tabungan", amount = amount, description = command))
                        setExpression(NoxExpression.Angry, 2000)
                    }
                } else if (lowerCommand.contains("pemasukan")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Pemasukan", amount = amount, description = command))
                        setExpression(NoxExpression.Happy, 2000)
                    }
                } else if (lowerCommand.contains("pengeluaran") || lowerCommand.contains("beli") || lowerCommand.contains("bayar")) {
                    if (amount > 0) {
                        repository.insertTransaction(Transaction(type = "Pengeluaran", amount = amount, description = command))
                        setExpression(NoxExpression.Thinking, 2000)
                    }
                } else {
                    setExpression(NoxExpression.Surprised, 2000)
                }
            } catch (e: Exception) {
                setExpression(NoxExpression.Sad, 2000)
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
