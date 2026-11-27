package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import org.junit.Test;

import okhttp3.logging.HttpLoggingInterceptor;

import static org.junit.Assert.assertEquals;

//Pruebas locales del cliente Retrofit
public class RetrofitClientTest {

    //El interceptor de registro debe quedar en nivel BASIC
    @Test
    public void interceptorDeRegistro_tieneNivelBasic() {
        HttpLoggingInterceptor logging = RetrofitClient.crearLoggingInterceptor();
        assertEquals(HttpLoggingInterceptor.Level.BASIC, logging.getLevel());
    }

    //Con BASE_URL vacía no se debe poder construir Retrofit
    @Test(expected = IllegalStateException.class)
    public void obtenerRetrofit_conBaseUrlVacia_lanzaExcepcion() {
        RetrofitClient.getRetrofit();
    }
}
