package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Base de los ViewModel de formularios: estado del envío, mensaje y ubicación (GPS o manual)
public abstract class FormularioViewModel extends AndroidViewModel {

    //Resultado de una validación correcta
    static final int VALIDO = 0;

    //Estado de la pantalla y mensaje
    private final MutableLiveData<EstadoUi> estado = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    //Ubicación por GPS: texto para el formulario y coordenadas en memoria mientras el formulario está abierto
    private final MutableLiveData<String> textoUbicacionGps = new MutableLiveData<>();
    private boolean ubicacionGps = false;
    private double latitud;
    private double longitud;
    private CancellationTokenSource cancelacionUbicacion;

    public FormularioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getTextoUbicacionGps() {
        return textoUbicacionGps;
    }

    protected boolean tieneUbicacionGps() {
        return ubicacionGps;
    }

    protected double getLatitud() {
        return latitud;
    }

    protected double getLongitud() {
        return longitud;
    }

    //Método para leer la ubicación actual; si no llega, se intenta con la última conocida
    public void leerUbicacionGps() {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ubicacionNoObtenida();
            return;
        }
        try {
            textoUbicacionGps.setValue(getApplication().getString(R.string.ubicacion_buscando));
            FusedLocationProviderClient cliente = LocationServices.getFusedLocationProviderClient(getApplication());
            cancelarLectura();
            cancelacionUbicacion = new CancellationTokenSource();
            cliente.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancelacionUbicacion.getToken())
                    .addOnSuccessListener(ubicacion -> {
                        if (ubicacion != null) {
                            guardarUbicacion(ubicacion);
                        } else {
                            leerUltimaUbicacion(cliente);
                        }
                    })
                    .addOnFailureListener(e -> leerUltimaUbicacion(cliente));
        } catch (SecurityException e) {
            Log.i("ubicacion", "error con el permiso de ubicación " + e.getMessage());
            ubicacionNoObtenida();
        }
    }

    //Método para leer la última ubicación conocida del dispositivo
    private void leerUltimaUbicacion(FusedLocationProviderClient cliente) {
        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ubicacionNoObtenida();
            return;
        }
        try {
            cliente.getLastLocation()
                    .addOnSuccessListener(ubicacion -> {
                        if (ubicacion != null) {
                            guardarUbicacion(ubicacion);
                        } else {
                            ubicacionNoObtenida();
                        }
                    })
                    .addOnFailureListener(e -> ubicacionNoObtenida());
        } catch (SecurityException e) {
            Log.i("ubicacion", "error con el permiso de ubicación " + e.getMessage());
            ubicacionNoObtenida();
        }
    }

    //Método para guardar la ubicación en memoria y mostrarla en el formulario (las coordenadas no se registran en Log)
    private void guardarUbicacion(Location ubicacion) {
        latitud = ubicacion.getLatitude();
        longitud = ubicacion.getLongitude();
        ubicacionGps = true;
        textoUbicacionGps.setValue(getApplication().getString(R.string.ubicacion_gps_obtenida, latitud, longitud));
    }

    //Sin ubicación por GPS se usa la dirección manual
    private void ubicacionNoObtenida() {
        ubicacionGps = false;
        textoUbicacionGps.setValue(getApplication().getString(R.string.ubicacion_no_obtenida));
    }

    private void cancelarLectura() {
        if (cancelacionUbicacion != null) {
            cancelacionUbicacion.cancel();
            cancelacionUbicacion = null;
        }
    }

    @Override
    protected void onCleared() {
        cancelarLectura();
        super.onCleared();
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
