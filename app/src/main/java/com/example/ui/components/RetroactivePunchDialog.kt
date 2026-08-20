package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.Funcionario
import com.example.ui.theme.EntradaGreen
import com.example.ui.theme.RetroAmber
import com.example.ui.theme.SaidaRed
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun RetroactivePunchDialog(
    funcionarios: List<Funcionario>,
    initialMatricula: String = "",
    onDismiss: () -> Unit,
    onConfirm: (matricula: String, tipo: String, dataHora: String, justificativa: String) -> Unit
) {
    val context = LocalContext.current

    var matricula by remember { mutableStateOf(initialMatricula) }
    var selectedFuncionario by remember(matricula, funcionarios) {
        mutableStateOf(funcionarios.find { it.matricula == matricula.trim() })
    }

    var tipoBatida by remember { mutableStateOf("ENTRADA") }

    val today = remember { LocalDate.now() }
    val nowTime = remember { LocalTime.now() }

    var selectedDate by remember { mutableStateOf(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var selectedTime by remember { mutableStateOf(nowTime.format(DateTimeFormatter.ofPattern("HH:mm"))) }

    val justificativasList = listOf(
        "Esquecimento",
        "Falha do Equipamento",
        "Trabalho Externo / Visita a Cliente",
        "Consulta Médica / Atestado",
        "Problema de Transporte / Trânsito",
        "Outro"
    )
    var selectedJustificativa by remember { mutableStateOf(justificativasList[0]) }
    var customObservacao by remember { mutableStateOf("") }
    var justificativaExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // DatePicker and TimePicker helpers
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, RetroAmber.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("retroactive_punch_dialog"),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RetroAmber.copy(alpha = 0.15f))
                            .border(1.dp, RetroAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = RetroAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Lançamento Retroativo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Registro de Ponto Excepcional",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Matrícula Input with Autocomplete/Validation
                Text(
                    text = "Matrícula do Colaborador *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate300
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = matricula,
                    onValueChange = {
                        matricula = it
                        selectedFuncionario = funcionarios.find { f -> f.matricula == it.trim() }
                        errorMessage = null
                    },
                    placeholder = { Text("Ex: 1001 ou 1234", color = Slate600) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selectedFuncionario != null) EntradaGreen else Slate500
                        )
                    },
                    trailingIcon = {
                        if (selectedFuncionario != null) {
                            Text(
                                text = selectedFuncionario?.nome ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = EntradaGreen,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("retro_matricula_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850,
                        focusedBorderColor = RetroAmber,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                if (selectedFuncionario != null) {
                    Text(
                        text = "✓ ${selectedFuncionario?.nome} (${selectedFuncionario?.cargo})",
                        style = MaterialTheme.typography.labelSmall,
                        color = EntradaGreen,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tipo de Batida (ENTRADA / SAÍDA) Segmented
                Text(
                    text = "Tipo de Registro *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate300
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate850)
                        .border(1.dp, Slate700, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tipoBatida == "ENTRADA") EntradaGreen else Color.Transparent)
                            .clickable { tipoBatida = "ENTRADA" }
                            .padding(vertical = 10.dp)
                            .testTag("retro_tipo_entrada"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🟢 ENTRADA",
                            fontWeight = FontWeight.Bold,
                            color = if (tipoBatida == "ENTRADA") Color.White else Slate400,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tipoBatida == "SAIDA") SaidaRed else Color.Transparent)
                            .clickable { tipoBatida = "SAIDA" }
                            .padding(vertical = 10.dp)
                            .testTag("retro_tipo_saida"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔴 SAÍDA",
                            fontWeight = FontWeight.Bold,
                            color = if (tipoBatida == "SAIDA") Color.White else Slate400,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Date picker button
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Data Ocorrida *",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate850)
                                .border(1.dp, Slate700, RoundedCornerShape(14.dp))
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 12.dp, vertical = 14.dp)
                                .testTag("retro_date_picker_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedDate,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = RetroAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Time picker button
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Horário *",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate850)
                                .border(1.dp, Slate700, RoundedCornerShape(14.dp))
                                .clickable { timePickerDialog.show() }
                                .padding(horizontal = 12.dp, vertical = 14.dp)
                                .testTag("retro_time_picker_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedTime,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = RetroAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Justificativa Dropdown
                Text(
                    text = "Motivo / Justificativa *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate300
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate850)
                            .border(1.dp, Slate700, RoundedCornerShape(14.dp))
                            .clickable { justificativaExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                            .testTag("retro_justificativa_dropdown"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedJustificativa,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Slate400
                        )
                    }

                    DropdownMenu(
                        expanded = justificativaExpanded,
                        onDismissRequest = { justificativaExpanded = false },
                        modifier = Modifier
                            .background(Slate850)
                            .border(1.dp, Slate700)
                    ) {
                        justificativasList.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option, color = Color.White) },
                                onClick = {
                                    selectedJustificativa = option
                                    justificativaExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedJustificativa == "Outro") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customObservacao,
                        onValueChange = { customObservacao = it },
                        placeholder = { Text("Especifique o motivo...", color = Slate600) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("retro_custom_observacao_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate850,
                            unfocusedContainerColor = Slate850,
                            focusedBorderColor = RetroAmber,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Legal / Compliance notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate800)
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RetroAmber,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Para auditoria e compliance, o sistema grava o horário solicitado para a folha ($selectedDate $selectedTime:00) e o instante exato em que esta solicitação foi emitida.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "⚠️ $errorMessage",
                        style = MaterialTheme.typography.bodySmall,
                        color = SaidaRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("retro_cancel_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val trimmedMatricula = matricula.trim()
                            if (trimmedMatricula.isEmpty()) {
                                errorMessage = "Por favor, digite a matrícula."
                                return@Button
                            }
                            if (selectedFuncionario == null) {
                                errorMessage = "Matrícula não cadastrada no sistema."
                                return@Button
                            }

                            val fullJustificativa = if (selectedJustificativa == "Outro" && customObservacao.isNotBlank()) {
                                "Outro: ${customObservacao.trim()}"
                            } else {
                                selectedJustificativa
                            }

                            val fullDataHora = "$selectedDate $selectedTime:00"
                            onConfirm(trimmedMatricula, tipoBatida, fullDataHora, fullJustificativa)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("retro_confirm_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RetroAmber),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Salvar Retroativo",
                            fontWeight = FontWeight.Bold,
                            color = Slate950
                        )
                    }
                }
            }
        }
    }
}
