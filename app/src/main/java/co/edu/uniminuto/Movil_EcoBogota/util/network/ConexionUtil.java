package co.edu.uniminuto.Movil_EcoBogota.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Indica si el dispositivo tiene conexión a internet.
 */
public final class ConexionUtil {

    private ConexionUtil() {
    }

    public static boolean hayInternet(Context context) {
        ConnectivityManager cm = context.getSystemService(ConnectivityManager.class);
        if (cm == null) {
            return false;
        }
        Network red = cm.getActiveNetwork();
        NetworkCapabilities capacidades = red == null ? null : cm.getNetworkCapabilities(red);
        return capacidades != null
                && capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
