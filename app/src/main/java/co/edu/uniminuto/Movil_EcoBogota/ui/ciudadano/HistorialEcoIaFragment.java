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
 * HU-18. Lista de consultas anteriores a EcoIA.
 */
public class HistorialEcoIaFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvConsultas;
    private EstadoLista estadoLista;

    public HistorialEcoIaFragment() {
        super(R.layout.fragment_historial_ecoia);
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
        rvConsultas = view.findViewById(R.id.rvConsultas);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvConsultas.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-18: EcoIaRepository.listarHistorial(RepositoryCallback<List<ConsultaEcoIaResponse>>).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvConsultas.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvConsultas.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvConsultas.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_historial_ecoia), false);
    }

    private void mostrarError(String mensaje) {
        rvConsultas.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }
}
