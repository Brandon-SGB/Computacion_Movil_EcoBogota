package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * HU-03. Lista de usuarios con búsqueda y filtro activo/inactivo.
 */
public class AdminUsuariosFragment extends Fragment {

    // 1. Atributos
    private RecyclerView rvUsuarios;
    private EstadoLista estadoLista;
    private TextInputEditText etBuscar;
    private ChipGroup cgFiltroActivo;

    public AdminUsuariosFragment() {
        super(R.layout.fragment_admin_usuarios);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        cgFiltroActivo.setOnCheckedStateChangeListener((grupo, ids) -> cargarDatos());
        etBuscar.setOnEditorActionListener((v, accion, evento) -> {
            cargarDatos();
            return false;
        });
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        rvUsuarios = view.findViewById(R.id.rvUsuarios);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        etBuscar = view.findViewById(R.id.etBuscar);
        cgFiltroActivo = view.findViewById(R.id.cgFiltroActivo);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-03: UsuarioRepository.listar(getTextoBusqueda(), getFiltroActivo(), ...).
        // onSuccess -> adapter.actualizar(lista) y mostrarContenido() o mostrarVacio();
        // onError -> mostrarError(mensaje).
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvUsuarios.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvUsuarios.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvUsuarios.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_usuarios), false);
    }

    private void mostrarError(String mensaje) {
        rvUsuarios.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    private String getTextoBusqueda() {
        return etBuscar.getText() == null ? "" : etBuscar.getText().toString().trim();
    }

    /** true = activos, false = inactivos, null = todos. */
    private Boolean getFiltroActivo() {
        int id = cgFiltroActivo.getCheckedChipId();
        if (id == R.id.chipActivos) {
            return Boolean.TRUE;
        } else if (id == R.id.chipInactivos) {
            return Boolean.FALSE;
        }
        return null;
    }

    /** Se conecta al adaptador (HU-03). */
    private void abrirFormulario(long idUsuario) {
        Navegador.desde(this).abrirPantalla(AdminUsuarioFormFragment.newInstance(idUsuario));
    }
}
