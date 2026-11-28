package co.edu.uniminuto.Movil_EcoBogota.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.edu.uniminuto.Movil_EcoBogota.MainActivity;
import co.edu.uniminuto.Movil_EcoBogota.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

//Prueba en el dispositivo: cada pestaña de la navegación inferior carga su pantalla y muestra su estado
@RunWith(AndroidJUnit4.class)
public class NavegacionInferiorTest {

    @Test
    public void pestanaReportes_cargaPantallaYEstado() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_reportes)).perform(click());
            onView(withId(R.id.tvTituloReportes)).check(matches(isDisplayed()));
            onView(withId(R.id.tvEstadoReportes)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pestanaSolicitudes_cargaPantallaYEstado() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_solicitudes)).perform(click());
            onView(withId(R.id.tvTituloSolicitudes)).check(matches(isDisplayed()));
            onView(withId(R.id.tvEstadoSolicitudes)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pestanaPuntos_cargaPantallaYEstado() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_puntos)).perform(click());
            onView(withId(R.id.tvTituloPuntos)).check(matches(isDisplayed()));
            onView(withId(R.id.tvEstadoPuntos)).check(matches(isDisplayed()));
        }
    }
}
