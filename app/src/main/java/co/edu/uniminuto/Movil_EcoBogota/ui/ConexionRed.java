package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

//Comprobación de la conexión a internet compartida por los ViewModel
public final class ConexionRed {

    private ConexionRed() {
    }

    //Método para verificar si el dispositivo tiene conexión a internet
    public static boolean hayConexion(Context context) {
        try {
            ConnectivityManager conexion = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conexion == null) {
                return false;
            }
            Network red = conexion.getActiveNetwork();
            NetworkCapabilities caps = conexion.getNetworkCapabilities(red);
            return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception e) {
            Log.i("conexion", "error con la conexión " + e.getMessage());
            return false;
        }
    }
}
