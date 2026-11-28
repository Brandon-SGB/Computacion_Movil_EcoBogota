package co.edu.uniminuto.Movil_EcoBogota.ui;

import org.junit.Test;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static org.junit.Assert.assertEquals;

//Pruebas locales de las validaciones de la nueva solicitud (HU-11)
public class NuevaSolicitudViewModelTest {

    //CP-11-03: cantidad vacía
    @Test
    public void cantidadVacia_esObligatoria() {
        assertEquals(R.string.error_cantidad_obligatoria, NuevaSolicitudViewModel.validarCantidad(""));
        assertEquals(R.string.error_cantidad_obligatoria, NuevaSolicitudViewModel.validarCantidad("   "));
        assertEquals(R.string.error_cantidad_obligatoria, NuevaSolicitudViewModel.validarCantidad(null));
    }

    //CP-11-03: cantidad no numérica
    @Test
    public void cantidadNoNumerica_esInvalida() {
        assertEquals(R.string.error_cantidad_numerica, NuevaSolicitudViewModel.validarCantidad("diez"));
        assertEquals(R.string.error_cantidad_numerica, NuevaSolicitudViewModel.validarCantidad("NaN"));
    }

    //CP-11-03: cantidad 0
    @Test
    public void cantidadCero_esInvalida() {
        assertEquals(R.string.error_cantidad_mayor_cero, NuevaSolicitudViewModel.validarCantidad("0"));
    }

    //CP-11-03: cantidad negativa
    @Test
    public void cantidadNegativa_esInvalida() {
        assertEquals(R.string.error_cantidad_mayor_cero, NuevaSolicitudViewModel.validarCantidad("-3"));
    }

    //CP-11-03: cantidad válida
    @Test
    public void cantidadPositiva_esValida() {
        assertEquals(FormularioViewModel.VALIDO, NuevaSolicitudViewModel.validarCantidad("5"));
        assertEquals(FormularioViewModel.VALIDO, NuevaSolicitudViewModel.validarCantidad(" 2.5 "));
    }

    //CP-11-04: sin GPS ni dirección la ubicación es obligatoria
    @Test
    public void sinGpsNiDireccion_ubicacionObligatoria() {
        assertEquals(R.string.error_ubicacion_obligatoria, FormularioViewModel.validarUbicacion(false, ""));
        assertEquals(R.string.error_ubicacion_obligatoria, FormularioViewModel.validarUbicacion(false, "  "));
        assertEquals(R.string.error_ubicacion_obligatoria, FormularioViewModel.validarUbicacion(false, null));
    }

    //CP-11-04: con dirección manual o con GPS la ubicación es válida
    @Test
    public void conDireccionOGps_ubicacionValida() {
        assertEquals(FormularioViewModel.VALIDO, FormularioViewModel.validarUbicacion(false, "Calle 1"));
        assertEquals(FormularioViewModel.VALIDO, FormularioViewModel.validarUbicacion(true, ""));
    }

    //CP-11-08: sin conexión la solicitud no se envía
    @Test
    public void mensajeEnvio_segunConexion() {
        assertEquals(R.string.solicitud_no_enviada_sin_conexion,
                FormularioViewModel.obtenerMensajeEnvio(false, R.string.solicitud_no_enviada_sin_conexion));
        assertEquals(R.string.envio_pendiente_backend,
                FormularioViewModel.obtenerMensajeEnvio(true, R.string.solicitud_no_enviada_sin_conexion));
    }
}
