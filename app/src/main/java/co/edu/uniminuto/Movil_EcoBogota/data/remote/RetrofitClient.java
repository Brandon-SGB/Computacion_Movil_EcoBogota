package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Cliente Retrofit único para las peticiones al backend
public class RetrofitClient {

    //Pendiente del contrato del backend (G2). No usar URL inventada.
    private static final String BASE_URL = "";

    //Instancia única de Retrofit, se crea la primera vez que se solicita
    private static Retrofit retrofit;

    private RetrofitClient() {
    }

    //Método que devuelve la instancia única de Retrofit
    public static synchronized Retrofit getRetrofit() {
        if (BASE_URL.isEmpty()) {
            throw new IllegalStateException(
                    "BASE_URL no está definida: pendiente del contrato del backend (G2)");
        }
        if (retrofit == null) {
            OkHttpClient cliente = new OkHttpClient.Builder()
                    .addInterceptor(crearLoggingInterceptor())
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(cliente)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    //Método que crea el interceptor de registro; siempre en nivel BASIC (sin encabezados ni cuerpos)
    static HttpLoggingInterceptor crearLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return logging;
    }
}
