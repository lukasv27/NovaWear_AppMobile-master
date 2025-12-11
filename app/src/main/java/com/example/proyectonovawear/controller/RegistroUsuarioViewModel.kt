package com.example.proyectonovawear.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.model.RegistroDTO
import com.example.proyectonovawear.network.RetrofitProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val progreso: Float = 0f,
    val registroExitoso: Boolean = false,
    val error: String? = null
)

class RegistroUsuarioViewModel : ViewModel() {

    private val api = RetrofitProvider.create<ApiService>()

    private val _state = MutableStateFlow(RegistroUiState())
    val state: StateFlow<RegistroUiState> = _state.asStateFlow()

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, error = null) }
    }

    fun onApellidoChange(value: String) {
        _state.update { it.copy(apellido = value, error = null) }
    }

    fun onCorreoChange(value: String) {
        _state.update { it.copy(correo = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    fun registrarUsuario() {
        val s = _state.value
        if (s.nombre.isBlank() || s.apellido.isBlank() || s.correo.isBlank() || s.password.isBlank()) {
            _state.update { it.copy(error = "No pueden haber campos vacíos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, progreso = 0f, registroExitoso = false) }

            try {
                // Simulación visual del progreso
                for (i in 1..100) {
                    _state.update { it.copy(progreso = i / 100f) }
                    delay(10)
                }

                val response = api.signup(RegistroDTO(s.nombre, s.apellido, s.correo, s.password))
                if (response.isSuccessful) {
                    _state.update {
                        it.copy(isLoading = false, registroExitoso = true)
                    }
                } else {
                    _state.update {
                        it.copy(isLoading = false, error = "Error en el registro (${response.code()})")
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Error de conexión")
                }
            }
        }
    }

    fun limpiarEstado() {
        _state.update { RegistroUiState() }
    }
}
