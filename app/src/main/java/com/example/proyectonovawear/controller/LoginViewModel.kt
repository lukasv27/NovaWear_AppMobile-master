package com.example.proyectonovawear.controller

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.db.AppDatabase
import com.example.proyectonovawear.model.LoginDTO
import com.example.proyectonovawear.model.UsuarioLocal
import com.example.proyectonovawear.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false,
    val nombreUsuario: String = ""
)

class LoginViewModel(
    private val api: ApiService = RetrofitProvider.create(),
    private val mockDb: AppDatabase? = null // 👈 parámetro opcional
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    // Si no se pasa mockDb, se inicializa luego con setDatabase()
    private var db: AppDatabase? = mockDb

    /** Llamar desde Compose o Activity para inicializar la DB real */
    fun setDatabase(context: Context) {
        if (db == null) {
            db = AppDatabase.get(context)
        }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, loginError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, loginError = null) }
    }

    fun login() {
        val s = _state.value

        if (s.email.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(loginError = "No pueden haber campos vacíos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loginError = null) }

            try {
                val response = api.loginUp(LoginDTO(s.email, s.password))

                if (response.isSuccessful) {
                    val persona = response.body()
                    if (persona != null) {
                        db?.usuarioDao()?.guardarUsuario(
                            UsuarioLocal(
                                id = persona.id.toInt(),
                                nombre = persona.nombre,
                                correo = persona.email
                            )
                        )

                        _state.update {
                            it.copy(
                                isLoading = false,
                                loginSuccess = true,
                                nombreUsuario = persona.nombre
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, loginError = "Error de respuesta del servidor") }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, loginError = "Credenciales incorrectas") }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, loginError = e.message ?: "Error de conexión")
                }
            }
        }
    }

    fun limpiarEstado() {
        _state.update { it.copy(loginError = null, loginSuccess = false) }
    }
}