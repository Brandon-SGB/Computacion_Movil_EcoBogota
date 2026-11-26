package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.DetalleSolicitudFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-12. Pestaña de Mis trámites: solicitudes propias con residuo, cantidad, fecha y estado.
 */
public class MisSolicitudesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvSolicitudes;
    private EstadoLista estadoLista;

    public MisSolicitudesFragment() {
        super(R.layout.fragment_mis_solicitudes);
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
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-12: SolicitudRepository.listarMias() -> GET /me/solicitudes.
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvSolicitudes.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvSolicitudes.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvSolicitudes.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_mis_solicitudes), false);
    }

    private void mostrarError(String mensaje) {
        rvSolicitudes.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** Se conecta al adaptador cuando la lista tenga datos (HU-12). */
    private void abrirDetalle(long id) {
        Navegador.desde(this).abrirPantalla(DetalleSolicitudFragment.newInstance(id, false));
    }

    /** La llama MisTramitesFragment cuando un formulario termina con éxito. */
    public void refrescar() {
        if (getView() != null) {
            cargarDatos();
        }
    }
}
