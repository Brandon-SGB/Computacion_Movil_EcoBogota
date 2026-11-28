package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Predicate;

import co.edu.uniminuto.Movil_EcoBogota.R;

//ViewModel del inicio de sesión (HU-02); la contraseña no se guarda ni se registra en Log (A7)
public class LoginViewModel extends AndroidViewModel {

    //Estado de la pantalla, mensaje y errores de los campos
    private final MutableLiveData<EstadoUi> estado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();
    private final MutableLiveData<String> errorCorreo = new MutableLiveData<>();
    private final MutableLiveData<String> errorContrasena = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<EstadoUi> getEstado() {
        return estado;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<String> getErrorCorreo() {
        return errorCorreo;
    }

    public LiveData<String> getErrorContrasena() {
        return errorContrasena;
    }

    //Método del botón Ingresar: valida, comprueba la conexión y, sin servicio del backend, no envía nada
    public void ingresar(String correo, String contrasena) {
        int validacionCorreo = validarCorreo(correo,
                texto -> Patterns.EMAIL_ADDRESS.matcher(texto).matches());
        int validacionContrasena = validarContrasena(contrasena);
        errorCorreo.setValue(textoError(validacionCorreo));
        errorContrasena.setValue(textoError(validacionContrasena));
        if (validacionCorreo != FormularioViewModel.VALIDO) {
            mostrarError(validacionCorreo);
            return;
        }
        if (validacionContrasena != FormularioViewModel.VALIDO) {
            mostrarError(validacionContrasena);
            return;
        }
        estado.setValue(EstadoUi.CARGANDO);
        mostrarError(obtenerMensajeIngreso(ConexionRed.hayConexion(getApplication())));
    }

    //El correo es obligatorio y debe tener formato válido (A8)
    static int validarCorreo(String correo, Predicate<String> formatoValido) {
        if (correo == null || correo.trim().isEmpty()) {
            return R.string.error_correo_obligatorio;
        }
        if (!formatoValido.test(correo.trim())) {
            return R.string.error_correo_invalido;
        }
        return FormularioViewModel.VALIDO;
    }

    //La contraseña es obligatoria (A8)
    static int validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            return R.string.error_contrasena_obligatoria;
        }
        return FormularioViewModel.VALIDO;
    }

    //Mensaje del ingreso según la conexión
    static int obtenerMensajeIngreso(boolean conectado) {
        return conectado ? R.string.login_pendiente_backend : R.string.conexion_no_disponible;
    }

    private void mostrarError(int idMensaje) {
        mensaje.setValue(getApplication().getString(idMensaje));
        estado.setValue(EstadoUi.ERROR);
    }

    private String textoError(int validacion) {
        return validacion == FormularioViewModel.VALIDO ? null : getApplication().getString(validacion);
    }
}
