package com.example.proyectonovawear.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.example.proyectonovawear.model.Mensaje

@Composable
fun tarjetaMensaje(mensaje: Mensaje) {
    /// arrangement pone el mensaje a la derecha (End) si esMio es true y viceversa
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start
    ) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(0.8f),
            // modifica el color de el mensaje segun si es mio o no
            colors = CardDefaults.cardColors(
                containerColor = if (mensaje.esMio){
                    MaterialTheme.colorScheme.primaryContainer
                }else{
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = mensaje.contenido,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(4.dp))


            }
        }
    }

}