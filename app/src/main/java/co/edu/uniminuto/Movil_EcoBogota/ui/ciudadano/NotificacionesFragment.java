package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.DetalleReporteFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.DetalleSolicitudFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-19. Bandeja de notificaciones; al tocar una, abre el detalle del trámite.
 */
public class NotificacionesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvNotificaciones;
    private EstadoLista estadoLista;

    public NotificacionesFragment() {
        super(R.layout.fragment_notificaciones);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        rvNotificaciones = view.findViewById(R.id.rvNotificaciones);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-19: NotificacionRepository.listar(RepositoryCallback<List<NotificacionResponse>>).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvNotificaciones.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvNotificaciones.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvNotificaciones.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_notificaciones), false);
    }

    private void mostrarError(String mensaje) {
        rvNotificaciones.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** Se conecta al adaptador: abre el reporte o la solicitud de la notificación. */
    private void abrirTramite(boolean esReporte, long idTramite) {
        Navegador.desde(this).abrirPantalla(esReporte
                ? DetalleReporteFragment.newInstance(idTramite, false)
                : DetalleSolicitudFragment.newInstance(idTramite, false));
    }
}
