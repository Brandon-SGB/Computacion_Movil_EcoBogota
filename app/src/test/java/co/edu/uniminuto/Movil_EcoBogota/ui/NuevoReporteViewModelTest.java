package co.edu.uniminuto.Movil_EcoBogota.ui;

import org.junit.Test;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static org.junit.Assert.assertEquals;

//Pruebas locales de las validaciones del nuevo reporte (HU-06)
public class NuevoReporteViewModelTest {

    //CP-06-03: descripción vacía
    @Test
    public void descripcionVacia_esObligatoria() {
        assertEquals(R.string.error_descripcion_obligatoria, NuevoReporteViewModel.validarDescripcion(""));
        assertEquals(R.string.error_descripcion_obligatoria, NuevoReporteViewModel.validarDescripcion("   "));
        assertEquals(R.string.error_descripcion_obligatoria, NuevoReporteViewModel.validarDescripcion(null));
    }

    //CP-06-03: descripción escrita
    @Test
    public void descripcionEscrita_esValida() {
        assertEquals(FormularioViewModel.VALIDO, NuevoReporteViewModel.validarDescripcion("Basura en el andén"));
    }

    //CP-06-05/06: ubicación por GPS o manual
    @Test
    public void ubicacion_gpsOManual() {
        assertEquals(R.string.error_ubicacion_obligatoria, FormularioViewModel.validarUbicacion(false, ""));
        assertEquals(FormularioViewModel.VALIDO, FormularioViewModel.validarUbicacion(false, "Carrera 7"));
        assertEquals(FormularioViewModel.VALIDO, FormularioViewModel.validarUbicacion(true, null));
    }

    //Sin conexión el reporte no se envía
    @Test
    public void mensajeEnvio_sinConexion() {
        assertEquals(R.string.reporte_no_enviado_sin_conexion,
                FormularioViewModel.obtenerMensajeEnvio(false, R.string.reporte_no_enviado_sin_conexion));
    }
}
