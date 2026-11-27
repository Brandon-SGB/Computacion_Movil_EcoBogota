package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;

import co.edu.uniminuto.Movil_EcoBogota.R;

//ViewModel del formulario de nuevo reporte (HU-06)
public class NuevoReporteViewModel extends FormularioViewModel {

    //Errores de los campos; null cuando el campo es válido
    private final MutableLiveData<String> errorDescripcion = new MutableLiveData<>();
    private final MutableLiveData<String> errorDireccion = new MutableLiveData<>();

    //Ruta de la foto tomada; el archivo vive en la caché de la app mientras el formulario está abierto
    private final MutableLiveData<String> rutaFoto = new MutableLiveData<>();

    //Acciones posibles tras la respuesta al permiso de cámara
    enum AccionPermisoCamara {
        ABRIR_CAMARA,
        EXPLICAR_Y_REINTENTAR,
        IR_A_AJUSTES
    }

    public NuevoReporteViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getErrorDescripcion() {
        return errorDescripcion;
    }

    public LiveData<String> getErrorDireccion() {
        return errorDireccion;
    }

    public LiveData<String> getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String ruta) {
        rutaFoto.setValue(ruta);
    }

    //Concedido: se abre la cámara; denegado con explicación: se explica y se reintenta (D-5);
    //denegado sin explicación tras pedirlo: denegación permanente, se ofrece ir a los ajustes (D-6)
    static AccionPermisoCamara decidirAccionPermisoCamara(boolean concedido, boolean mostrarExplicacion) {
        if (concedido) {
            return AccionPermisoCamara.ABRIR_CAMARA;
        }
        return mostrarExplicacion ? AccionPermisoCamara.EXPLICAR_Y_REINTENTAR : AccionPermisoCamara.IR_A_AJUSTES;
    }

    //Al cerrar el formulario se elimina la foto temporal; no se guarda en la galería
    @Override
    protected void onCleared() {
        String ruta = rutaFoto.getValue();
        if (ruta != null && !new File(ruta).delete()) {
            Log.i("camara", "no se pudo eliminar la foto temporal");
        }
        super.onCleared();
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
