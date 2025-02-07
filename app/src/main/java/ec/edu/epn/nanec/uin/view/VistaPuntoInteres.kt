package ec.edu.epn.nanec.uin.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.animation.core.tween

@Composable
fun VistaPuntoInteres(
    nombre: String,
    descripcion: String,
    imagenUrl: String?,
    fecha: String,
    tipo: String,
    esActualizado: Boolean // Recibimos si el evento ha sido actualizado
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(tween(durationMillis = 300)) // Animación de cambio de tamaño
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Imagen del evento
            AsyncImage(
                model = imagenUrl,
                contentDescription = "Imagen de $nombre",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fecha: $fecha",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tipo: $tipo",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Animación de resaltar el evento actualizado
            if (esActualizado) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "¡Actualizado!",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
