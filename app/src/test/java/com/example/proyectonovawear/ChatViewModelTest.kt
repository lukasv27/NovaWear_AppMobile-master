package com.example.proyectonovawear

import androidx.compose.runtime.mutableStateListOf
import com.example.proyectonovawear.api.ChatApiService
import com.example.proyectonovawear.controller.ChatViewModel
import com.example.proyectonovawear.model.Chat
import com.example.proyectonovawear.model.Mensaje
import com.example.proyectonovawear.model.Persona
import com.example.proyectonovawear.model.Productos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify


class ChatViewModelTest {

    private lateinit var viewModel: ChatViewModel
    private lateinit var api: ChatApiService

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mock(ChatApiService::class.java)
        viewModel = ChatViewModel(api)
        viewModel.setPersonaId(99L) // simular usuario logueado
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addMessage agrega mensaje localmente con esMio=true`() {
        val mensaje = Mensaje(
            id = 1L,
            contenido = "Hola!",
            fecha = System.currentTimeMillis(),
            esMio = false,
            personaId = 99L
        )

        viewModel.addMessage(10L, "Producto Test", mensaje)

        val chat = viewModel.chats.find { it.producto.id == 10L }
        Assert.assertNotNull(chat)
        Assert.assertEquals(1, chat!!.mensajes.size)
        Assert.assertTrue(chat.mensajes[0].esMio)
        Assert.assertEquals("Hola!", chat.mensajes[0].contenido)
    }

    @Test
    fun `addMessage llama api_enviarMensaje y luego refresca chat`() = runTest {
        // arrange
        val productoId = 10L
        val productoNombre = "Producto Test"
        val personaId = 99L
        val contenidoLocal = "Hola desde el test"

        // mensaje desde la app
        val mensajeLocal = Mensaje(
            id = 1L,
            contenido = contenidoLocal,
            fecha = 123L,
            esMio = false,
            personaId = personaId
        )

        // Lo que el backend devolvería al pedir el chat
        val mensajeBackend = Mensaje(
            id = 2L,
            contenido = "Respuesta del backend",
            fecha = 456L,
            esMio = false,
            personaId = personaId
        )

        val chatBackend = Chat(
            id = 100L,
            producto = Productos(productoId, productoNombre, 0, "", "", ""),
            dueno = Persona(1L, "Dueño", "Test", "dueno@test.com"),
            interesado = Persona(personaId, "Interesado", "Test", "int@test.com"),
            mensajes = mutableStateListOf(mensajeBackend)
        )


        `when`(api.enviarMensaje(productoId, personaId, contenidoLocal))
            .thenReturn(mensajeBackend)

        `when`(api.getChat(productoId, personaId))
            .thenReturn(chatBackend)

        // Act
        viewModel.setPersonaId(personaId)
        viewModel.addMessage(productoId, productoNombre, mensajeLocal)


        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        // se llamó a la API con los params correctos
        verify(api).enviarMensaje(productoId, personaId, contenidoLocal)
        verify(api, times(2)).getChat(productoId, personaId)

        // el chat en el ViewModel tiene los mensajes del backend
        val chat = viewModel.chats.find { it.producto.id == productoId }
        Assert.assertNotNull(chat)
        Assert.assertEquals(1, chat!!.mensajes.size)
        Assert.assertEquals("Respuesta del backend", chat.mensajes[0].contenido)
    }

    @Test
    fun `refreshChat marca esMio segun personaId`() = runTest {
        val productoId = 20L
        val productoNombre = "Otro Producto"
        val personaId = 99L

        // Mensajes que vienen del backend: uno mío y uno de otro
        val mensajeMio = Mensaje(
            id = 1L,
            contenido = "Hola, soy yo",
            fecha = 111L,
            esMio = false,
            personaId = personaId
        )

        val mensajeOtro = Mensaje(
            id = 2L,
            contenido = "Hola, soy otro",
            fecha = 222L,
            esMio = false,
            personaId = 123L
        )

        val chatBackend = Chat(
            id = 200L,
            producto = Productos(productoId, productoNombre, 0, "", "", ""),
            dueno = Persona(1L, "Dueño", "Test", "dueno@test.com"),
            interesado = Persona(personaId, "Interesado", "Test", "int@test.com"),
            mensajes = mutableStateListOf(mensajeMio, mensajeOtro)
        )

        `when`(api.getChat(productoId, personaId))
            .thenReturn(chatBackend)

        viewModel.setPersonaId(personaId)

        // Act
        viewModel.refreshChat(productoId, productoNombre)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val chat = viewModel.chats.find { it.producto.id == productoId }
        Assert.assertNotNull(chat)
        Assert.assertEquals(2, chat!!.mensajes.size)

        val msg1 = chat.mensajes[0]
        val msg2 = chat.mensajes[1]

        Assert.assertTrue(msg1.esMio)      // personaId == 99L
        Assert.assertFalse(msg2.esMio)     // personaId == 123L
    }


}