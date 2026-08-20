package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdminPasswordDialog
import com.example.ui.components.DigitalClock
import com.example.ui.components.PunchSuccessDialog
import com.example.ui.components.RetroactivePunchDialog
import com.example.ui.components.VirtualNumpad
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.EntradaGreen
import com.example.ui.theme.EntradaGreenDark
import com.example.ui.theme.RetroAmber
import com.example.ui.theme.SaidaRed
import com.example.ui.theme.SaidaRedDark
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.PontoViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TotemScreen(
    viewModel: PontoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val matriculaInput by viewModel.matriculaInput.collectAsStateWithLifecycle()
    val matchedFuncionario by viewModel.matchedFuncionario.collectAsStateWithLifecycle()
    val allFuncionarios by viewModel.allFuncionarios.collectAsStateWithLifecycle()
    val lastPunchSuccess by viewModel.lastPunchSuccess.collectAsStateWithLifecycle()
    val showRetroactiveDialog by viewModel.showRetroactiveDialog.collectAsStateWithLifecycle()
    val showAdminPasswordDialog by viewModel.showAdminPasswordDialog.collectAsStateWithLifecycle()
    val adminPin by viewModel.adminPin.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Slate950, Slate900, Color(0xFF090D16))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("totem_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Admin quick gear button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RP",
                            color = AccentBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "REGISTRO DE PONTO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { viewModel.openAdminPasswordDialog() },
                    modifier = Modifier.testTag("admin_panel_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Painel do Administrador",
                        tint = AccentBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Digital Clock component
            Box(modifier = Modifier.widthIn(max = 540.dp)) {
                DigitalClock()
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Matricula Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.5.dp,
                        if (matchedFuncionario != null) EntradaGreen.copy(alpha = 0.6f) else Slate700,
                        RoundedCornerShape(20.dp)
                    )
                    .testTag("matricula_display_card"),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DIGITE SUA MATRÍCULA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate400,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big Matricula display or placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900)
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (matriculaInput.isEmpty()) {
                            Text(
                                text = "• • • •",
                                fontSize = 32.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Slate600,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = matriculaInput,
                                fontSize = 34.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 6.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("matricula_active_text")
                            )
                        }
                    }

                    // Matched Employee preview pill
                    AnimatedVisibility(
                        visible = matchedFuncionario != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        matchedFuncionario?.let { func ->
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(EntradaGreen.copy(alpha = 0.15f))
                                    .border(1.dp, EntradaGreen.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EntradaGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${func.nome} (${func.cargo})",
                                    color = EntradaGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Virtual Numpad
            Box(modifier = Modifier.widthIn(max = 540.dp)) {
                VirtualNumpad(
                    onDigitClick = { digit -> viewModel.onDigitInput(digit) },
                    onClearClick = { viewModel.onClearInput() },
                    onBackspaceClick = { viewModel.onBackspaceInput() }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Dual Jumbo Action Buttons (ENTRADA / SAÍDA)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // ENTRADA Button
                Button(
                    onClick = { viewModel.baterPonto("ENTRADA") },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .testTag("btn_entrada"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EntradaGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ENTRADA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // SAÍDA Button
                Button(
                    onClick = { viewModel.baterPonto("SAIDA") },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .testTag("btn_saida"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaidaRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAÍDA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Secondary Action: Inserir Ponto Retroativo
            TextButton(
                onClick = { viewModel.openRetroactiveDialog() },
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .testTag("btn_open_retroactive_dialog")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = RetroAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inserir Ponto Retroativo / Esqueceu de bater?",
                    color = RetroAmber,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Floating Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )

        // Overlay Dialogs
        lastPunchSuccess?.let { registro ->
            PunchSuccessDialog(
                registro = registro,
                onDismiss = { viewModel.dismissPunchSuccess() }
            )
        }

        if (showRetroactiveDialog) {
            RetroactivePunchDialog(
                funcionarios = allFuncionarios,
                initialMatricula = matriculaInput,
                onDismiss = { viewModel.closeRetroactiveDialog() },
                onConfirm = { matricula, tipo, dataHora, justificativa ->
                    viewModel.registrarPontoRetroativo(matricula, tipo, dataHora, justificativa)
                }
            )
        }

        if (showAdminPasswordDialog) {
            AdminPasswordDialog(
                correctPin = adminPin,
                onDismiss = { viewModel.closeAdminPasswordDialog() },
                onSuccess = { viewModel.enterAdminMode() }
            )
        }
    }
}
