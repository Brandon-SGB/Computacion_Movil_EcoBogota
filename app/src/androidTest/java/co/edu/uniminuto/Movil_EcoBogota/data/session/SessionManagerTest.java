package co.edu.uniminuto.Movil_EcoBogota.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

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
}
