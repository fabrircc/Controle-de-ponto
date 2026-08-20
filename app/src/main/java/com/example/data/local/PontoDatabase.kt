package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(
    entities = [Funcionario::class, RegistroPonto::class],
    version = 1,
    exportSchema = false
)
abstract class PontoDatabase : RoomDatabase() {

    abstract fun pontoDao(): PontoDao

    companion object {
        @Volatile
        private var INSTANCE: PontoDatabase? = null

        fun getDatabase(context: Context): PontoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PontoDatabase::class.java,
                    "ponto_eletronico.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
