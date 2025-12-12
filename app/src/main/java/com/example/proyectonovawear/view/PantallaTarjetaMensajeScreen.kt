package com.example.proyectonovawear.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.proyectonovawear.model.Mensaje

import java.text.SimpleDateFormat
import java.util.*

@Composable
fun tarjetaMensaje(mensaje: Mensaje) {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val fechaFormateada = mensaje.fecha?.let {
        try {
            formatter.format(Date(it))
        } catch (e: Exception) {
            ""
        }
    } ?: ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start
    ) {
        OutlinedCard(
            modifier = Modifier
                .padding(4.dp)
                .widthIn(max = 250.dp), // 👈 burbuja con ancho máximo
            colors = CardDefaults.cardColors(
                containerColor = if (mensaje.esMio) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = mensaje.contenido.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = fechaFormateada,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = if (mensaje.esMio) TextAlign.End else TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}