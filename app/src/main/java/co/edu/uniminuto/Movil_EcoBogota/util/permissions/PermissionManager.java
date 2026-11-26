package co.edu.uniminuto.Movil_EcoBogota.util.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Métodos del documento de Permisos (3 a 7) en una clase reutilizable (alternativa L1), con la
 * Activity Result API (alternativa L2), explicación previa y denegación permanente (L3).
 * Se crea como atributo de la Activity o del Fragment (antes de onStart).
 */
public class PermissionManager {

    // 1. Atributos
    public enum Estado { CONCEDIDO, DENEGADO, DENEGADO_PERMANENTE, NO_REQUERIDO }

    public interface Listener {
        /** @param todosConcedidos true si el usuario concedió todo lo pedido. */
        void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado);
    }

    private final Supplier<Activity> proveedorActividad;
    private final ActivityResultLauncher<String[]> launcher;
    private final List<String> denegadosPermanentes = new ArrayList<>();

    public PermissionManager(Fragment fragment, Listener listener) {
        this(fragment, fragment::requireActivity, listener);
    }

    public PermissionManager(ComponentActivity activity, Listener listener) {
        this(activity, () -> activity, listener);
    }

    private PermissionManager(ActivityResultCaller caller, Supplier<Activity> proveedor, Listener listener) {
        this.proveedorActividad = proveedor;
        this.launcher = caller.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                resultado -> onRespuestaUsuario(resultado, listener));
    }

    // 2. Grupos de permisos según la versión de Android
    public static String[] getPermisosCamara() {
        return new String[]{Manifest.permission.CAMERA};
    }

    /** FINE y COARSE juntos: la ubicación aproximada también se acepta (L7). */
    public static String[] getPermisosUbicacion() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    public static String[] getPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.POST_NOTIFICATIONS};
        }
        return new String[0];
    }

    /** Bluetooth solo demuestra el manejo de permisos (D9). Hasta API 30 se concede al instalar. */
    public static String[] getPermisosBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT};
        }
        return new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
    }

    // 3. Estado de un grupo de permisos (getPermissionsStatus del documento)
    public Estado getPermissionsStatus(String... permisos) {
        if (permisos.length == 0) {
            return Estado.NO_REQUERIDO;
        }
        if (checkHasPermissions(permisos)) {
            return Estado.CONCEDIDO;
        }
        for (String permiso : permisos) {
            if (denegadosPermanentes.contains(permiso)) {
                return Estado.DENEGADO_PERMANENTE;
            }
        }
        return Estado.DENEGADO;
    }

    // 4. ¿Tiene todos los permisos? (checkHasPermissions del documento)
    public boolean checkHasPermissions(String... permisos) {
        Context context = proveedorActividad.get();
        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** Ubicación: basta con la aproximada (L7). */
    public boolean tieneUbicacion() {
        Context context = proveedorActividad.get();
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 5. ¿Hay que explicar antes de pedir? (L3)
    public boolean debeMostrarExplicacion(String... permisos) {
        Activity activity = proveedorActividad.get();
        for (String permiso : permisos) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)) {
                return true;
            }
        }
        return false;
    }

    // 6. Solicitar solo los que faltan (getRequestPermissions del documento)
    public void getRequestPermissions(String... permisos) {
        Context context = proveedorActividad.get();
        List<String> faltantes = new ArrayList<>();
        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                faltantes.add(permiso);
            }
        }
        if (faltantes.isEmpty()) {
            launcher.launch(new String[0]);
            return;
        }
        launcher.launch(faltantes.toArray(new String[0]));
    }

    // 7. Abrir los ajustes de la app (denegación permanente)
    public void abrirAjustes() {
        Activity activity = proveedorActividad.get();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    // 8. Respuesta del usuario
    private void onRespuestaUsuario(Map<String, Boolean> resultado, Listener listener) {
        Activity activity = proveedorActividad.get();
        boolean todos = true;
        for (Map.Entry<String, Boolean> entrada : resultado.entrySet()) {
            String permiso = entrada.getKey();
            if (Boolean.TRUE.equals(entrada.getValue())) {
                denegadosPermanentes.remove(permiso);
            } else {
                todos = false;
                // Denegado sin opción de explicar: el sistema ya no muestra el diálogo.
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)
                        && !denegadosPermanentes.contains(permiso)) {
                    denegadosPermanentes.add(permiso);
                }
            }
        }
        listener.onResultadoPermisos(todos, resultado);
    }
}
