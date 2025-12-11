package com.example.proyectonovawear.api

import com.example.proyectonovawear.model.LoginDTO
import com.example.proyectonovawear.model.Persona
import com.example.proyectonovawear.model.Productos
import com.example.proyectonovawear.model.RegistroDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("productos/{personaId}")
    suspend fun agregarProducto(
        @Path("personaId") personaId: Long,
        @Body body: Productos
    ): Productos

    // Todos los productos del marketplace
    @GET("productos")
    suspend fun getProductos(): List<Productos>

    // Productos de un usuario específico
    @GET("productos/persona/{personaId}")
    suspend fun getProductosPorPersona(
        @Path("personaId") personaId: Long
    ): List<Productos>


    @POST("/personas/registro")// aqui es la ruta del backend
    suspend fun signup(@Body registroDTO: RegistroDTO, @Header("Content-Type") contentType: String = "application/json"): Response<RegistroDTO>//definimos el metodo con su correspiondiente mapeo y definiendo los header necesarios para la consulta recorder que si no se define los header el backen retornara error y problemas de permiso

    @POST("/personas/login")
    suspend fun loginUp(@Body loginDTO: LoginDTO): Response<Persona>




}