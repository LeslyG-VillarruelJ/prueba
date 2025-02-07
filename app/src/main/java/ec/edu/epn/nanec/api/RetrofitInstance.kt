package ec.edu.epn.nanec.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://172.29.63.25:8000"
    //private const val BASE_URL = "http://192.168.100.130:8000"

    val api: EventosApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventosApi::class.java)
    }
}