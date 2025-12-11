package com.example.proyectonovawear.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Chat(
    val id: Long = 0, // <--- aquí agregamos el chatId del backend
    val productoId: Long,
    val productoNombre: String,
    val mensajes: SnapshotStateList<Mensaje> = mutableStateListOf()
)
