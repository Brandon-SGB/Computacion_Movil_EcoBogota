package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.edu.uniminuto.Movil_EcoBogota.MainActivity;
import co.edu.uniminuto.Movil_EcoBogota.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

//Prueba en el dispositivo: campos obligatorios de los formularios de solicitud (HU-11) y reporte (HU-06)
@RunWith(AndroidJUnit4.class)
public class FormulariosTest {

    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Test
    public void nuevaSolicitudVacia_muestraCantidadYUbicacionObligatorias() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_solicitudes)).perform(click());
            onView(withId(R.id.btnNuevaSolicitud)).perform(click());
            onView(withId(R.id.btnEnviarSolicitud)).perform(scrollTo(), click());
            onView(withId(R.id.etCantidad)).check(matches(hasErrorText(context.getString(R.string.error_cantidad_obligatoria))));
            onView(withId(R.id.etDireccion)).check(matches(hasErrorText(context.getString(R.string.error_ubicacion_obligatoria))));
        }
    }

    @Test
    public void nuevoReporteVacio_muestraDescripcionYUbicacionObligatorias() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_reportes)).perform(click());
            onView(withId(R.id.btnNuevoReporte)).perform(click());
            onView(withId(R.id.btnEnviarReporte)).perform(scrollTo(), click());
            onView(withId(R.id.etDescripcion)).perform(scrollTo())
                    .check(matches(hasErrorText(context.getString(R.string.error_descripcion_obligatoria))));
            onView(withId(R.id.etDireccion)).perform(scrollTo())
                    .check(matches(hasErrorText(context.getString(R.string.error_ubicacion_obligatoria))));
        }
    }
}
