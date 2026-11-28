package co.edu.uniminuto.Movil_EcoBogota.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

//Gestor de la sesión: guarda el token JWT cifrado en el dispositivo (D-05)
public class SessionManager implements SessionStore {

    //Nombre del archivo de preferencias cifradas y clave del token
    static final String NOMBRE_PREFERENCIAS = "sesion_ecobogota";
    private static final String CLAVE_TOKEN = "token_jwt";

    //Instancia única
    private static SessionManager instancia;

    //Preferencias cifradas donde se guarda el token
    private final SharedPreferences preferencias;

    //Aviso para la interfaz cuando la sesión se invalida (P-04)
    private final MutableLiveData<Boolean> sesionInvalidada = new MutableLiveData<>(false);

    //Las clases de security-crypto 1.1.0 están marcadas como obsoletas; se mantienen por la decisión R-01/DA-6
    @SuppressWarnings("deprecation")
    private SessionManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            preferencias = EncryptedSharedPreferences.create(
                    context,
                    NOMBRE_PREFERENCIAS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("No fue posible inicializar el almacenamiento cifrado de la sesión", e);
        }
    }

    //Método que devuelve la instancia única usando el contexto de la aplicación
    public static synchronized SessionManager getInstance(Context context) {
        if (instancia == null) {
            instancia = new SessionManager(context.getApplicationContext());
        }
        return instancia;
    }

    //Método para guardar el token recibido del backend
    public synchronized void guardarToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacío");
        }
        preferencias.edit().putString(CLAVE_TOKEN, token).commit();
    }

    @Override
    public synchronized String obtenerToken() {
        return preferencias.getString(CLAVE_TOKEN, null);
    }

    //Solo comprueba que el token exista; no lo decodifica
    @Override
    public synchronized boolean hayToken() {
        String token = obtenerToken();
        return token != null && !token.isEmpty();
    }

    //Aviso de sesión invalidada para la interfaz (P-04); true una sola vez por invalidación
    public LiveData<Boolean> getSesionInvalidada() {
        return sesionInvalidada;
    }

    //La interfaz confirma que ya atendió el aviso para no repetirlo
    public void limpiarAvisoSesion() {
        sesionInvalidada.postValue(false);
    }

    //Sincronizado para que varias respuestas 401 simultáneas produzcan una sola invalidación y un solo aviso
    @Override
    public synchronized boolean invalidarSesion() {
        if (!preferencias.contains(CLAVE_TOKEN)) {
            return false;
        }
        preferencias.edit().remove(CLAVE_TOKEN).commit();
        sesionInvalidada.postValue(true);
        return true;
    }
}
