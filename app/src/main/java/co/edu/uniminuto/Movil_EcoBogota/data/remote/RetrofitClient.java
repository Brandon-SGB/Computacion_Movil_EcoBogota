package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import co.edu.uniminuto.Movil_EcoBogota.BuildConfig;
import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton de Retrofit (patrón de la guía) con OkHttp, timeouts y token JWT.
 * Los *ApiService de cada módulo se crean con {@link #crearServicio(Context, Class)}.
 */
public final class RetrofitClient {

    // 1. Atributos
    private static volatile Retrofit instancia;

    private RetrofitClient() {
    }

    // 2. Acceso a la instancia única
    public static Retrofit getInstancia(Context context) {
        if (instancia == null) {
            synchronized (RetrofitClient.class) {
                if (instancia == null) {
                    instancia = construir(context.getApplicationContext());
                }
            }
        }
        return instancia;
    }

    public static <T> T crearServicio(Context context, Class<T> servicio) {
        return getInstancia(context).create(servicio);
    }

    // 3. Construcción
    private static Retrofit construir(Context appContext) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient cliente = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.TIMEOUT_CONEXION_SEGUNDOS, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT_LECTURA_SEGUNDOS, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT_LECTURA_SEGUNDOS, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(new SessionManager(appContext)))
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
