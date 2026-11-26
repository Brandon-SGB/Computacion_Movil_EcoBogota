package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-05. Lista de residuos (reutiliza ResiduoAdapter) + FAB Nuevo.
 */
public class AdminResiduosFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvResiduos;
    private EstadoLista estadoLista;
    private FloatingActionButton fabNuevo;

    public AdminResiduosFragment() {
        super(R.layout.fragment_admin_residuos);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        fabNuevo.setOnClickListener(v -> abrirFormulario(0L));
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        rvResiduos = view.findViewById(R.id.rvResiduos);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        fabNuevo = view.findViewById(R.id.fabNuevo);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvResiduos.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-05: ResiduoRepository.listar(...).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvResiduos.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvResiduos.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvResiduos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_residuos), false);
    }

    private void mostrarError(String mensaje) {
        rvResiduos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** id = 0 crea un registro nuevo; otro id lo edita (se conecta al adaptador). */
    private void abrirFormulario(long id) {
        Navegador.desde(this).abrirPantalla(AdminResiduoFormFragment.newInstance(id));
    }
}
