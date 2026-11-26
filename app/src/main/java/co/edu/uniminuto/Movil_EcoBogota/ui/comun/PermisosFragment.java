package co.edu.uniminuto.Movil_EcoBogota.ui.comun;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;

/**
 * Transversal / D6. Estado de Cámara, Ubicación, Notificaciones y Bluetooth (este último solo
 * para demostrar permisos, D9). Botones Solicitar, Verificar y Abrir ajustes. Lo usan el
 * ciudadano (desde Perfil) y el administrador (desde la toolbar).
 */
public class PermisosFragment extends Fragment {

    // 1. Atributos
    private View filaCamara;
    private View filaUbicacion;
    private View filaNotificaciones;
    private View filaBluetooth;
    private Button btnSolicitar;
    private Button btnVerificar;
    private Button btnAbrirAjustes;

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);

    public PermisosFragment() {
        super(R.layout.fragment_permisos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnSolicitar.setOnClickListener(this::getRequestPermissions);
        btnVerificar.setOnClickListener(this::getCheckPermissions);
        btnAbrirAjustes.setOnClickListener(v -> permissionManager.abrirAjustes());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Al volver de Ajustes, el estado puede haber cambiado.
        actualizarEstados();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        filaCamara = view.findViewById(R.id.filaCamara);
        filaUbicacion = view.findViewById(R.id.filaUbicacion);
        filaNotificaciones = view.findViewById(R.id.filaNotificaciones);
        filaBluetooth = view.findViewById(R.id.filaBluetooth);
        btnSolicitar = view.findViewById(R.id.btnSolicitar);
        btnVerificar = view.findViewById(R.id.btnVerificar);
        btnAbrirAjustes = view.findViewById(R.id.btnAbrirAjustes);
        configurarFila(filaCamara, R.string.permiso_camara, R.string.permiso_camara_motivo);
        configurarFila(filaUbicacion, R.string.permiso_ubicacion, R.string.permiso_ubicacion_motivo);
        configurarFila(filaNotificaciones, R.string.permiso_notificaciones, R.string.permiso_notificaciones_motivo);
        configurarFila(filaBluetooth, R.string.permiso_bluetooth, R.string.permiso_bluetooth_motivo);
    }

    // 3. Métodos auxiliares
    private void configurarFila(View fila, @StringRes int nombre, @StringRes int motivo) {
        ((TextView) fila.findViewById(R.id.tvNombrePermiso)).setText(nombre);
        ((TextView) fila.findViewById(R.id.tvMotivoPermiso)).setText(motivo);
    }

    private void mostrarEstado(View fila, PermissionManager.Estado estado) {
        TextView tvEstado = fila.findViewById(R.id.tvEstadoPermiso);
        switch (estado) {
            case CONCEDIDO:
                tvEstado.setText(R.string.estado_permiso_concedido);
                break;
            case DENEGADO_PERMANENTE:
                tvEstado.setText(R.string.estado_permiso_bloqueado);
                break;
            case NO_REQUERIDO:
                tvEstado.setText(R.string.estado_permiso_no_requerido);
                break;
            default:
                tvEstado.setText(R.string.estado_permiso_denegado);
                break;
        }
    }

    private PermissionManager.Estado estadoUbicacion() {
        return permissionManager.tieneUbicacion()
                ? PermissionManager.Estado.CONCEDIDO
                : permissionManager.getPermissionsStatus(PermissionManager.getPermisosUbicacion());
    }

    private boolean hayBloqueados() {
        return permissionManager.getPermissionsStatus(PermissionManager.getPermisosCamara()) == PermissionManager.Estado.DENEGADO_PERMANENTE
                || estadoUbicacion() == PermissionManager.Estado.DENEGADO_PERMANENTE
                || permissionManager.getPermissionsStatus(PermissionManager.getPermisosNotificaciones()) == PermissionManager.Estado.DENEGADO_PERMANENTE
                || permissionManager.getPermissionsStatus(PermissionManager.getPermisosBluetooth()) == PermissionManager.Estado.DENEGADO_PERMANENTE;
    }

    private String[] getListPermissions() {
        List<String> permisos = new ArrayList<>();
        Collections.addAll(permisos, PermissionManager.getPermisosCamara());
        Collections.addAll(permisos, PermissionManager.getPermisosUbicacion());
        Collections.addAll(permisos, PermissionManager.getPermisosNotificaciones());
        Collections.addAll(permisos, PermissionManager.getPermisosBluetooth());
        return permisos.toArray(new String[0]);
    }

    private void actualizarEstados() {
        mostrarEstado(filaCamara, permissionManager.getPermissionsStatus(PermissionManager.getPermisosCamara()));
        mostrarEstado(filaUbicacion, estadoUbicacion());
        mostrarEstado(filaNotificaciones, permissionManager.getPermissionsStatus(PermissionManager.getPermisosNotificaciones()));
        mostrarEstado(filaBluetooth, permissionManager.getPermissionsStatus(PermissionManager.getPermisosBluetooth()));
        String[] permisos = getListPermissions();
        btnSolicitar.setEnabled(!permissionManager.checkHasPermissions(permisos));
        btnAbrirAjustes.setVisibility(hayBloqueados() ? View.VISIBLE : View.GONE);
    }

    // 4. Eventos
    private void getCheckPermissions(View view) {
        actualizarEstados();
    }

    private void getRequestPermissions(View view) {
        String[] permisos = getListPermissions();
        if (permissionManager.debeMostrarExplicacion(permisos)) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.permiso_explicacion_titulo)
                    .setMessage(R.string.permiso_explicacion_general)
                    .setPositiveButton(R.string.btn_continuar, (d, w) -> permissionManager.getRequestPermissions(permisos))
                    .setNegativeButton(R.string.btn_cancelar, null)
                    .show();
        } else {
            permissionManager.getRequestPermissions(permisos);
        }
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        actualizarEstados();
    }
}
