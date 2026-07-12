package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Pemasukan", "Pengeluaran", "Tabungan", "Ambil Tabungan"
    val amount: Long,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "targets")
data class TargetSaving(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Long,
    val currentAmount: Long = 0,
    val deadline: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val darkTheme: Boolean = true,
    val pinEnabled: Boolean = false,
    val pinHash: String? = null,
    val neonGlow: Boolean = true,
    val noxEyeAnimation: Boolean = true,
    val idleAnimation: Boolean = true,
    val noxVolume: Float = 0.8f,
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true,
    val fingerprintLogin: Boolean = false
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Int)
    
    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface TargetSavingDao {
    @Query("SELECT * FROM targets ORDER BY timestamp DESC")
    fun getAllTargets(): Flow<List<TargetSaving>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: TargetSaving)
    
    @Query("UPDATE targets SET currentAmount = :amount WHERE id = :id")
    suspend fun updateTargetAmount(id: Int, amount: Long)

    @Query("DELETE FROM targets WHERE id = :id")
    suspend fun deleteTarget(id: Int)
    
    @Query("DELETE FROM targets")
    suspend fun clearAll()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)
}

@Database(entities = [Transaction::class, TargetSaving::class, Settings::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun targetSavingDao(): TargetSavingDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nox_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class AppRepository(
    private val transactionDao: TransactionDao,
    private val targetSavingDao: TargetSavingDao,
    private val settingsDao: SettingsDao
) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allTargets: Flow<List<TargetSaving>> = targetSavingDao.getAllTargets()
    val settings: Flow<Settings?> = settingsDao.getSettings()

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(id: Int) = transactionDao.deleteTransaction(id)
    suspend fun clearTransactions() = transactionDao.clearAll()

    suspend fun insertTarget(target: TargetSaving) = targetSavingDao.insertTarget(target)
    suspend fun updateTargetAmount(id: Int, amount: Long) = targetSavingDao.updateTargetAmount(id, amount)
    suspend fun deleteTarget(id: Int) = targetSavingDao.deleteTarget(id)
    suspend fun clearTargets() = targetSavingDao.clearAll()

    suspend fun insertSettings(settings: Settings) = settingsDao.insertSettings(settings)
}
