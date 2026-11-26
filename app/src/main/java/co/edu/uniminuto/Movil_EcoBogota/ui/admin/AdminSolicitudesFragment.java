package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.DetalleSolicitudFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-13. Todas las solicitudes con filtro por estado; abre DetalleSolicitudFragment en modo admin.
 */
public class AdminSolicitudesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvSolicitudes;
    private EstadoLista estadoLista;
    private ChipGroup cgEstados;

    public AdminSolicitudesFragment() {
        super(R.layout.fragment_admin_solicitudes);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        cgEstados.setOnCheckedStateChangeListener((grupo, ids) -> cargarDatos());
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        cgEstados = view.findViewById(R.id.cgEstados);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-13: SolicitudRepository.listarTodas(getEstadoSeleccionado(), ...).
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
        estadoLista.mostrarMensaje(getString(R.string.vacio_solicitudes), false);
    }

    private void mostrarError(String mensaje) {
        rvSolicitudes.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** Estado elegido en los chips; null = todos (lista oficial pendiente, P8). */
    private String getEstadoSeleccionado() {
        int id = cgEstados.getCheckedChipId();
        if (id == View.NO_ID || id == R.id.chipEstadoTodos) {
            return null;
        }
        return String.valueOf(cgEstados.findViewById(id).getTag());
    }

    /** Abre el detalle en modo administrador (se conecta al adaptador). */
    private void abrirDetalle(long id) {
        Navegador.desde(this).abrirPantalla(DetalleSolicitudFragment.newInstance(id, true));
    }
}
