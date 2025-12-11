package com.example.proyectonovawear.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectonovawear.controller.CrearProductosViewModel
import com.example.proyectonovawear.model.Productos

// pantalla para agregar tus productos y para ver productos que ya publicaste
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pantallaAgregar(navController: NavController, appNavController: NavController, viewModel: CrearProductosViewModel= viewModel()) {
    val state by viewModel.state.collectAsState()
    val listaProductos = state.list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones") }, // igual que en PantallaLogin
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9E9E9E),
                    titleContentColor = Color.Black
                ),
                actions = {
                    Button(
                        onClick = {
                            appNavController.navigate("login") {
                                popUpTo("barraInferior") { inclusive = true }
                            }
                        }
                    ) {
                        Text("CERRAR SESIÓN")
                    }

                }

            )

        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregarProducto") }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listaProductos) { producto ->
                OutlinedCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        producto.imagenUri?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text("Nombre: ${producto.nombre}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Text("Precio: ${producto.precio}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Text("Talla: ${producto.talla}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Text("Descripción: ${producto.descripcion}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)


                    }
                }
            }
        }
    }
}