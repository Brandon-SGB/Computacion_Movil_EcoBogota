package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;
import co.edu.uniminuto.Movil_EcoBogota.ui.auth.LoginActivity;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.PermisosFragment;

/**
 * HU-14/02. Nombre, correo y total de puntos; accesos a Historial, Notificaciones y Permisos;
 * Cerrar sesión.
 */
public class PerfilFragment extends Fragment {

    // 1. Atributos
    private TextView tvNombre;
    private TextView tvCorreo;
    private TextView tvTotalPuntos;
    private Button btnHistorial;
    private Button btnNotificaciones;
    private Button btnPermisos;
    private Button btnCerrarSesion;
    private SessionManager sessionManager;

    public PerfilFragment() {
        super(R.layout.fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        mostrarDatosSesion();
        cargarPuntos();
        btnHistorial.setOnClickListener(v -> Navegador.desde(this).abrirPantalla(new HistorialActividadesFragment()));
        btnNotificaciones.setOnClickListener(v -> Navegador.desde(this).abrirPantalla(new NotificacionesFragment()));
        btnPermisos.setOnClickListener(v -> Navegador.desde(this).abrirPantalla(new PermisosFragment()));
        btnCerrarSesion.setOnClickListener(this::cerrarSesion);
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tvNombre = view.findViewById(R.id.tvNombre);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        tvTotalPuntos = view.findViewById(R.id.tvTotalPuntos);
        btnHistorial = view.findViewById(R.id.btnHistorial);
        btnNotificaciones = view.findViewById(R.id.btnNotificaciones);
        btnPermisos = view.findViewById(R.id.btnPermisos);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        sessionManager = new SessionManager(requireContext());
    }

    // 3. Métodos auxiliares
    private void mostrarDatosSesion() {
        tvNombre.setText(sessionManager.getNombre());
        tvCorreo.setText(sessionManager.getCorreo());
    }

    private void cargarPuntos() {
        tvTotalPuntos.setText(R.string.valor_no_disponible);
        // TODO HU-14: PuntosRepository.obtenerResumen(...) -> tvTotalPuntos.
    }

    // 4. Eventos
    private void cerrarSesion(View view) {
        sessionManager.cerrarSesion();
        LoginActivity.abrirLimpiandoPila(requireContext());
        requireActivity().finish();
    }
}
