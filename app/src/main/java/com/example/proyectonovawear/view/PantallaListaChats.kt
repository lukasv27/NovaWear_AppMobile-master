package com.example.proyectonovawear.view



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import com.example.proyectonovawear.controller.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaChats(
    navController: NavController,
    appNavController: NavController,
    chatViewModel: ChatViewModel
) {
    val chats = chatViewModel.chats // lista de chats activos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Chats") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9E9E9E),
                    titleContentColor = Color.Black
                ),

                actions = {
                    Button(onClick = { appNavController.navigate("login") }) {
                        Text("CERRAR SESION")
                    }
                }
            )
        }
    ) { padding ->
        if (chats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes chats activos")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(chats, key = { it.producto.id ?: -1 }) { chat ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("mensaje/${chat.producto.id}/${chat.producto.nombre}")
                            },

                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = chat.producto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (chat.mensajes.isNotEmpty()) {
                                Text(
                                    text = "Último mensaje: ${chat.mensajes.last().contenido}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            } else {
                                Text(
                                    text = "Sin mensajes aún",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
