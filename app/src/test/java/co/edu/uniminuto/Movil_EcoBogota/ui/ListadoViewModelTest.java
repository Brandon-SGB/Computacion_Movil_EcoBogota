package co.edu.uniminuto.Movil_EcoBogota.ui;

import org.junit.Test;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static org.junit.Assert.assertEquals;

//Pruebas locales de la lógica de los listados
public class ListadoViewModelTest {

    //Sin conexión se informa la falta de internet antes que cualquier otra cosa
    @Test
    public void mensajeConsulta_sinConexion_informaSinInternet() {
        assertEquals(R.string.conexion_no_disponible, ListadoViewModel.obtenerMensajeConsulta(false));
    }

    //Con conexión se informa que la consulta espera el servicio del backend
    @Test
    public void mensajeConsulta_conConexion_informaServicioPendiente() {
        assertEquals(R.string.consulta_pendiente_backend, ListadoViewModel.obtenerMensajeConsulta(true));
    }
}
