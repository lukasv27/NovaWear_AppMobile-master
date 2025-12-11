package com.example.proyectonovawear.controller

import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.model.RegistroDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroUsuarioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var viewModel: RegistroUsuarioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        apiService = mock()
        // Inyectamos el mock en el ViewModel usando reflexión
        viewModel = RegistroUsuarioViewModel()
        val apiField = RegistroUsuarioViewModel::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(viewModel, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando hay campos vacíos se setea error`() = runTest {
        viewModel.onNombreChange("")
        viewModel.onApellidoChange("")
        viewModel.onCorreoChange("")
        viewModel.onPasswordChange("")

        viewModel.registrarUsuario()

        val state = viewModel.state.value
        assertEquals("No pueden haber campos vacíos", state.error)
        assertFalse(state.registroExitoso)
    }

    @Test
    fun `registro exitoso actualiza estado correctamente`() = runTest {
        // Mockear respuesta exitosa
        whenever(apiService.signup(RegistroDTO("Lukas", "Test", "lukas@test.com", "1234")))
            .thenReturn(Response.success(RegistroDTO("Lukas", "Test", "lukas@test.com", "1234")))

        viewModel.onNombreChange("Lukas")
        viewModel.onApellidoChange("Test")
        viewModel.onCorreoChange("lukas@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.registrarUsuario()

        // Avanzar tiempo simulado del delay
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.registroExitoso)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `registro con error devuelve mensaje de error`() = runTest {
        // Mockear respuesta con error 400
        whenever(apiService.signup(RegistroDTO("Lukas", "Test", "lukas@test.com", "1234")))
            .thenReturn(Response.error(400, ResponseBody.create(null, "")))

        viewModel.onNombreChange("Lukas")
        viewModel.onApellidoChange("Test")
        viewModel.onCorreoChange("lukas@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.registrarUsuario()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Error en el registro (400)", state.error)
        assertFalse(state.registroExitoso)
    }

    @Test
    fun `limpiarEstado reinicia el uiState`() {
        viewModel.onNombreChange("Lukas")
        viewModel.onApellidoChange("Test")
        viewModel.onCorreoChange("lukas@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.limpiarEstado()

        val state = viewModel.state.value
        assertEquals("", state.nombre)
        assertEquals("", state.apellido)
        assertEquals("", state.correo)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertFalse(state.registroExitoso)
        assertEquals(0f, state.progreso)
        assertNull(state.error)
    }
}