package co.edu.uniminuto.Movil_EcoBogota.ui;

import org.junit.Test;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static org.junit.Assert.assertEquals;

//Pruebas locales de la lógica de administración (HU-08, HU-13)
public class GestionViewModelTest {

    //Sin conexión la actualización de estado no se envía
    @Test
    public void actualizarEstado_sinConexion_informaSinInternet() {
        assertEquals(R.string.conexion_no_disponible, GestionViewModel.obtenerMensajeActualizacion(false));
    }

    //Con conexión la actualización espera el servicio del backend
    @Test
    public void actualizarEstado_conConexion_informaServicioPendiente() {
        assertEquals(R.string.envio_pendiente_backend, GestionViewModel.obtenerMensajeActualizacion(true));
    }

    //Un 403 se informa como falta de permisos
    @Test
    public void respuesta403_informaSinPermisos() {
        assertEquals(R.string.error_sin_permisos, GestionViewModel.obtenerMensajeError(403));
    }

    //Otros errores se informan de forma general
    @Test
    public void otraRespuesta_informaErrorGeneral() {
        assertEquals(R.string.error_operacion, GestionViewModel.obtenerMensajeError(500));
    }

    //Los listados de administración comprueban la conexión antes de consultar
    @Test
    public void consultaAdministracion_sinConexion_informaSinInternet() {
        assertEquals(R.string.conexion_no_disponible, ListadoViewModel.obtenerMensajeConsulta(false));
    }
}
