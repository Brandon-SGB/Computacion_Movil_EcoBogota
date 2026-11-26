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
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-07. Pestaña de Mis trámites: reportes propios con título, tipo, fecha y estado.
 */
public class MisReportesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvReportes;
    private EstadoLista estadoLista;

    public MisReportesFragment() {
        super(R.layout.fragment_mis_reportes);
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
        rvReportes = view.findViewById(R.id.rvReportes);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-07: ReporteRepository.listarMios() -> GET /me/reportes.
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
        estadoLista.mostrarMensaje(getString(R.string.vacio_mis_reportes), false);
    }

    private void mostrarError(String mensaje) {
        rvReportes.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** Se conecta al adaptador cuando la lista tenga datos (HU-07). */
    private void abrirDetalle(long id) {
        Navegador.desde(this).abrirPantalla(DetalleReporteFragment.newInstance(id, false));
    }

    /** La llama MisTramitesFragment cuando un formulario termina con éxito. */
    public void refrescar() {
        if (getView() != null) {
            cargarDatos();
        }
    }
}
