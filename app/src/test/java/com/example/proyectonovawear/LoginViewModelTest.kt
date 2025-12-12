package com.example.proyectonovawear.controller

import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.dao.UsuarioDao
import com.example.proyectonovawear.db.AppDatabase

import com.example.proyectonovawear.model.LoginDTO
import com.example.proyectonovawear.model.Persona
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.*
import org.mockito.Mockito.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class
LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var apiService: ApiService
    private lateinit var mockDb: AppDatabase
    private lateinit var mockDao: UsuarioDao

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        apiService = mock(ApiService::class.java)
        mockDb = mock(AppDatabase::class.java)
        mockDao = mock(UsuarioDao::class.java)

        `when`(mockDb.usuarioDao()).thenReturn(mockDao)

        viewModel = LoginViewModel(apiService, mockDb)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cuando email o password están vacíos, se setea loginError`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")

        viewModel.login()

        val state = viewModel.state.value
        Assert.assertEquals("No pueden haber campos vacíos", state.loginError)
        Assert.assertFalse(state.loginSuccess)
    }

    @Test
    fun `login exitoso actualiza estado`() = runTest {
        val persona = Persona(id = 1L, nombre = "Lukas", apellido = "perez", email = "lukas@test.com")
        `when`(apiService.loginUp(LoginDTO("lukas@test.com", "1234")))
            .thenReturn(Response.success(persona))

        viewModel.onEmailChange("lukas@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertTrue(state.loginSuccess)
        Assert.assertEquals("Lukas", state.nombreUsuario)
        Assert.assertNull(state.loginError)
    }


    @Test
    fun `login con credenciales incorrectas devuelve error`() = runTest {
        `when`(apiService.loginUp(LoginDTO("wrong@test.com", "badpass")))
            .thenReturn(Response.error(401, ResponseBody.create(null, "")))

        viewModel.onEmailChange("wrong@test.com")
        viewModel.onPasswordChange("badpass")

        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("Credenciales incorrectas", state.loginError)
        Assert.assertFalse(state.loginSuccess)
    }

    @Test
    fun `login lanza excepción y muestra mensaje de error`() = runTest {
        `when`(apiService.loginUp(LoginDTO("error@test.com", "1234")))
            .thenThrow(RuntimeException("Network down"))

        viewModel.onEmailChange("error@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals("Network down", state.loginError)
        Assert.assertFalse(state.loginSuccess)
    }

    @Test
    fun `limpiarEstado reinicia loginError y loginSuccess`() {
        viewModel.onEmailChange("lukas@test.com")
        viewModel.onPasswordChange("1234")

        viewModel.limpiarEstado()

        val state = viewModel.state.value
        Assert.assertNull(state.loginError)
        Assert.assertFalse(state.loginSuccess)
    }
}