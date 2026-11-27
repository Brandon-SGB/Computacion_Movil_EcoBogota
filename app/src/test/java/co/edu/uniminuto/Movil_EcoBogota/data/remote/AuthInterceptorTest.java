package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionStore;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

//Pruebas locales del interceptor de autenticación (A9, A10, A11 y A12, parte unitaria)
public class AuthInterceptorTest {

    //Dirección local solo para construir peticiones de prueba; no corresponde al backend
    private static final String URL_PRUEBA = "http://localhost/prueba";
    private static final String TOKEN_PRUEBA = "token-de-prueba";

    //A9: con token, la petición protegida sale con Authorization: Bearer <token>
    @Test
    public void peticionProtegida_conToken_anadeAuthorization() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        ChainFalsa chain = new ChainFalsa(peticion(false), 200);

        new AuthInterceptor(sesion).intercept(chain);

        assertEquals("Bearer " + TOKEN_PRUEBA,
                chain.peticionEnviada.header(AuthInterceptor.ENCABEZADO_AUTORIZACION));
    }

    //A9b: sin token, la petición sale sin Authorization
    @Test
    public void peticionProtegida_sinToken_noAnadeAuthorization() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(null);
        ChainFalsa chain = new ChainFalsa(peticion(false), 200);

        new AuthInterceptor(sesion).intercept(chain);

        assertNull(chain.peticionEnviada.header(AuthInterceptor.ENCABEZADO_AUTORIZACION));
    }

    //A10: con No-Auth sale sin Authorization y sin la marca No-Auth, aunque haya token
    @Test
    public void peticionPublica_conToken_noAnadeAuthorizationYRetiraMarca() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        ChainFalsa chain = new ChainFalsa(peticion(true), 200);

        new AuthInterceptor(sesion).intercept(chain);

        assertNull(chain.peticionEnviada.header(AuthInterceptor.ENCABEZADO_AUTORIZACION));
        assertNull(chain.peticionEnviada.header(AuthInterceptor.ENCABEZADO_SIN_AUTH));
    }

    //A11: un 401 en una petición protegida invalida la sesión una vez
    @Test
    public void respuesta401_enProtegida_invalidaSesionUnaVez() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        ChainFalsa chain = new ChainFalsa(peticion(false), 401);

        Response respuesta = new AuthInterceptor(sesion).intercept(chain);

        assertEquals(401, respuesta.code());
        assertEquals(1, sesion.llamadasInvalidar.get());
        assertFalse(sesion.hayToken());
    }

    //A11: un 401 en una petición pública (No-Auth) no invalida la sesión
    @Test
    public void respuesta401_enPublica_noInvalidaSesion() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        ChainFalsa chain = new ChainFalsa(peticion(true), 401);

        new AuthInterceptor(sesion).intercept(chain);

        assertEquals(0, sesion.llamadasInvalidar.get());
        assertTrue(sesion.hayToken());
    }

    //A11: varias respuestas 401 simultáneas producen una sola invalidación efectiva
    @Test
    public void variasRespuestas401Concurrentes_unaSolaInvalidacionEfectiva() throws Exception {
        final int hilos = 10;
        final SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        final AuthInterceptor interceptor = new AuthInterceptor(sesion);
        final CountDownLatch salida = new CountDownLatch(1);
        ExecutorService ejecutor = Executors.newFixedThreadPool(hilos);
        try {
            List<Future<Integer>> resultados = new ArrayList<>();
            for (int i = 0; i < hilos; i++) {
                resultados.add(ejecutor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        salida.await();
                        return interceptor.intercept(new ChainFalsa(peticion(false), 401)).code();
                    }
                }));
            }
            salida.countDown();
            for (Future<Integer> resultado : resultados) {
                assertEquals(Integer.valueOf(401), resultado.get(10, TimeUnit.SECONDS));
            }
        } finally {
            ejecutor.shutdownNow();
        }

        assertEquals(1, sesion.invalidacionesEfectivas.get());
        assertFalse(sesion.hayToken());
    }

    //A12: un 403 no invalida la sesión y el token se conserva
    @Test
    public void respuesta403_noInvalidaSesion() throws IOException {
        SessionStoreFalso sesion = new SessionStoreFalso(TOKEN_PRUEBA);
        ChainFalsa chain = new ChainFalsa(peticion(false), 403);

        Response respuesta = new AuthInterceptor(sesion).intercept(chain);

        assertEquals(403, respuesta.code());
        assertEquals(0, sesion.llamadasInvalidar.get());
        assertEquals(TOKEN_PRUEBA, sesion.obtenerToken());
    }

    //Método auxiliar para construir una petición protegida o pública
    private static Request peticion(boolean publica) {
        Request.Builder builder = new Request.Builder().url(URL_PRUEBA);
        if (publica) {
            builder.header(AuthInterceptor.ENCABEZADO_SIN_AUTH, "true");
        }
        return builder.build();
    }

    //SessionStore falso que replica el comportamiento sincronizado del SessionManager
    private static class SessionStoreFalso implements SessionStore {
        private String token;
        final AtomicInteger llamadasInvalidar = new AtomicInteger();
        final AtomicInteger invalidacionesEfectivas = new AtomicInteger();

        SessionStoreFalso(String token) {
            this.token = token;
        }

        @Override
        public synchronized String obtenerToken() {
            return token;
        }

        @Override
        public synchronized boolean hayToken() {
            return token != null && !token.isEmpty();
        }

        @Override
        public synchronized boolean invalidarSesion() {
            llamadasInvalidar.incrementAndGet();
            if (token == null) {
                return false;
            }
            token = null;
            invalidacionesEfectivas.incrementAndGet();
            return true;
        }
    }

    //Interceptor.Chain falso: guarda la petición enviada y responde con el código indicado
    private static class ChainFalsa implements Interceptor.Chain {
        private final Request peticionOriginal;
        private final int codigo;
        Request peticionEnviada;

        ChainFalsa(Request peticionOriginal, int codigo) {
            this.peticionOriginal = peticionOriginal;
            this.codigo = codigo;
        }

        @Override
        public Request request() {
            return peticionOriginal;
        }

        @Override
        public Response proceed(Request request) {
            peticionEnviada = request;
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(codigo)
                    .message("prueba")
                    .build();
        }

        @Override
        public Connection connection() {
            return null;
        }

        @Override
        public Call call() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int connectTimeoutMillis() {
            return 0;
        }

        @Override
        public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
            return this;
        }

        @Override
        public int readTimeoutMillis() {
            return 0;
        }

        @Override
        public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
            return this;
        }

        @Override
        public int writeTimeoutMillis() {
            return 0;
        }

        @Override
        public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
            return this;
        }
    }
}
