package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Agrega el encabezado "Authorization: Bearer &lt;token&gt;" cuando hay sesión iniciada.
 */
public class AuthInterceptor implements Interceptor {

    // 1. Atributos
    private static final String ENCABEZADO = "Authorization";
    private static final String PREFIJO = "Bearer ";
    private final SessionManager sessionManager;

    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // 2. Interceptación
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }
        Request conToken = original.newBuilder()
                .header(ENCABEZADO, PREFIJO + token)
                .build();
        return chain.proceed(conToken);
    }
}
