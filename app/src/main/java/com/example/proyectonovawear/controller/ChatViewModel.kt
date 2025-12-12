package com.example.proyectonovawear.controller

import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectonovawear.api.ChatApiService
import com.example.proyectonovawear.model.Chat
import com.example.proyectonovawear.model.Mensaje
import com.example.proyectonovawear.model.Persona
import com.example.proyectonovawear.model.Productos
import com.example.proyectonovawear.network.RetrofitProvider
import kotlinx.coroutines.launch

class ChatViewModel(
    private val api: ChatApiService = RetrofitProvider.create(),
    private var personaId: Long? = null
) : ViewModel() {

    // 👇 mantenemos el nombre _chats pero lo hacemos observable
    private val _chats = mutableStateListOf<Chat>()
    val chats: List<Chat> get() = _chats

    fun setPersonaId(id: Long) {
        personaId = id
    }

    fun getChatForProduct(productoId: Long, productoNombre: String): Chat {
        val existing = _chats.find { it.producto.id == productoId }
        if (existing != null) return existing

        val nuevo = Chat(
            id = -1,
            producto = Productos(productoId, productoNombre, 0, "", "", ""),
            dueno = Persona(0, "", "", ""),
            interesado = Persona(personaId ?: 0, "", "", ""),
            mensajes = mutableStateListOf()
        )

        _chats.add(nuevo)

        // cargar mensajes iniciales
        refreshChat(productoId, productoNombre)

        return nuevo
    }

    fun addMessage(productoId: Long, productoNombre: String, mensaje: Mensaje) {
        val chat = getChatForProduct(productoId, productoNombre)
        chat.mensajes.add(mensaje.copy(esMio = true))


        viewModelScope.launch {
            try {
                personaId?.let { interesadoId ->
                    api.enviarMensaje(productoId, interesadoId, mensaje.contenido!!)
                    // 👇 refrescamos inmediatamente para traer mensajes de otros usuarios
                    refreshChat(productoId, productoNombre)
                } ?: Log.e("ChatViewModel", "personaId no inicializado")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error enviando mensaje", e)
            }
        }
    }

    fun refreshChat(productoId: Long, productoNombre: String) {
        viewModelScope.launch {
            try {
                personaId?.let { interesadoId ->
                    val chatData = api.getChat(productoId, interesadoId)

                    val mensajesTransformados = chatData.mensajes.map { mensaje ->
                        mensaje.copy(esMio = mensaje.personaId == personaId)
                    }

                    val chat = getChatForProduct(productoId, productoNombre)
                    chat.mensajes.clear()
                    chat.mensajes.addAll(mensajesTransformados)

                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error refrescando chat", e)
            }
        }
    }
}