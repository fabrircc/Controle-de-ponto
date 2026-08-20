package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "funcionarios",
    indices = [Index(value = ["matricula"], unique = true)]
)
data class Funcionario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matricula: String,
    val nome: String,
    val cargo: String = "Colaborador",
    val departamento: String = "Geral",
    val ativo: Boolean = true,
    val dataCadastro: String = ""
)
