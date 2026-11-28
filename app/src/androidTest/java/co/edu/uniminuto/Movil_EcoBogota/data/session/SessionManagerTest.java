package co.edu.uniminuto.Movil_EcoBogota.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//Prueba en el dispositivo: el token se guarda cifrado (A5)
@RunWith(AndroidJUnit4.class)
public class SessionManagerTest {

    private static final String VALOR_PRUEBA = "valor-prueba-A5";

    @Test
    public void token_seGuardaCifradoYSeInvalidaUnaSolaVez() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sesion = SessionManager.getInstance(context);
        try {
            sesion.guardarToken(VALOR_PRUEBA);
            assertEquals(VALOR_PRUEBA, sesion.obtenerToken());
            assertTrue(sesion.hayToken());

            //El archivo crudo no debe contener el valor en ninguna clave ni valor
            SharedPreferences crudas = context.getSharedPreferences(
                    SessionManager.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            for (Map.Entry<String, ?> entrada : crudas.getAll().entrySet()) {
                assertFalse(entrada.getKey().contains(VALOR_PRUEBA));
                assertFalse(String.valueOf(entrada.getValue()).contains(VALOR_PRUEBA));
            }

            assertTrue(sesion.invalidarSesion());
            assertFalse(sesion.invalidarSesion());
            assertFalse(sesion.hayToken());
        } finally {
            //Se deja el almacenamiento sin token
            sesion.invalidarSesion();
        }
    }

    //P-04: diez invalidaciones simultáneas producen una sola invalidación y un solo aviso a la interfaz
    @Test
    public void invalidacionesConcurrentes_unSoloAviso() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SessionManager sesion = SessionManager.getInstance(context);
        AtomicInteger avisos = new AtomicInteger();
        Observer<Boolean> observador = invalidada -> {
            if (Boolean.TRUE.equals(invalidada)) {
                avisos.incrementAndGet();
            }
        };
        sesion.limpiarAvisoSesion();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                () -> sesion.getSesionInvalidada().observeForever(observador));
        ExecutorService ejecutor = Executors.newFixedThreadPool(10);
        try {
            sesion.guardarToken(VALOR_PRUEBA);
            CountDownLatch salida = new CountDownLatch(1);
            List<Future<Boolean>> resultados = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                resultados.add(ejecutor.submit(() -> {
                    salida.await();
                    return sesion.invalidarSesion();
                }));
            }
            salida.countDown();
            int efectivas = 0;
            for (Future<Boolean> resultado : resultados) {
                if (resultado.get(10, TimeUnit.SECONDS)) {
                    efectivas++;
                }
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();

            assertEquals(1, efectivas);
            assertEquals(1, avisos.get());
        } finally {
            ejecutor.shutdownNow();
            InstrumentationRegistry.getInstrumentation().runOnMainSync(
                    () -> sesion.getSesionInvalidada().removeObserver(observador));
            sesion.invalidarSesion();
            sesion.limpiarAvisoSesion();
        }
    }
}
