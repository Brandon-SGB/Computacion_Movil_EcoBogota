package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Base de los ViewModel de listados: expone el estado de la pantalla y el mensaje para el usuario
public abstract class ListadoViewModel extends AndroidViewModel {

    //Estado de la pantalla y mensaje
    private final MutableLiveData<EstadoUi> estado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public ListadoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<EstadoUi> getEstado() {
        return estado;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    //Método para consultar el listado; sin servicio del backend configurado termina en ERROR con su mensaje
    public void cargar() {
        estado.setValue(EstadoUi.CARGANDO);
        mensaje.setValue(getApplication().getString(obtenerMensajeConsulta(hayConexion())));
        estado.setValue(EstadoUi.ERROR);
    }

    //Mensaje de la consulta según la conexión; primero se comprueba la conexión
    static int obtenerMensajeConsulta(boolean conectado) {
        return conectado ? R.string.consulta_pendiente_backend : R.string.conexion_no_disponible;
    }

    //Método para verificar si el dispositivo tiene conexión a internet
    protected boolean hayConexion() {
        return ConexionRed.hayConexion(getApplication());
    }
}
