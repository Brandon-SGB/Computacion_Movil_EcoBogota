package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Base de los ViewModel de administración: listado completo y actualización de estado
public abstract class GestionViewModel extends ListadoViewModel {

    //Código HTTP con el que el backend indicaría falta de permisos
    static final int CODIGO_SIN_PERMISOS = 403;

    //Mensaje del resultado de actualizar el estado
    private final MutableLiveData<String> mensajeActualizacion = new MutableLiveData<>();

    public GestionViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensajeActualizacion() {
        return mensajeActualizacion;
    }

    //Método del botón Actualizar estado; los estados posibles llegan con el contrato G7
    public void actualizarEstado() {
        mensajeActualizacion.setValue(getApplication().getString(
                obtenerMensajeActualizacion(hayConexion())));
    }

    //Mensaje de la actualización según la conexión
    static int obtenerMensajeActualizacion(boolean conectado) {
        return conectado ? R.string.envio_pendiente_backend : R.string.conexion_no_disponible;
    }

    //Mensaje para una respuesta de error del backend; un 403 se informa como falta de permisos
    static int obtenerMensajeError(int codigoHttp) {
        return codigoHttp == CODIGO_SIN_PERMISOS ? R.string.error_sin_permisos : R.string.error_operacion;
    }
}
