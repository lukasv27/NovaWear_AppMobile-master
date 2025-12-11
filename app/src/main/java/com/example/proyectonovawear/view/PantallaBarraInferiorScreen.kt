package com.example.proyectonovawear.view




import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectonovawear.controller.ChatViewModel


import com.example.proyectonovawear.controller.CrearProductosViewModel

import com.example.proyectonovawear.model.Destination
import com.example.proyectonovawear.model.Productos

// todas las pantallas que tengan barrra inferior se muestran aqui
@Composable
fun barraInferior(appNavController: NavController, usuarioId: Long, listaProductos: MutableList<Productos>) {
    val navController = rememberNavController()
    var selectedDestination by remember { mutableIntStateOf(0) }

    val destinations = listOf(
        Destination("Productos", Icons.Default.ShoppingCart, "productos"),
        Destination("Mis Publicaciones", Icons.Default.AccountBox, "pantallaAgregar"),
        Destination("Mensajes", Icons.Default.MailOutline, "listaChats")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF9E9E9E), // COLOR DE FONDO gris
                contentColor = Color.Magenta // COLOR DEL CONTENIDO ICONO Y TEXTO
            )
            {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            selectedDestination = index
                            navController.navigate(destination.route)
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,

                                )
                        },
                        label = {
                            Text(
                                destination.label,

                                )
                        }
                    )
                }


            }
        }
    ) { innerPadding ->
        val crearProductosViewModel: CrearProductosViewModel = viewModel()
        val chatViewModel: ChatViewModel = viewModel() // NUEVO: ViewModel compartido para chats


        NavHost(
            navController = navController,
            startDestination = "productos",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("productos") {
                Productos(
                    navController = navController,
                    appNavController = appNavController,
                    viewModel = crearProductosViewModel,
                    usuarioId = usuarioId // 👈 reemplaza con el id real del usuario logueado
                )
            }
            composable("pantallaAgregar") {
                pantallaAgregar(
                    navController = navController,
                    appNavController = appNavController,
                    viewModel = crearProductosViewModel
                )
            }
            composable("agregarProducto") {
                PantallaAgregarProductoScreen(
                    navController = navController,
                    appNavController = appNavController,
                    viewModel = crearProductosViewModel
                )
            }
            composable("listaChats") {
                //  pantalla general de mensajes como lista de chats
                PantallaListaChats(navController = navController,
                                    appNavController = appNavController,
                                    chatViewModel = chatViewModel)
            }

            composable("mensaje/{productoId}/{productoNombre}/{usuarioId}") { backStackEntry ->
                val productoId = backStackEntry.arguments?.getString("productoId")?.toLong() ?: 0L
                val productoNombre = backStackEntry.arguments?.getString("productoNombre") ?: ""
                val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toLong() ?: 0L

                pantallaMensaje(
                    navController = navController,
                    appNavController = appNavController,
                    productoId = productoId,
                    productoNombre = productoNombre,
                    chatViewModel = chatViewModel,
                    usuarioId = usuarioId // 👈 reemplaza con el id real del usuario logueado

                )
            }
        }

    }

}