package co.edu.uniminuto.Movil_EcoBogota.data.session;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda la sesión del usuario: token, id, nombre, correo y rol.
 * Pendiente (riesgo R8 de la Fase 1): mover el token a almacenamiento cifrado.
 */
public class SessionManager {

    // 1. Atributos
    public static final String ROL_CIUDADANO = "CIUDADANO";
    public static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    private static final String PREFERENCIAS = "ecobogota_sesion";
    private static final String CLAVE_TOKEN = "token";
    private static final String CLAVE_ID = "id_usuario";
    private static final String CLAVE_NOMBRE = "nombre";
    private static final String CLAVE_CORREO = "correo";
    private static final String CLAVE_ROL = "rol";

    private final SharedPreferences preferencias;

    public SessionManager(Context context) {
        preferencias = context.getApplicationContext()
                .getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
    }

    // 2. Escritura
    public void guardarSesion(String token, long idUsuario, String nombre, String correo, String rol) {
        preferencias.edit()
                .putString(CLAVE_TOKEN, token)
                .putLong(CLAVE_ID, idUsuario)
                .putString(CLAVE_NOMBRE, nombre)
                .putString(CLAVE_CORREO, correo)
                .putString(CLAVE_ROL, rol)
                .apply();
    }

    public void cerrarSesion() {
        preferencias.edit().clear().apply();
    }

    // 3. Lectura
    public boolean haySesion() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public boolean esAdministrador() {
        return ROL_ADMINISTRADOR.equals(getRol());
    }

    public String getToken() {
        return preferencias.getString(CLAVE_TOKEN, null);
    }

    public long getIdUsuario() {
        return preferencias.getLong(CLAVE_ID, 0L);
    }

    public String getNombre() {
        return preferencias.getString(CLAVE_NOMBRE, "");
    }

    public String getCorreo() {
        return preferencias.getString(CLAVE_CORREO, "");
    }

    public String getRol() {
        return preferencias.getString(CLAVE_ROL, ROL_CIUDADANO);
    }
}
