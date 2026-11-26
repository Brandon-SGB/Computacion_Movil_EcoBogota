package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
 * HU-04. Buscador, chips de categoría y lista de residuos (nombre, categoría y color de
 * recipiente). Accesos a Recomendaciones y EcoIA.
 */
public class InicioFragment extends Fragment {

    // 1. Atributos
    private TextInputEditText etBuscar;
    private ChipGroup cgCategorias;
    private TextView tvAvisoSinConexion;
    private RecyclerView rvResiduos;
    private EstadoLista estadoLista;
    private Button btnRecomendaciones;
    private Button btnEcoIa;

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        btnRecomendaciones.setOnClickListener(this::abrirRecomendaciones);
        btnEcoIa.setOnClickListener(this::abrirEcoIa);
        cgCategorias.setOnCheckedStateChangeListener((grupo, ids) -> cargarDatos());
        etBuscar.setOnEditorActionListener((v, accion, evento) -> {
            cargarDatos();
            return false;
        });
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        etBuscar = view.findViewById(R.id.etBuscar);
        cgCategorias = view.findViewById(R.id.cgCategorias);
        tvAvisoSinConexion = view.findViewById(R.id.tvAvisoSinConexion);
        rvResiduos = view.findViewById(R.id.rvResiduos);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        btnRecomendaciones = view.findViewById(R.id.btnRecomendaciones);
        btnEcoIa = view.findViewById(R.id.btnEcoIa);
        tvAvisoSinConexion.setVisibility(View.GONE);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvResiduos.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-04: ResiduoRepository.buscar(texto, categoría, ...) y CategoriaRepository para
        // crear los chips. Sin conexión, el aviso de datos guardados depende de Room (T-06, backlog).
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

    /** Se conecta al adaptador de residuos. */
    private void abrirDetalle(long idResiduo) {
        Navegador.desde(this).abrirPantalla(DetalleResiduoFragment.newInstance(idResiduo));
    }

    // 4. Eventos
    private void abrirRecomendaciones(View view) {
        Navegador.desde(this).abrirPantalla(new RecomendacionesFragment());
    }

    private void abrirEcoIa(View view) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).seleccionarPestana(R.id.nav_ecoia);
        }
    }
}
