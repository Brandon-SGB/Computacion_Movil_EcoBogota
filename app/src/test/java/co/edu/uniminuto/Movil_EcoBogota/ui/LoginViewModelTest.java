package co.edu.uniminuto.Movil_EcoBogota.ui;

import org.junit.Test;

import java.util.function.Predicate;

import co.edu.uniminuto.Movil_EcoBogota.R;

import static org.junit.Assert.assertEquals;

//Pruebas locales de las validaciones del inicio de sesión (HU-02, A8)
public class LoginViewModelTest {

    //Formato de correo para las pruebas locales; en la app se usa Patterns.EMAIL_ADDRESS
    private static final Predicate<String> FORMATO_PRUEBA = texto -> texto.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    @Test
    public void correoVacio_esObligatorio() {
        assertEquals(R.string.error_correo_obligatorio, LoginViewModel.validarCorreo("", FORMATO_PRUEBA));
        assertEquals(R.string.error_correo_obligatorio, LoginViewModel.validarCorreo("  ", FORMATO_PRUEBA));
        assertEquals(R.string.error_correo_obligatorio, LoginViewModel.validarCorreo(null, FORMATO_PRUEBA));
    }

    @Test
    public void correoSinFormato_esInvalido() {
        assertEquals(R.string.error_correo_invalido, LoginViewModel.validarCorreo("usuario", FORMATO_PRUEBA));
    }

    @Test
    public void correoConFormato_esValido() {
        assertEquals(FormularioViewModel.VALIDO, LoginViewModel.validarCorreo("usuario@dominio.co", FORMATO_PRUEBA));
    }

    @Test
    public void contrasenaVacia_esObligatoria() {
        assertEquals(R.string.error_contrasena_obligatoria, LoginViewModel.validarContrasena(""));
        assertEquals(R.string.error_contrasena_obligatoria, LoginViewModel.validarContrasena(null));
    }

    @Test
    public void contrasenaEscrita_esValida() {
        assertEquals(FormularioViewModel.VALIDO, LoginViewModel.validarContrasena("clave"));
    }

    @Test
    public void mensajeIngreso_segunConexion() {
        assertEquals(R.string.conexion_no_disponible, LoginViewModel.obtenerMensajeIngreso(false));
        assertEquals(R.string.login_pendiente_backend, LoginViewModel.obtenerMensajeIngreso(true));
    }
}
