package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

//Prueba en el dispositivo: validación de la pantalla de inicio de sesión (HU-02, A8)
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void camposVacios_muestraCorreoYContrasenaObligatorios() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.btnIngresar)).perform(click());
            onView(withId(R.id.etCorreo)).check(matches(hasErrorText(context.getString(R.string.error_correo_obligatorio))));
            onView(withId(R.id.etContrasena)).check(matches(hasErrorText(context.getString(R.string.error_contrasena_obligatoria))));
        }
    }

    @Test
    public void correoSinFormato_muestraCorreoInvalido() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.etCorreo)).perform(replaceText("usuario"));
            onView(withId(R.id.etContrasena)).perform(replaceText("clave"));
            closeSoftKeyboard();
            onView(withId(R.id.btnIngresar)).perform(click());
            onView(withId(R.id.etCorreo)).check(matches(hasErrorText(context.getString(R.string.error_correo_invalido))));
        }
    }

    //Botón provisional Volver al inicio: abre la pantalla principal con la navegación inferior
    @Test
    public void volverAlInicio_abrePantallaPrincipal() {
        try (ActivityScenario<LoginActivity> ignored = ActivityScenario.launch(LoginActivity.class)) {
            onView(withId(R.id.btnVolverInicio)).perform(click());
            onView(withId(R.id.bnvMenu)).check(matches(isDisplayed()));
        }
    }
}
