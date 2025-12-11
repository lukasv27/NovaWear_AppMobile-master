package com.example.proyectonovawear.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class Chat(
    val id: Long,
    val producto: Productos,
    val dueno: Persona,
    val interesado: Persona,
    val mensajes: SnapshotStateList<Mensaje> = mutableStateListOf()
)


