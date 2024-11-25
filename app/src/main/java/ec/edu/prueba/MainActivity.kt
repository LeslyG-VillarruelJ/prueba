package ec.edu.prueba

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.prueba.ui.theme.PruebaTheme

class MainActivity : ComponentActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        databaseHelper = DatabaseHelper(this)
        insertarDatosIniciales()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val eventos = obtenerEventosDesdeBaseDeDatos(databaseHelper) // Convierte el cursor a lista
        setContent {
            ListaDeEventos(eventos) // Pasa la lista de eventos
        }
    }

    private fun insertarDatosIniciales() {
        // Verifica si ya hay datos en la base de datos para evitar duplicados
        val cursor = databaseHelper.getAllEvents()
        if (cursor.count == 0) {
            databaseHelper.insertEvents("Trail en el Mirador de la Perdiz", "Mirador de la Perdiz", "31/10/2024", 30)
            databaseHelper.insertEvents("Concurso de la Mejor Colada Morada", "Estadio de San José", "31/10/2024", 100)
            databaseHelper.insertEvents("Conmemoración Día de los Difuntos", "Cementerio de San José", "02/11/2024", 30)
            databaseHelper.insertEvents("Concurso de Guagua de Pan", "Estadio de San José", "03/11/2024", 100)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PruebaTheme {
        Greeting("Android")
    }
}

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "Eventos.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "eventos"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "nombre"
        const val COLUMN_ADDRESS = "lugar"
        const val COLUMN_DATE = "fecha"
        const val COLUMN_ASSISTENTS = "numero_sistentes"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_ADDRESS TEXT NOT NULL,
            $COLUMN_DATE DATE NOT NULL,
            $COLUMN_ASSISTENTS INTEGER NOT NULL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertEvents(nombre: String, lugar: String, fecha: String, numero_asistentes: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, nombre)
            put(COLUMN_ADDRESS, lugar)
            put(COLUMN_DATE, fecha)
            put(COLUMN_ASSISTENTS, numero_asistentes)
        }
        return db.insert(TABLE_NAME, null, values)
    }
    fun getAllEvents(): Cursor {
        val db = this.readableDatabase
        val projection = arrayOf(COLUMN_NAME, COLUMN_ADDRESS, COLUMN_DATE, COLUMN_ASSISTENTS)
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }
}

data class Evento(
    val nombre: String,
    val lugar: String,
    val fecha: String,
    val asistentes: Int
)

fun obtenerEventosDesdeBaseDeDatos(databaseHelper: DatabaseHelper): List<Evento> {
    val eventos = mutableListOf<Evento>()
    val cursor = databaseHelper.getAllEvents()
    cursor?.use {
        while (it.moveToNext()) {
            val nombreIndex = it.getColumnIndex(DatabaseHelper.COLUMN_NAME)
            val direccionIndex = it.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS)
            val fechaIndex = it.getColumnIndex(DatabaseHelper.COLUMN_DATE)
            val asistentesIndex = it.getColumnIndex(DatabaseHelper.COLUMN_ASSISTENTS)
            if (nombreIndex != -1 && direccionIndex != -1 && fechaIndex != -1 && asistentesIndex != -1) {
                val nombre = it.getString(nombreIndex)
                val lugar = it.getString(direccionIndex)
                val fecha = it.getString(fechaIndex)
                val asistentes = it.getInt(asistentesIndex)
                eventos.add(Evento(nombre, lugar, fecha, asistentes))
            }
        }
    }
    return eventos
}

@Composable
fun EventoCard(evento: Evento) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row( // Usamos un Row para organizar la imagen y el contenido horizontalmente
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Carga de la imagen del evento a la izquierda
            // Aquí puedes usar un recurso de imagen o una imagen predeterminada
            val imagenId = when (evento.nombre) {
                "Trail en el Mirador de la Perdiz" -> R.drawable.img
                "Concurso de la Mejor Colada Morada" -> R.drawable.img_1
                "Conmemoración Día de los Difuntos" -> R.drawable.img_2
                "Concurso de Guagua de Pan" -> R.drawable.img_3
                else -> R.drawable.img// Imagen por defecto si no coincide
            }

            Image(
                painter = painterResource(id = imagenId), // Cambiado por la imagen seleccionada
                contentDescription = "Imagen del evento",
                modifier = Modifier
                    .size(60.dp) // Tamaño de la imagen
                    .padding(end = 16.dp) // Espacio entre la imagen y el contenido
            )

            // Contenido del evento a la derecha
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Para asegurarnos de que el texto ocupe el espacio restante
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = evento.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evento.lugar,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = evento.fecha,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${evento.asistentes} asistentes",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}


@Composable
fun ListaDeEventos(eventos: List<Evento>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Espaciado general
    ) {
        // Título principal
        Text(
            text = "Eventos en 'San José' por el feriado de noviembre",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Espacio entre el título y las tarjetas
        )

        // Lista de eventos en formato lista vertical
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(eventos.size) { index ->  // Itera sobre cada evento
                EventoCard(evento = eventos[index])
            }
        }
    }
}

