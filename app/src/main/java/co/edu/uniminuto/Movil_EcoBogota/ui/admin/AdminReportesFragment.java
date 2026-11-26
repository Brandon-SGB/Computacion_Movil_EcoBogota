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
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.DetalleReporteFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-08. Todos los reportes con filtro por estado; abre DetalleReporteFragment en modo admin.
 */
public class AdminReportesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvReportes;
    private EstadoLista estadoLista;
    private ChipGroup cgEstados;

    public AdminReportesFragment() {
        super(R.layout.fragment_admin_reportes);
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
        rvReportes = view.findViewById(R.id.rvReportes);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        cgEstados = view.findViewById(R.id.cgEstados);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-08: ReporteRepository.listarTodos(getEstadoSeleccionado(), ...).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvReportes.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvReportes.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvReportes.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_reportes), false);
    }

    private void mostrarError(String mensaje) {
        rvReportes.setVisibility(View.GONE);
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
        Navegador.desde(this).abrirPantalla(DetalleReporteFragment.newInstance(id, true));
    }
}
