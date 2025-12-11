package com.example.proyectonovawear.view


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectonovawear.controller.ChatViewModel


import com.example.proyectonovawear.model.Mensaje


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pantallaMensaje(
    navController: NavController,
    appNavController: NavController,
    productoId: Long,
    productoNombre: String,
    usuarioId: Long, // 👈 id del usuario logueado
    chatViewModel: ChatViewModel
) {
    var nuevoMensaje by remember { mutableStateOf("") }

    // 👇 Al entrar a la pantalla, seteamos el personaId
    LaunchedEffect(usuarioId, productoId) {
        chatViewModel.setPersonaId(usuarioId)
        chatViewModel.refreshChat(productoId, productoNombre)
    }


    // Obtenemos el chat correspondiente al producto
    val chat = remember { chatViewModel.getChatForProduct(productoId, productoNombre) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9E9E9E),
                    titleContentColor = Color.Black
                ),
                actions = {
                    Button(onClick = {
                        appNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("CERRAR SESIÓN")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Lista de mensajes
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true // los mensajes más recientes abajo
            ) {
                items(chat.mensajes.reversed(), key = { it.id }) { mensaje ->
                    tarjetaMensaje(mensaje)
                }
            }

            // Row para escribir y enviar mensaje
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevoMensaje,
                    onValueChange = { nuevoMensaje = it },
                    label = { Text("Escribe un mensaje") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (nuevoMensaje.isNotBlank()) {
                            val mensaje = Mensaje(
                                id = System.nanoTime(),
                                contenido = nuevoMensaje,
                                fecha = System.currentTimeMillis(),
                                esMio = true,
                                personaId = usuarioId // 👈 se guarda con el id correcto
                            )
                            chatViewModel.addMessage(productoId, productoNombre, mensaje)
                            nuevoMensaje = ""
                        }
                    },
                    enabled = nuevoMensaje.isNotBlank()
                ) {
                    Text("ENVIAR")
                }
            }
        }
    }
}