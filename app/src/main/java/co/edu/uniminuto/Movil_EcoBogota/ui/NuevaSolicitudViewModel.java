package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import co.edu.uniminuto.Movil_EcoBogota.R;

//ViewModel del formulario de nueva solicitud de recolección (HU-11)
public class NuevaSolicitudViewModel extends FormularioViewModel {

    //Errores de los campos; null cuando el campo es válido
    private final MutableLiveData<String> errorCantidad = new MutableLiveData<>();
    private final MutableLiveData<String> errorDireccion = new MutableLiveData<>();

    public NuevaSolicitudViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getErrorCantidad() {
        return errorCantidad;
    }

    public LiveData<String> getErrorDireccion() {
        return errorDireccion;
    }

    //Método para validar y enviar la solicitud
    public void enviar(String cantidad, String direccion) {
        int validacionCantidad = validarCantidad(cantidad);
        int validacionUbicacion = validarUbicacion(tieneUbicacionGps(), direccion);
        errorCantidad.setValue(textoError(validacionCantidad));
        errorDireccion.setValue(textoError(validacionUbicacion));
        if (validacionCantidad != VALIDO) {
            mostrarError(validacionCantidad);
            return;
        }
        if (validacionUbicacion != VALIDO) {
            mostrarError(validacionUbicacion);
            return;
        }
        enviarSinServicio(R.string.solicitud_no_enviada_sin_conexion);
    }

    //La cantidad es obligatoria, numérica y mayor que 0 (CP-11-03)
    static int validarCantidad(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return R.string.error_cantidad_obligatoria;
        }
        double cantidad;
        try {
            cantidad = Double.parseDouble(texto.trim());
        } catch (NumberFormatException e) {
            return R.string.error_cantidad_numerica;
        }
        if (Double.isNaN(cantidad) || Double.isInfinite(cantidad)) {
            return R.string.error_cantidad_numerica;
        }
        if (cantidad <= 0) {
            return R.string.error_cantidad_mayor_cero;
        }
        return VALIDO;
    }

    //Método auxiliar que convierte el resultado de una validación en texto
    private String textoError(int validacion) {
        return validacion == VALIDO ? null : getApplication().getString(validacion);
    }
}
