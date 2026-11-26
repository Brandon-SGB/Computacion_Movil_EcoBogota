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
 * HU-16. Recomendaciones de reciclaje agrupadas por categoría.
 */
public class RecomendacionesFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvRecomendaciones;
    private EstadoLista estadoLista;

    public RecomendacionesFragment() {
        super(R.layout.fragment_recomendaciones);
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
        rvRecomendaciones = view.findViewById(R.id.rvRecomendaciones);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvRecomendaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-16: RecomendacionRepository.listar(RepositoryCallback<List<RecomendacionResponse>>).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvRecomendaciones.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvRecomendaciones.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvRecomendaciones.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_recomendaciones), false);
    }

    private void mostrarError(String mensaje) {
        rvRecomendaciones.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }
}
