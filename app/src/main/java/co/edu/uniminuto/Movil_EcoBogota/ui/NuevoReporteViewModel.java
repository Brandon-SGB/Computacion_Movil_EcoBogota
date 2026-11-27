package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import co.edu.uniminuto.Movil_EcoBogota.R;

//ViewModel del formulario de nuevo reporte (HU-06)
public class NuevoReporteViewModel extends FormularioViewModel {

    //Errores de los campos; null cuando el campo es válido
    private final MutableLiveData<String> errorDescripcion = new MutableLiveData<>();
    private final MutableLiveData<String> errorDireccion = new MutableLiveData<>();

    public NuevoReporteViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getErrorDescripcion() {
        return errorDescripcion;
    }

    public LiveData<String> getErrorDireccion() {
        return errorDireccion;
    }

    //Método para validar y enviar el reporte
    public void enviar(String descripcion, String direccion) {
        int validacionDescripcion = validarDescripcion(descripcion);
        int validacionUbicacion = validarUbicacion(tieneUbicacionGps(), direccion);
        errorDescripcion.setValue(textoError(validacionDescripcion));
        errorDireccion.setValue(textoError(validacionUbicacion));
        if (validacionDescripcion != VALIDO) {
            mostrarError(validacionDescripcion);
            return;
        }
        if (validacionUbicacion != VALIDO) {
            mostrarError(validacionUbicacion);
            return;
        }
        enviarSinServicio(R.string.reporte_no_enviado_sin_conexion);
    }

    //La descripción es obligatoria (CP-06-03)
    static int validarDescripcion(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return R.string.error_descripcion_obligatoria;
        }
        return VALIDO;
    }

    //Método auxiliar que convierte el resultado de una validación en texto
    private String textoError(int validacion) {
        return validacion == VALIDO ? null : getApplication().getString(validacion);
    }
}
