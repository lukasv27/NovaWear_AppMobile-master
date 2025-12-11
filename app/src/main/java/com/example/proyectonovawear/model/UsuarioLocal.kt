package com.example.proyectonovawear.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_local")
data class UsuarioLocal(
    @PrimaryKey val id: Int,
    val nombre: String,
    val correo: String
)
