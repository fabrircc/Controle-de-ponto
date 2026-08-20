package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PontoDao {

    // --- Funcionários ---

    @Query("SELECT * FROM funcionarios ORDER BY nome ASC")
    fun getAllFuncionarios(): Flow<List<Funcionario>>

    @Query("SELECT * FROM funcionarios WHERE ativo = 1 ORDER BY nome ASC")
    fun getAtivosFuncionarios(): Flow<List<Funcionario>>

    @Query("SELECT * FROM funcionarios WHERE matricula = :matricula LIMIT 1")
    suspend fun getFuncionarioByMatricula(matricula: String): Funcionario?

    @Query("SELECT * FROM funcionarios WHERE id = :id LIMIT 1")
    suspend fun getFuncionarioById(id: Long): Funcionario?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFuncionario(funcionario: Funcionario): Long

    @Update
    suspend fun updateFuncionario(funcionario: Funcionario)

    @Delete
    suspend fun deleteFuncionario(funcionario: Funcionario)

    @Query("SELECT COUNT(*) FROM funcionarios")
    suspend fun countFuncionarios(): Int

    // --- Registros de Ponto ---

    @Query("SELECT * FROM registros_ponto ORDER BY dataHoraBatida DESC")
    fun getAllRegistros(): Flow<List<RegistroPonto>>

    @Query("SELECT * FROM registros_ponto ORDER BY dataHoraSistema DESC LIMIT :limit")
    fun getRecentRegistros(limit: Int = 20): Flow<List<RegistroPonto>>

    @Query("SELECT * FROM registros_ponto WHERE funcionarioId = :funcionarioId ORDER BY dataHoraBatida DESC")
    fun getRegistrosByFuncionario(funcionarioId: Long): Flow<List<RegistroPonto>>

    @Query("SELECT * FROM registros_ponto WHERE dataHoraBatida LIKE :datePrefix || '%' ORDER BY dataHoraBatida DESC")
    fun getRegistrosByDatePrefix(datePrefix: String): Flow<List<RegistroPonto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: RegistroPonto): Long

    @Delete
    suspend fun deleteRegistro(registro: RegistroPonto)

    @Query("DELETE FROM registros_ponto WHERE id = :id")
    suspend fun deleteRegistroById(id: Long)
}
