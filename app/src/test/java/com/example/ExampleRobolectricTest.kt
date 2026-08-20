package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.Funcionario
import com.example.data.local.PontoDatabase
import com.example.data.repository.PontoRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: PontoDatabase
  private lateinit var repository: PontoRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, PontoDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = PontoRepository(database.pontoDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Registro de Ponto", appName)
  }

  @Test
  fun `register normal and retroactive punches successfully`() = runBlocking {
    val funcionario = Funcionario(
      matricula = "1001",
      nome = "Ana Silva",
      cargo = "Analista",
      departamento = "Operações",
      ativo = true
    )
    val cadastrado = repository.cadastrarFuncionario("1001", "Ana Silva", "Analista", "Operações")
    assertTrue(cadastrado.isSuccess)

    // Test Normal Punch (Entrada)
    val entrada = repository.registrarPonto("1001", "ENTRADA")
    assertTrue(entrada.isSuccess)
    val regEntrada = entrada.getOrNull()
    assertNotNull(regEntrada)
    assertEquals("ENTRADA", regEntrada?.tipoBatida)
    assertEquals(false, regEntrada?.isRetroativo)

    // Test Retroactive Punch (Saída)
    val saidaRetro = repository.registrarPonto(
      matricula = "1001",
      tipoBatida = "SAIDA",
      customDataHora = "2026-08-20 17:00:00",
      isRetroativo = true,
      justificativa = "Esquecimento"
    )
    assertTrue(saidaRetro.isSuccess)
    val regSaida = saidaRetro.getOrNull()
    assertNotNull(regSaida)
    assertEquals("SAIDA", regSaida?.tipoBatida)
    assertEquals(true, regSaida?.isRetroativo)
    assertEquals("Esquecimento", regSaida?.justificativa)
    assertEquals("2026-08-20 17:00:00", regSaida?.dataHoraBatida)
  }
}

