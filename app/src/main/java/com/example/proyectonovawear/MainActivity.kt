package com.example.proyectonovawear


import PantallaLogin
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.proyectonovawear.model.Productos
import com.example.proyectonovawear.model.RegistroDTO
import com.example.proyectonovawear.network.RetrofitProvider
import com.example.proyectonovawear.ui.theme.ProyectoNovaWearTheme
import kotlinx.coroutines.launch
import android.util.Log
import android.util.Log.e
import androidx.compose.ui.platform.LocalContext
import com.example.proyectonovawear.api.ApiService
import com.example.proyectonovawear.db.AppDatabase
import com.example.proyectonovawear.model.LoginDTO
import com.example.proyectonovawear.model.UsuarioLocal


import com.example.proyectonovawear.view.barraInferior
import com.example.proyectonovawear.view.pantallaAgregar
import com.example.proyectonovawear.view.pantallaMensaje

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import registroUsuario


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoNovaWearTheme {
                MyApp()
            }
        }
    }
}
// a las paginas que tienen barra inferior las quite de aqui y las puse en el composable de barra inferior
@Composable
fun MyApp() {
    val appNavController = rememberNavController()

    // Lista de productos compartida
    val listaProductos = remember { mutableStateListOf<Productos>() }

    NavHost(navController = appNavController, startDestination = "login") {
        composable("login") {
            PantallaLogin(appNavController = appNavController)
        }
        composable("barraInferior") {
            barraInferior(appNavController = appNavController, listaProductos = listaProductos)
        }
        composable("registroUsuario") {
            registroUsuario(appNavController = appNavController)
        }

    }

}





