package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.Funcionario
import com.example.data.local.RegistroPonto
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.EntradaGreen
import com.example.ui.theme.RetroAmber
import com.example.ui.theme.SaidaRed
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.PontoViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AdminScreen(
    viewModel: PontoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRegistros by viewModel.allRegistros.collectAsStateWithLifecycle()
    val allFuncionarios by viewModel.allFuncionarios.collectAsStateWithLifecycle()
    val adminPin by viewModel.adminPin.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Auditoria", "Colaboradores", "Relatórios & Export", "Totem & Kiosk")

    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.exitAdminMode() },
                    modifier = Modifier.testTag("admin_back_to_totem_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar ao Totem",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Painel Administrativo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gestão de Ponto, Funcionários & Auditoria",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }

            // Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate900,
                contentColor = AccentBlue,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AccentBlue,
                            height = 3.dp
                        )
                    }
                },
                divider = { HorizontalDivider(color = Slate800) }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (selectedTab == index) AccentBlue else Slate400
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> AuditoriaTab(registros = allRegistros, onDeleteRegistro = { viewModel.excluirRegistro(it) })
                    1 -> ColaboradoresTab(
                        funcionarios = allFuncionarios,
                        onToggleStatus = { viewModel.alternarStatusFuncionario(it) },
                        onDeleteFuncionario = { viewModel.excluirFuncionario(it) },
                        onOpenAddDialog = { showAddEmployeeDialog = true }
                    )
                    2 -> RelatoriosTab(
                        registros = allRegistros,
                        funcionarios = allFuncionarios,
                        onExportCsv = {
                            val csvData = viewModel.generateCsvExport(allRegistros)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_SUBJECT, "Relatório_Ponto_Eletrônico.csv")
                                putExtra(Intent.EXTRA_TEXT, csvData)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Exportar Relatório de Ponto"))
                        }
                    )
                    3 -> TotemKioskTab(
                        currentPin = adminPin,
                        onUpdatePin = { newPin -> viewModel.updateAdminPin(newPin) }
                    )
                }
            }
        }

        // Add Employee Dialog
        if (showAddEmployeeDialog) {
            AddEmployeeDialog(
                onDismiss = { showAddEmployeeDialog = false },
                onConfirm = { matricula, nome, cargo, dep ->
                    viewModel.cadastrarNovoFuncionario(matricula, nome, cargo, dep) {
                        showAddEmployeeDialog = false
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

// -------------------------------------------------------------
// TAB 1: AUDITORIA & REGISTROS
// -------------------------------------------------------------
@Composable
private fun AuditoriaTab(
    registros: List<RegistroPonto>,
    onDeleteRegistro: (RegistroPonto) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyRetroativo by remember { mutableStateOf(false) }

    val filteredRegistros = remember(registros, searchQuery, filterOnlyRetroativo) {
        registros.filter { r ->
            val matchSearch = searchQuery.isBlank() ||
                    r.funcionarioNome.contains(searchQuery, ignoreCase = true) ||
                    r.funcionarioMatricula.contains(searchQuery, ignoreCase = true)
            val matchRetro = !filterOnlyRetroativo || r.isRetroativo
            matchSearch && matchRetro
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Filter controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar colaborador ou matrícula...", color = Slate600, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("auditoria_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (filterOnlyRetroativo) RetroAmber.copy(alpha = 0.2f) else Slate900)
                    .border(1.dp, if (filterOnlyRetroativo) RetroAmber else Slate800, RoundedCornerShape(12.dp))
                    .clickable { filterOnlyRetroativo = !filterOnlyRetroativo }
                    .padding(horizontal = 12.dp, vertical = 14.dp)
                    .testTag("filter_retroactive_chip"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Apenas Retroativo",
                    color = if (filterOnlyRetroativo) RetroAmber else Slate400,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Total de registros encontrados: ${filteredRegistros.size}",
            style = MaterialTheme.typography.labelSmall,
            color = Slate400
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredRegistros.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Nenhum registro de ponto encontrado.",
                    color = Slate500,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRegistros, key = { it.id }) { reg ->
                    RegistroItemCard(registro = reg, onDelete = { onDeleteRegistro(reg) })
                }
            }
        }
    }
}

@Composable
private fun RegistroItemCard(
    registro: RegistroPonto,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val isEntrada = registro.tipoBatida == "ENTRADA"
    val accentColor = if (isEntrada) EntradaGreen else SaidaRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (registro.isRetroativo) RetroAmber.copy(alpha = 0.4f) else Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Name, Matricula, and Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = registro.funcionarioNome,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${registro.funcionarioMatricula})",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Type Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = registro.tipoBatida,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    if (registro.isRetroativo) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RetroAmber.copy(alpha = 0.15f))
                                .border(1.dp, RetroAmber.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RETROATIVO",
                                color = RetroAmber,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir registro",
                            tint = Slate500,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timestamps Breakdown (Folha vs Auditoria Real)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate850)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Horário da Folha:",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Text(
                        text = registro.dataHoraBatida,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Auditoria (Sistema):",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Text(
                        text = registro.dataHoraSistema,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (registro.isRetroativo) RetroAmber else Slate300
                    )
                }
            }

            if (registro.isRetroativo && !registro.justificativa.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📌 Justificativa: ${registro.justificativa}",
                    fontSize = 12.sp,
                    color = RetroAmber,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Registro de Ponto", color = Color.White) },
            text = { Text("Deseja realmente remover o registro de ${registro.funcionarioNome} (${registro.dataHoraBatida})?", color = Slate300) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaidaRed)
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = Slate300)
                }
            },
            containerColor = Slate900
        )
    }
}

