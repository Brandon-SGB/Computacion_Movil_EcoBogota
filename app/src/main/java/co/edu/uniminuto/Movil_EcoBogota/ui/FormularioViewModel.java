package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Base de los ViewModel de formularios: estado del envío, mensaje y validación de la ubicación
public abstract class FormularioViewModel extends AndroidViewModel {

    //Resultado de una validación correcta
    static final int VALIDO = 0;

    //Estado de la pantalla y mensaje
    private final MutableLiveData<EstadoUi> estado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public FormularioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<EstadoUi> getEstado() {
        return estado;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    //Método para dejar la pantalla en error con un mensaje de strings.xml
    protected void mostrarError(int idMensaje) {
        mensaje.setValue(getApplication().getString(idMensaje));
        estado.setValue(EstadoUi.ERROR);
    }

    //Método de envío: primero se comprueba la conexión; sin servicio del backend no se envía ni se guarda nada
    protected void enviarSinServicio(int idMensajeSinConexion) {
        estado.setValue(EstadoUi.CARGANDO);
        mostrarError(obtenerMensajeEnvio(ConexionRed.hayConexion(getApplication()), idMensajeSinConexion));
    }

    //Mensaje del envío según la conexión
    static int obtenerMensajeEnvio(boolean conectado, int idMensajeSinConexion) {
        return conectado ? R.string.envio_pendiente_backend : idMensajeSinConexion;
    }

    //La ubicación es válida si llegó por GPS o si se escribió una dirección
    static int validarUbicacion(boolean ubicacionGps, String direccion) {
        if (ubicacionGps || (direccion != null && !direccion.trim().isEmpty())) {
            return VALIDO;
        }
        return R.string.error_ubicacion_obligatoria;
    }
}
