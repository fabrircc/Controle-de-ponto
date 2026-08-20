package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.Funcionario
import com.example.data.local.PontoDatabase
import com.example.data.local.RegistroPonto
import com.example.data.repository.PontoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PontoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PontoRepository

    val allFuncionarios: StateFlow<List<Funcionario>>
    val activeFuncionarios: StateFlow<List<Funcionario>>
    val allRegistros: StateFlow<List<RegistroPonto>>
    val recentRegistros: StateFlow<List<RegistroPonto>>

    private val _matriculaInput = MutableStateFlow("")
    val matriculaInput: StateFlow<String> = _matriculaInput.asStateFlow()

    private val _matchedFuncionario = MutableStateFlow<Funcionario?>(null)
    val matchedFuncionario: StateFlow<Funcionario?> = _matchedFuncionario.asStateFlow()

    private val _lastPunchSuccess = MutableStateFlow<RegistroPonto?>(null)
    val lastPunchSuccess: StateFlow<RegistroPonto?> = _lastPunchSuccess.asStateFlow()

    private val _userFeedback = MutableSharedFlow<String>()
    val userFeedback: SharedFlow<String> = _userFeedback.asSharedFlow()

    private val _showRetroactiveDialog = MutableStateFlow(false)
    val showRetroactiveDialog: StateFlow<Boolean> = _showRetroactiveDialog.asStateFlow()

    private val _showAdminPasswordDialog = MutableStateFlow(false)
    val showAdminPasswordDialog: StateFlow<Boolean> = _showAdminPasswordDialog.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.TOTEM)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _adminPin = MutableStateFlow("1234")
    val adminPin: StateFlow<String> = _adminPin.asStateFlow()

    enum class AppScreen {
        TOTEM,
        ADMIN
    }

    init {
        val database = PontoDatabase.getDatabase(application)
        repository = PontoRepository(database.pontoDao())

        allFuncionarios = repository.allFuncionarios.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeFuncionarios = repository.activeFuncionarios.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allRegistros = repository.allRegistros.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        recentRegistros = repository.recentRegistros.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.checkAndSeedIfEmpty()
        }
    }

    fun onDigitInput(digit: String) {
        if (_matriculaInput.value.length < 10) {
            _matriculaInput.value += digit
            checkMatchingEmployee(_matriculaInput.value)
        }
    }

    fun onBackspaceInput() {
        if (_matriculaInput.value.isNotEmpty()) {
            _matriculaInput.value = _matriculaInput.value.dropLast(1)
            checkMatchingEmployee(_matriculaInput.value)
        }
    }

    fun onClearInput() {
        _matriculaInput.value = ""
        _matchedFuncionario.value = null
    }

    fun setMatriculaDirect(matricula: String) {
        _matriculaInput.value = matricula
        checkMatchingEmployee(matricula)
    }

    private fun checkMatchingEmployee(matricula: String) {
        val trimmed = matricula.trim()
        if (trimmed.isEmpty()) {
            _matchedFuncionario.value = null
            return
        }
        _matchedFuncionario.value = allFuncionarios.value.find { it.matricula == trimmed }
    }

    fun baterPonto(tipoBatida: String) {
        val matricula = _matriculaInput.value.trim()
        if (matricula.isEmpty()) {
            emitFeedback("Por favor, digite sua matrícula antes de bater o ponto.")
            return
        }

        viewModelScope.launch {
            val result = repository.registrarPonto(
                matricula = matricula,
                tipoBatida = tipoBatida,
                isRetroativo = false
            )

            result.fold(
                onSuccess = { registro ->
                    _lastPunchSuccess.value = registro
                    _matriculaInput.value = ""
                    _matchedFuncionario.value = null
                },
                onFailure = { err ->
                    emitFeedback("Erro: ${err.message ?: "Falha ao registrar ponto."}")
                }
            )
        }
    }

    fun registrarPontoRetroativo(
        matricula: String,
        tipoBatida: String,
        dataHora: String,
        justificativa: String
    ) {
        viewModelScope.launch {
            val result = repository.registrarPonto(
                matricula = matricula,
                tipoBatida = tipoBatida,
                customDataHora = dataHora,
                isRetroativo = true,
                justificativa = justificativa
            )

            result.fold(
                onSuccess = { registro ->
                    _lastPunchSuccess.value = registro
                    _showRetroactiveDialog.value = false
                    _matriculaInput.value = ""
                    _matchedFuncionario.value = null
                },
                onFailure = { err ->
                    emitFeedback("Erro no registro retroativo: ${err.message}")
                }
            )
        }
    }

    fun cadastrarNovoFuncionario(
        matricula: String,
        nome: String,
        cargo: String,
        departamento: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.cadastrarFuncionario(matricula, nome, cargo, departamento)
            result.fold(
                onSuccess = {
                    emitFeedback("Colaborador ${it.nome} cadastrado com sucesso!")
                    onSuccess()
                },
                onFailure = { err ->
                    emitFeedback("Erro ao cadastrar: ${err.message}")
                }
            )
        }
    }

    fun alternarStatusFuncionario(funcionario: Funcionario) {
        viewModelScope.launch {
            val updated = funcionario.copy(ativo = !funcionario.ativo)
            repository.updateFuncionario(updated)
            emitFeedback("Colaborador ${funcionario.nome} agora está ${if (updated.ativo) "Ativo" else "Inativo"}.")
        }
    }

    fun excluirFuncionario(funcionario: Funcionario) {
        viewModelScope.launch {
            repository.deleteFuncionario(funcionario)
            emitFeedback("Colaborador ${funcionario.nome} e seus registros foram removidos.")
        }
    }

    fun excluirRegistro(registro: RegistroPonto) {
        viewModelScope.launch {
            repository.deleteRegistro(registro)
            emitFeedback("Registro de ponto excluído.")
        }
    }

    fun dismissPunchSuccess() {
        _lastPunchSuccess.value = null
    }

    fun openRetroactiveDialog() {
        _showRetroactiveDialog.value = true
    }

    fun closeRetroactiveDialog() {
        _showRetroactiveDialog.value = false
    }

    fun openAdminPasswordDialog() {
        _showAdminPasswordDialog.value = true
    }

    fun closeAdminPasswordDialog() {
        _showAdminPasswordDialog.value = false
    }

    fun enterAdminMode() {
        _showAdminPasswordDialog.value = false
        _currentScreen.value = AppScreen.ADMIN
    }

    fun exitAdminMode() {
        _currentScreen.value = AppScreen.TOTEM
    }

    fun updateAdminPin(newPin: String) {
        if (newPin.length >= 4) {
            _adminPin.value = newPin
            emitFeedback("PIN de Administrador atualizado com sucesso!")
        } else {
            emitFeedback("O PIN de Administrador deve conter pelo menos 4 dígitos.")
        }
    }

    fun generateCsvExport(registros: List<RegistroPonto>): String {
        val sb = StringBuilder()
        sb.append("ID;Matricula;Colaborador;Tipo;Data_Hora_Folha;Data_Hora_Auditoria;Lancamento_Retroativo;Justificativa\n")
        for (r in registros) {
            val retroStr = if (r.isRetroativo) "SIM" else "NAO"
            val justStr = r.justificativa?.replace(";", ",") ?: ""
            sb.append("${r.id};${r.funcionarioMatricula};\"${r.funcionarioNome}\";${r.tipoBatida};${r.dataHoraBatida};${r.dataHoraSistema};$retroStr;\"$justStr\"\n")
        }
        return sb.toString()
    }

    private fun emitFeedback(message: String) {
        viewModelScope.launch {
            _userFeedback.emit(message)
        }
    }
}
