package com.example.proyectonovawear.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Mensaje(
    val id: Long,
    val contenido: String,
    val fecha: Long? = null,
    val esMio: Boolean = false,
    val personaId: Long? = null,
    val mensajes: SnapshotStateList<Mensaje> = mutableStateListOf()

    ) {
    // 👇 Calculamos esMio dinámicamente en el cliente
    fun esMio(usuarioId: Long): Boolean {
        return personaId == usuarioId
    }
}