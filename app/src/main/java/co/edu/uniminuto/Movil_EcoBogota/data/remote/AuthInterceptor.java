package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionStore;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//Interceptor que añade el token JWT a las peticiones protegidas (D-JWT)
public class AuthInterceptor implements Interceptor {

    //Encabezado que marca una petición pública (por ejemplo, el inicio de sesión)
    static final String ENCABEZADO_SIN_AUTH = "No-Auth";
    static final String ENCABEZADO_AUTORIZACION = "Authorization";
    private static final int CODIGO_NO_AUTORIZADO = 401;

    //Fuente del token
    private final SessionStore sessionStore;

    public AuthInterceptor(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        //Petición pública: se retira la marca, no se añade token y un 401 no invalida la sesión
        if (original.header(ENCABEZADO_SIN_AUTH) != null) {
            Request publica = original.newBuilder()
                    .removeHeader(ENCABEZADO_SIN_AUTH)
                    .build();
            return chain.proceed(publica);
        }

        //Petición protegida: se añade el token si existe
        Request.Builder builder = original.newBuilder();
        String token = sessionStore.obtenerToken();
        if (token != null && !token.isEmpty()) {
            builder.header(ENCABEZADO_AUTORIZACION, "Bearer " + token);
        }
        Response respuesta = chain.proceed(builder.build());

        //Un 401 en una petición protegida indica sesión expirada; un 403 no modifica la sesión
        if (respuesta.code() == CODIGO_NO_AUTORIZADO) {
            sessionStore.invalidarSesion();
        }
        return respuesta;
    }
}
