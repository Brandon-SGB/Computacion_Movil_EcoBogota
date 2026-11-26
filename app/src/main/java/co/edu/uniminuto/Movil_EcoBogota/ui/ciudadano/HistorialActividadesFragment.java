package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;

/**
 * HU-15. Historial de actividades: fecha, descripción y puntos (+/-). Solo lectura.
 */
public class HistorialActividadesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvMovimientos;
    private EstadoLista estadoLista;

    public HistorialActividadesFragment() {
        super(R.layout.fragment_historial_actividades);
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
        rvMovimientos = view.findViewById(R.id.rvMovimientos);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvMovimientos.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-15: PuntosRepository.listarMovimientos() -> GET /me/movimientos.
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvMovimientos.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvMovimientos.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvMovimientos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_historial_actividades), false);
    }

    private void mostrarError(String mensaje) {
        rvMovimientos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }
}
