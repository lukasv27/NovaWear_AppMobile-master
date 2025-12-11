package com.example.proyectonovawear.api

import com.example.proyectonovawear.model.Chat
import com.example.proyectonovawear.model.Mensaje
import retrofit2.http.*

interface ChatApiService {

    @GET("api/chats/{productoId}")
    suspend fun getChatByProducto(@Path("productoId") productoId: Long): Chat

    @POST("api/chats/{productoId}/mensajes/{personaId}")
    suspend fun enviarMensaje(
        @Path("productoId") productoId: Long,
        @Path("personaId") personaId: Long,
        @Body contenido: String
    ): Mensaje
}
