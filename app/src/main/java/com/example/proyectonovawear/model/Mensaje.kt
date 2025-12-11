package com.example.proyectonovawear.model

data class Mensaje(
    val id: Long,
    val contenido: String,
    val fecha: Long? = null,
    val esMio: Boolean = false,
    val personaId: Long? = null

    )