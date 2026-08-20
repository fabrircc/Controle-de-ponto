package com.example.data.repository

import com.example.data.local.Funcionario
import com.example.data.local.PontoDao
import com.example.data.local.RegistroPonto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PontoRepository(private val pontoDao: PontoDao) {

    val allFuncionarios: Flow<List<Funcionario>> = pontoDao.getAllFuncionarios()
    val activeFuncionarios: Flow<List<Funcionario>> = pontoDao.getAtivosFuncionarios()
    val allRegistros: Flow<List<RegistroPonto>> = pontoDao.getAllRegistros()
    val recentRegistros: Flow<List<RegistroPonto>> = pontoDao.getRecentRegistros(20)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    suspend fun findFuncionarioByMatricula(matricula: String): Funcionario? {
        return withContext(Dispatchers.IO) {
            pontoDao.getFuncionarioByMatricula(matricula.trim())
        }
    }

    suspend fun registrarPonto(
        matricula: String,
        tipoBatida: String,
        customDataHora: String? = null,
        isRetroativo: Boolean = false,
        justificativa: String? = null
    ): Result<RegistroPonto> {
        return withContext(Dispatchers.IO) {
            val funcionario = pontoDao.getFuncionarioByMatricula(matricula.trim())
                ?: return@withContext Result.failure(Exception("Matrícula '$matricula' não encontrada no sistema!"))

            if (!funcionario.ativo) {
                return@withContext Result.failure(Exception("Colaborador ${funcionario.nome} está inativo."))
            }

            val agora = LocalDateTime.now().format(formatter)
            val dataHoraFolha = if (isRetroativo && !customDataHora.isNullOrBlank()) {
                customDataHora.trim()
            } else {
                agora
            }

            val registro = RegistroPonto(
                funcionarioId = funcionario.id,
                funcionarioMatricula = funcionario.matricula,
                funcionarioNome = funcionario.nome,
                tipoBatida = tipoBatida.uppercase().trim(),
                dataHoraBatida = dataHoraFolha,
                dataHoraSistema = agora,
                isRetroativo = isRetroativo,
                justificativa = if (isRetroativo) justificativa?.trim() else null
            )

            val id = pontoDao.insertRegistro(registro)
            Result.success(registro.copy(id = id))
        }
    }

    suspend fun cadastrarFuncionario(
        matricula: String,
        nome: String,
        cargo: String = "Colaborador",
        departamento: String = "Geral"
    ): Result<Funcionario> {
        return withContext(Dispatchers.IO) {
            val trimmedMatricula = matricula.trim()
            if (trimmedMatricula.isEmpty() || nome.trim().isEmpty()) {
                return@withContext Result.failure(Exception("Matrícula e Nome são obrigatórios."))
            }

            val existing = pontoDao.getFuncionarioByMatricula(trimmedMatricula)
            if (existing != null) {
                return@withContext Result.failure(Exception("Já existe um colaborador com a matrícula '$trimmedMatricula' (${existing.nome})."))
            }

            val agora = LocalDateTime.now().format(formatter)
            val funcionario = Funcionario(
                matricula = trimmedMatricula,
                nome = nome.trim(),
                cargo = cargo.ifBlank { "Colaborador" }.trim(),
                departamento = departamento.ifBlank { "Geral" }.trim(),
                ativo = true,
                dataCadastro = agora
            )
            val id = pontoDao.insertFuncionario(funcionario)
            Result.success(funcionario.copy(id = id))
        }
    }

    suspend fun updateFuncionario(funcionario: Funcionario) {
        withContext(Dispatchers.IO) {
            pontoDao.updateFuncionario(funcionario)
        }
    }

    suspend fun deleteFuncionario(funcionario: Funcionario) {
        withContext(Dispatchers.IO) {
            pontoDao.deleteFuncionario(funcionario)
        }
    }

    suspend fun deleteRegistro(registro: RegistroPonto) {
        withContext(Dispatchers.IO) {
            pontoDao.deleteRegistro(registro)
        }
    }

    suspend fun checkAndSeedIfEmpty() {
        withContext(Dispatchers.IO) {
            val count = pontoDao.countFuncionarios()
            if (count == 0) {
                val f1 = Funcionario(matricula = "1001", nome = "Ana Silva", cargo = "Analista de Operações", departamento = "Operacional", ativo = true, dataCadastro = "2026-01-15 08:00:00")
                val f2 = Funcionario(matricula = "1002", nome = "Carlos Mendes", cargo = "Desenvolvedor de Sistemas", departamento = "Tecnologia", ativo = true, dataCadastro = "2026-02-01 09:00:00")
                val f3 = Funcionario(matricula = "1003", nome = "Roberto Gomes", cargo = "Gerente de Produção", departamento = "Produção", ativo = true, dataCadastro = "2026-02-10 08:30:00")
                val f4 = Funcionario(matricula = "1234", nome = "Mariana Costa", cargo = "Assistente Administrativo", departamento = "Recursos Humanos", ativo = true, dataCadastro = "2026-03-01 08:00:00")

                pontoDao.insertFuncionario(f1)
                pontoDao.insertFuncionario(f2)
                pontoDao.insertFuncionario(f3)
                pontoDao.insertFuncionario(f4)
            }
        }
    }
}
