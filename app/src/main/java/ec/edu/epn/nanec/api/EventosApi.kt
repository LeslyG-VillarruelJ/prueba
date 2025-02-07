package ec.edu.epn.nanec.api

import ec.edu.epn.nanec.model.Evento
import ec.edu.epn.nanec.model.Usuario
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventosApi {
    @GET("/")
    suspend fun obtenerEventos(): List<Evento>
    @GET("/{tipo}")
    suspend fun obtenerEventosByTipo(@Path("tipo") tipo: String): List<Evento>
    @POST("/registrar")
    suspend fun registrarUsuario(@Body usuario: Usuario )
}