package ec.edu.epn.nanec.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.epn.nanec.api.RetrofitInstance
import ec.edu.epn.nanec.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class EventosViewModel : ViewModel() {
    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos

    private lateinit var socket: Socket

    init {
        cargarEventos()
        iniciarSocketIO()
    }

    private fun cargarEventos() {
        viewModelScope.launch {
            try {
                val eventosApi = RetrofitInstance.api
                val eventosRecibidos = eventosApi.obtenerEventos()
                _eventos.value = eventosRecibidos
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error desconocido: ${e.message}")
            }
        }
    }

    private fun iniciarSocketIO() {
        try {
            // Configuración de conexión al servidor de Socket.IO
            socket = IO.socket("http://172.29.63.25:8000")  // Ajusta la IP si es necesario

            // Escucha de eventos
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("Socket.IO", "Conectado al servidor")
            }

            socket.on("eventoActualizado") { args ->
                Log.d("Socket.IO", "Evento actualizado recibido: $args") // Este Log debería imprimirse cuando el servidor emita el evento
                if (args.isNotEmpty()) {
                    val eventoJson = args[0] as JSONObject
                    val evento = parsearEvento(eventoJson)
                    actualizarEventoEnLista(evento)
                }
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d("Socket.IO", "Desconectado del servidor")
            }

            socket.connect()
        } catch (e: Exception) {
            Log.e("Socket.IO", "Error al conectar: ${e.message}")
        }
    }

    private fun parsearEvento(eventoJson: JSONObject): Evento {
        val gson = com.google.gson.Gson()
        return gson.fromJson(eventoJson.toString(), Evento::class.java)
    }

    private fun actualizarEventoEnLista(eventoActualizado: Evento) {
        viewModelScope.launch {
            // Actualizamos el evento en la lista
            _eventos.value = _eventos.value.map {
                if (it._id == eventoActualizado._id) {
                    it.copy(
                        nombre = eventoActualizado.nombre,
                        ubicacion = eventoActualizado.ubicacion,
                        descripcion = eventoActualizado.descripcion,
                        tipo = eventoActualizado.tipo,
                        fecha = eventoActualizado.fecha,
                        imagenUrl = eventoActualizado.imagenUrl,
                        latitud = eventoActualizado.latitud,
                        longitud = eventoActualizado.longitud,
                        esActualizado = true // Indicamos que este evento ha sido actualizado
                    )
                } else {
                    it
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::socket.isInitialized) {
            socket.disconnect()
            socket.close()
        }
    }
}