// -------------------------------------------------------------
// TAB 2: COLABORADORES
// -------------------------------------------------------------
@Composable
private fun ColaboradoresTab(
    funcionarios: List<Funcionario>,
    onToggleStatus: (Funcionario) -> Unit,
    onDeleteFuncionario: (Funcionario) -> Unit,
    onOpenAddDialog: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Colaboradores Cadastrados (${funcionarios.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Button(
                    onClick = onOpenAddDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_employee_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Slate950,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Novo Colaborador",
                        fontWeight = FontWeight.Bold,
                        color = Slate950,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(funcionarios, key = { it.id }) { func ->
                    FuncionarioCard(
                        funcionario = func,
                        onToggle = { onToggleStatus(func) },
                        onDelete = { onDeleteFuncionario(func) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FuncionarioCard(
    funcionario: Funcionario,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (funcionario.ativo) AccentBlue.copy(alpha = 0.15f) else Slate800)
                        .border(1.dp, if (funcionario.ativo) AccentBlue.copy(alpha = 0.4f) else Slate700, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = funcionario.nome.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (funcionario.ativo) AccentBlue else Slate500,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = funcionario.nome,
                        fontWeight = FontWeight.Bold,
                        color = if (funcionario.ativo) Color.White else Slate500,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Matrícula / PIN: ${funcionario.matricula}",
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlue,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${funcionario.cargo} • ${funcionario.departamento}",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Active Switch
                Switch(
                    checked = funcionario.ativo,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EntradaGreen,
                        checkedTrackColor = EntradaGreen.copy(alpha = 0.3f),
                        uncheckedThumbColor = Slate500,
                        uncheckedTrackColor = Slate800
                    ),
                    modifier = Modifier.testTag("employee_active_switch_${funcionario.matricula}")
                )

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir colaborador",
                        tint = Slate500,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Excluir Colaborador", color = Color.White) },
            text = { Text("Deseja realmente remover ${funcionario.nome} e todo o seu histórico de batidas?", color = Slate300) },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaidaRed)
                ) {
                    Text("Excluir", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = Slate300)
                }
            },
            containerColor = Slate900
        )
    }
}

// -------------------------------------------------------------
// TAB 3: RELATÓRIOS & EXPORTAÇÃO
// -------------------------------------------------------------
@Composable
private fun RelatoriosTab(
    registros: List<RegistroPonto>,
    funcionarios: List<Funcionario>,
    onExportCsv: () -> Unit
) {
    val totalRegistros = registros.size
    val totalRetroativos = registros.count { it.isRetroativo }
    val totalNormais = totalRegistros - totalRetroativos
    val activeCount = funcionarios.count { it.ativo }

    val retroPercent = if (totalRegistros > 0) (totalRetroativos * 100) / totalRegistros else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Resumo Geral da Folha & Auditoria",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Metrics 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Total de Batidas",
                value = "$totalRegistros",
                subtitle = "$totalNormais no horário regular",
                accentColor = AccentBlue,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Colaboradores Ativos",
                value = "$activeCount",
                subtitle = "${funcionarios.size} cadastrados",
                accentColor = EntradaGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Batidas Retroativas",
                value = "$totalRetroativos",
                subtitle = "$retroPercent% do total de registros",
                accentColor = RetroAmber,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Status do Totem",
                value = "100% OK",
                subtitle = "SQLite Local Ativo",
                accentColor = EntradaGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Export Actions Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EntradaGreen.copy(alpha = 0.15f))
                            .border(1.dp, EntradaGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = EntradaGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Exportação para Folha de Pagamento",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Gera arquivo CSV compatível com Excel e Google Sheets",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onExportCsv,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("export_csv_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EntradaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compartilhar / Baixar Relatório (CSV)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Slate400,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500,
                fontSize = 11.sp
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 4: TOTEM & KIOSK CONFIG
// -------------------------------------------------------------
@Composable
private fun TotemKioskTab(
    currentPin: String,
    onUpdatePin: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Pinning Guide Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Modo Totem / Fixação de Tela (Kiosk)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Para transformar este tablet/celular em um Totem dedicado de Ponto, ative a Fixação de Tela no Android:\n\n" +
                            "1. Vá em Configurações do Android > Segurança > Fixação de Aplicativos (App Pinning).\n" +
                            "2. Ative a opção e marque 'Pedir PIN para desafixar'.\n" +
                            "3. Abra este app de Ponto, abra a tela de apps recentes e toque no ícone no topo para 'Fixar'.\n" +
                            "4. O tablet ficará travado exclusivamente nesta tela sem sair para o sistema operacional.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate300,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Change Admin PIN Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Slate800, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Alterar PIN de Administrador",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "PIN atual configurado: $currentPin",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 6) newPin = it },
                        placeholder = { Text("Novo PIN (4-6 dígitos)", color = Slate600) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_admin_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate850,
                            unfocusedContainerColor = Slate850,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (newPin.length >= 4) {
                                onUpdatePin(newPin)
                                newPin = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("update_pin_button")
                    ) {
                        Text(
                            text = "Atualizar",
                            fontWeight = FontWeight.Bold,
                            color = Slate950
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: ADD EMPLOYEE
// -------------------------------------------------------------
@Composable
private fun AddEmployeeDialog(
    onDismiss: () -> Unit,
    onConfirm: (matricula: String, nome: String, cargo: String, departamento: String) -> Unit
) {
    var matricula by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cadastrar Novo Colaborador",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula / PIN (único) *", color = Slate400) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_employee_matricula"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome Completo *", color = Slate400) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_employee_nome"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = cargo,
                    onValueChange = { cargo = it },
                    label = { Text("Cargo (Ex: Operador)", color = Slate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = departamento,
                    onValueChange = { departamento = it },
                    label = { Text("Departamento (Ex: Produção)", color = Slate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = "⚠️ $errorMessage",
                        style = MaterialTheme.typography.bodySmall,
                        color = SaidaRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (matricula.isBlank() || nome.isBlank()) {
                        errorMessage = "Matrícula e Nome são obrigatórios."
                        return@Button
                    }
                    onConfirm(matricula.trim(), nome.trim(), cargo.trim(), departamento.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier.testTag("confirm_add_employee_button")
            ) {
                Text("Salvar", fontWeight = FontWeight.Bold, color = Slate950)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar", color = Slate300)
            }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(20.dp)
    )
}
