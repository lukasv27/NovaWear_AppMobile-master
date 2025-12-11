package com.example.proyectonovawear.model

import android.net.Uri

data class Productos (
    val id: Long? = null,
    val nombre: String,
    val precio: Int,
    val descripcion: String,
    val talla: String,
    val imagenUri: String? = null,
    val dueno: Persona? = null


)