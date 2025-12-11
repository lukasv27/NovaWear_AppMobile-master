package com.example.proyectonovawear.controller

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.model.Productos
import com.example.proyectonovawear.network.RetrofitProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CrearProductosUiState(
    val nombre: String = "",
    val precio: String = "",
    val descripcion: String = "",
    val talla: String = "",
    val imagenUri: Uri? = null,
    val createError: String? = null,
    val created: Boolean? = null,
    val isCreating: Boolean = false,
    val list: List<Productos> = emptyList(),
    val listaCargando: Boolean = false,
    val errorCargando: String? = null
)

class CrearProductosViewModel(
    private val api: ApiService = RetrofitProvider.create(),

    autoLoad: Boolean = true
) : ViewModel() {

    private val _state = MutableStateFlow(CrearProductosUiState())
    val state: StateFlow<CrearProductosUiState> = _state.asStateFlow()

    // 👇 Persona logueada (dueño del producto)
    private var personaId: Long? = null
    fun setPersonaId(id: Long) {
        personaId = id
    }

    init {
        if (autoLoad) {
            cargarProductos()
        }
    }

    fun onNombreChange(value: String) {
        _state.update { it.copy(nombre = value, createError = null) }
    }

    fun onDescripcionChange(value: String) {
        _state.update { it.copy(descripcion = value, createError = null) }
    }

    fun onTallaChange(value: String) {
        _state.update { it.copy(talla = value, createError = null) }
    }

    fun onPrecioChange(value: Int) {
        _state.update { it.copy(precio = value.toString(), createError = null) }
    }

    fun onImageChange(value: Uri?) {
        _state.update { it.copy(imagenUri = value, createError = null) }
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _state.update { it.copy(listaCargando = true, errorCargando = null) }
            flow { emit(api.getProductos()) }
                .onEach { productos ->
                    _state.update { it.copy(list = productos, listaCargando = false) }
                }
                .catch { e ->
                    _state.update { it.copy(listaCargando = false, errorCargando = e.message) }
                }
                .collect()
        }
    }

    fun agregarProducto() {
        val s = _state.value

        // Validar campos obligatorios
        if (s.nombre.isBlank() || s.descripcion.isBlank() || s.talla.isBlank() || s.precio.isBlank() || s.imagenUri == null) {
            _state.update { it.copy(createError = "Todos los campos son obligatorios") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true, createError = null, created = null) }
            try {
                val nuevoProducto = Productos(
                    nombre = s.nombre,
                    descripcion = s.descripcion,
                    talla = s.talla,
                    precio = s.precio.toIntOrNull() ?: 0,
                    imagenUri = s.imagenUri.toString()
                )

                personaId?.let { id ->
                    // 👇 Llamada real al backend
                    val productoGuardado = api.agregarProducto(id, nuevoProducto)

                    // Actualizar lista con lo que devuelve el backend
                    _state.update {
                        it.copy(
                            list = it.list + productoGuardado,
                            nombre = "",
                            descripcion = "",
                            talla = "",
                            precio = "",
                            imagenUri = null,
                            created = true,
                            isCreating = false
                        )
                    }
                } ?: run {
                    _state.update { it.copy(isCreating = false, createError = "personaId no inicializado") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isCreating = false, createError = e.message ?: "Error al crear") }
            }
        }
    }

    fun limpiarResultado() {
        _state.update { it.copy(created = null, createError = null) }
    }


    fun cargarMisProductos() {
        val id = personaId ?: return
        viewModelScope.launch {
            _state.update { it.copy(listaCargando = true, errorCargando = null) }
            try {
                val productosUsuario = api.getProductosPorPersona(id)
                _state.update { it.copy(list = productosUsuario, listaCargando = false) }
            } catch (e: Exception) {
                _state.update { it.copy(listaCargando = false, errorCargando = e.message) }
            }
        }
    }
}