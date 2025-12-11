package com.example.proyectonovawear.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectonovawear.controller.CrearProductosViewModel
import com.example.proyectonovawear.model.Productos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarProductoScreen(
    navController: NavController,
    appNavController: NavController,
    viewModel: CrearProductosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarCamara by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val usuarioId = prefs.getLong("usuarioId", -1L)

    // 👇 Inicializamos personaId en el ViewModel al entrar a la pantalla
    LaunchedEffect(usuarioId) {
        if (usuarioId != -1L) {
            viewModel.setPersonaId(usuarioId)
            viewModel.cargarMisProductos()
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageChange(uri)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Agregar Producto") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating
            )

            OutlinedTextField(
                value = state.talla,
                onValueChange = viewModel::onTallaChange,
                label = { Text("Talla") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating
            )

            OutlinedTextField(
                value = state.precio,
                onValueChange = {
                    if (it.all { c -> c.isDigit() }) viewModel.onPrecioChange(it.toIntOrNull() ?: 0)
                },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (state.imagenUri == null) {
                OutlinedButton(
                    onClick = { lanzadorGaleria.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isCreating
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Seleccionar Imagen")
                }
                OutlinedButton(
                    onClick = { mostrarCamara = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isCreating
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar foto")
                }
            } else {
                AsyncImage(
                    model = state.imagenUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { lanzadorGaleria.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }

            // --- Botón para crear producto ---
            Button(
                onClick = { viewModel.agregarProducto() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isCreating
            ) {
                Text("Publicar Producto")
            }

            if (state.isCreating) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            // --- Mostrar errores ---
            state.createError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // --- Si se creó correctamente ---
            state.created?.let {
                LaunchedEffect(it) {
                    snackbarHostState.showSnackbar("Producto creado correctamente")
                    viewModel.limpiarResultado()
                }
            }
        }

        if (mostrarCamara) {
            CameraCaptureScreen(
                onImageCaptured = { uri ->
                    viewModel.onImageChange(uri)
                    mostrarCamara = false
                },
                onClose = { mostrarCamara = false }
            )
        }
    }
}
