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

        fun getDatabase(context: Context, scope: CoroutineScope): PontoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PontoDatabase::class.java,
                    "ponto_eletronico.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database.pontoDao())
                    }
                }
            }

            private suspend fun prepopulateDatabase(dao: PontoDao) {
                // Initial test employees
                val f1 = Funcionario(
                    id = 1,
                    matricula = "1001",
                    nome = "Ana Silva",
                    cargo = "Analista de Operações",
                    departamento = "Operacional",
                    ativo = true,
                    dataCadastro = "2026-01-15 08:00:00"
                )
                val f2 = Funcionario(
                    id = 2,
                    matricula = "1002",
                    nome = "Carlos Mendes",
                    cargo = "Desenvolvedor de Sistemas",
                    departamento = "Tecnologia",
                    ativo = true,
                    dataCadastro = "2026-02-01 09:00:00"
                )
                val f3 = Funcionario(
                    id = 3,
                    matricula = "1003",
                    nome = "Roberto Gomes",
                    cargo = "Gerente de Produção",
                    departamento = "Produção",
                    ativo = true,
                    dataCadastro = "2026-02-10 08:30:00"
                )
                val f4 = Funcionario(
                    id = 4,
                    matricula = "1234",
                    nome = "Mariana Costa",
                    cargo = "Assistente Administrativo",
                    departamento = "Recursos Humanos",
                    ativo = true,
                    dataCadastro = "2026-03-01 08:00:00"
                )

                dao.insertFuncionario(f1)
                dao.insertFuncionario(f2)
                dao.insertFuncionario(f3)
                dao.insertFuncionario(f4)

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val now = LocalDateTime.now()
                val todayStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                // Sample punches
                dao.insertRegistro(
                    RegistroPonto(
                        funcionarioId = 1,
                        funcionarioMatricula = "1001",
                        funcionarioNome = "Ana Silva",
                        tipoBatida = "ENTRADA",
                        dataHoraBatida = "$todayStr 08:00:00",
                        dataHoraSistema = "$todayStr 08:00:05",
                        isRetroativo = false,
                        justificativa = null
                    )
                )
                dao.insertRegistro(
                    RegistroPonto(
                        funcionarioId = 1,
                        funcionarioMatricula = "1001",
                        funcionarioNome = "Ana Silva",
                        tipoBatida = "SAIDA",
                        dataHoraBatida = "$todayStr 12:00:00",
                        dataHoraSistema = "$todayStr 12:00:10",
                        isRetroativo = false,
                        justificativa = null
                    )
                )
                dao.insertRegistro(
                    RegistroPonto(
                        funcionarioId = 2,
                        funcionarioMatricula = "1002",
                        funcionarioNome = "Carlos Mendes",
                        tipoBatida = "ENTRADA",
                        dataHoraBatida = "$todayStr 08:00:00",
                        dataHoraSistema = "$todayStr 10:15:00",
                        isRetroativo = true,
                        justificativa = "Esquecimento"
                    )
                )
            }
        }
    }
}
