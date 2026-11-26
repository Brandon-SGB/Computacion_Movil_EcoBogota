package co.edu.uniminuto.Movil_EcoBogota.util.validation;

import android.util.Patterns;

/**
 * Validaciones comunes de formularios (correo, campos vacíos y contraseña).
 */
public final class ValidadorFormulario {

    // 1. Constantes
    public static final int LONGITUD_MINIMA_CONTRASENA = 8;

    private ValidadorFormulario() {
    }

    // 2. Validaciones
    public static boolean estaVacio(CharSequence texto) {
        return texto == null || texto.toString().trim().isEmpty();
    }

    public static boolean esCorreoValido(CharSequence correo) {
        return !estaVacio(correo) && Patterns.EMAIL_ADDRESS.matcher(correo.toString().trim()).matches();
    }

    public static boolean esContrasenaValida(CharSequence contrasena) {
        return contrasena != null && contrasena.length() >= LONGITUD_MINIMA_CONTRASENA;
    }

    public static boolean esTelefonoValido(CharSequence telefono) {
        return !estaVacio(telefono) && telefono.toString().trim().matches("[0-9+ ]{7,15}");
    }

    public static boolean esNumeroPositivo(CharSequence texto) {
        if (estaVacio(texto)) {
            return false;
        }
        try {
            return Double.parseDouble(texto.toString().trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
