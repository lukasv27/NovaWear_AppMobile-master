package com.example.proyectonovawear.api

import com.example.proyectonovawear.model.Chat
import com.example.proyectonovawear.model.Mensaje
import retrofit2.http.*

interface ChatApiService {

    // Obtener el chat de un producto para un interesado específico
    @GET("api/chats/{productoId}/persona/{interesadoId}")
    suspend fun getChat(
        @Path("productoId") productoId: Long,
        @Path("interesadoId") interesadoId: Long
    ): Chat

    // Obtener mensajes del chat (producto + interesado)
    @GET("api/chats/{productoId}/persona/{interesadoId}/mensajes")
    suspend fun getMensajes(
        @Path("productoId") productoId: Long,
        @Path("interesadoId") interesadoId: Long
    ): List<Mensaje>

    // Enviar mensaje desde un interesado
    @POST("api/chats/{productoId}/persona/{interesadoId}/mensajes")
    @Headers("Content-Type: text/plain")
    suspend fun enviarMensaje(
        @Path("productoId") productoId: Long,
        @Path("interesadoId") interesadoId: Long,
        @Body contenido: String
    ): Mensaje

}