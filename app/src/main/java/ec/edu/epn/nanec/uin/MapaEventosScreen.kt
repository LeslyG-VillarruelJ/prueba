package ec.edu.epn.nanec.uin

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastMinByOrNull
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ec.edu.epn.nanec.model.Evento
import ec.edu.epn.nanec.viewmodel.EventosViewModel
import ec.edu.epn.nanec.viewmodel.UsuarioViewModel
import kotlin.collections.MutableList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun MapaEventosScreen(eventosViewModel: EventosViewModel, usuarioViewModel: UsuarioViewModel) {
    val eventos = eventosViewModel.eventos.collectAsState().value
    //val ubicacionActual = remember { mutableStateOf<LatLng?>(null) }
    val ubicacionPar = usuarioViewModel.ubicacion.collectAsState().value
    val ubicacionActual = ubicacionPar?.let { LatLng(it.first, it.second) }

    val mapView = rememberMapViewWithLifecycle()
// mapview lo asignamos como el valor que retorina la funcion rememberMapViewWithLifecycle al composable
    AndroidView(factory = { mapView }) { mapView ->
        mapView.getMapAsync { googleMap -> // crea un objeto de tipo googlemap y lo asocia al mapa (getMapAsync)
            configurarMapa(googleMap, eventos, ubicacionActual)
        }
    }

    // Obtener ubicación actual (simulación para este ejemplo, se puede conectar con FusedLocationProvider)
    // utilizamos como el use Effect para que se ejecute solo una vez cuando se crea el composable - dado que el unit es vaío
    // dado que unit significa vacío
    /*LaunchedEffect(Unit) {
        // Ubicación ficticia
        ubicacionActual.value = LatLng(-0.180653, -78.467838) // Quito, Ecuador
    }*/
}

// se hacen 3 cosas
// 1. configurar el mapa
// 2. añadir marcas con addmarker
// 3. Calcula la distancia entre dos puntos
// 4. El MapView al composable - es la vista del mapa - asocia este map view al composable

// configuración Mapa tipo de mapa, controles de zoom, centrar la cámara en la ubicación actual
fun configurarMapa(googleMap: GoogleMap, eventos: List<Evento>, ubicacionActual: LatLng?) {

    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    googleMap.uiSettings.isZoomControlsEnabled = true

    // Centrar la cámara en la ubicación actual
    ubicacionActual?.let {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 12f))
        val circleOptions = CircleOptions()
            .center(it) // Centro del círculo en la ubicación actual
            .radius(100.0) // Radio en metros (1 km en este caso)
            .strokeWidth(2f) // Ancho del borde
            .strokeColor(0xFF0000FF.toInt()) // Color del borde (azul)
            .fillColor(0x220000FF) // Color de relleno con transparencia (azul claro)
        googleMap.addCircle(circleOptions)
    }
    val eventoCercano = eventos.minByOrNull { it ->
        calcularDistancia(ubicacionActual, LatLng(it.latitud, it.longitud))
    }
    Log.d("Eventos", "Número de eventos: ${eventoCercano?.nombre}")

    // Agregar marcadores para cada evento
    eventos.forEach { evento ->
        val posicion = LatLng(evento.latitud, evento.longitud) // Asegúrate de tener estos campos en el modelo Evento
        val distance = calcularDistancia(ubicacionActual, posicion)
        val distances: MutableList<Float> = mutableListOf()
        distances.add(distance)

        googleMap.addMarker(
            MarkerOptions()
                .position(posicion)
                .title(evento.nombre)
                .snippet("Evento: ${evento.tipo} Distancia: ${calcularDistancia(ubicacionActual, posicion)} km")
        )
    }
}

// Función para calcular la distancia entre dos puntos
fun calcularDistancia(ubicacionActual: LatLng?, destino: LatLng): Float {
    if (ubicacionActual == null) return 0f
    val resultados = FloatArray(1)
    Location.distanceBetween(
        ubicacionActual.latitude, ubicacionActual.longitude,
        destino.latitude, destino.longitude,
        resultados
    )
    return resultados[0] / 1000 // Convertir a kilómetros
}

// lo que se retorna ya está asociado al composable
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context as Context)
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}

