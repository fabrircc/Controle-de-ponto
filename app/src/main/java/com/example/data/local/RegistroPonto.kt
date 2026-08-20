package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_ponto",
    foreignKeys = [
        ForeignKey(
            entity = Funcionario::class,
            parentColumns = ["id"],
            childColumns = ["funcionarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["funcionarioId"]),
        Index(value = ["dataHoraBatida"])
    ]
)
data class RegistroPonto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val funcionarioId: Long,
    val funcionarioMatricula: String,
    val funcionarioNome: String,
    val tipoBatida: String, // "ENTRADA" or "SAIDA"
    val dataHoraBatida: String, // Horário considerado para a folha (AAAA-MM-DD HH:MM:SS)
    val dataHoraSistema: String, // Horário real do sistema no momento do registro (Auditoria)
    val isRetroativo: Boolean = false,
    val justificativa: String? = null
)
