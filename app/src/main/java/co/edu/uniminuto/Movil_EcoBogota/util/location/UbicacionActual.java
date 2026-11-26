package co.edu.uniminuto.Movil_EcoBogota.util.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Obtiene una lectura de la ubicación actual. Llamar solo cuando
 * PermissionManager#tieneUbicacion() sea true.
 */
public final class UbicacionActual {

    public interface Callback {
        void onUbicacion(Location ubicacion);

        void onSinUbicacion();
    }

    private UbicacionActual() {
    }

    @SuppressLint("MissingPermission")
    public static void obtener(Context context, Callback callback) {
        try {
            LocationServices.getFusedLocationProviderClient(context)
                    .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener(ubicacion -> {
                        if (ubicacion != null) {
                            callback.onUbicacion(ubicacion);
                        } else {
                            callback.onSinUbicacion();
                        }
                    })
                    .addOnFailureListener(e -> callback.onSinUbicacion());
        } catch (SecurityException e) {
            callback.onSinUbicacion();
        }
    }
}
