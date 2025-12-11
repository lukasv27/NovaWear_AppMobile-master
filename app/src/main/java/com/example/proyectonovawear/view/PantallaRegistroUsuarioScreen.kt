import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectonovawear.controller.RegistroUsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun registroUsuario(appNavController: NavController) {
    val viewModel: RegistroUsuarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    if (state.registroExitoso) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Registro exitoso") },
            text = { Text("El usuario ha sido registrado correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarEstado()
                        appNavController.navigate("login")
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro de usuario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    Button(
                        onClick = { appNavController.navigate("login") },
                        enabled = !state.isLoading
                    ) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = state.isLoading
            )
            OutlinedTextField(
                value = state.apellido,
                onValueChange = viewModel::onApellidoChange,
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = state.isLoading
            )
            OutlinedTextField(
                value = state.correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = state.isLoading
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                readOnly = state.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (state.isLoading) {
                LinearProgressIndicator(
                    progress = state.progreso,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = { viewModel.registrarUsuario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = !state.isLoading
            ) {
                Text("Guardar Registro")
            }

            state.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
