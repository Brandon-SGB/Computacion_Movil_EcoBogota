package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.EstadoLista;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;

/**
 * HU-09. Puntos de reciclaje: alternar Lista/Mapa, filtro por residuo y "Cerca de mí" (si hay
 * permiso de ubicación; si se niega, la lista se muestra sin ordenar por distancia).
 */
public class PuntosReciclajeFragment extends Fragment {

    // 1. Atributos
    private MaterialButtonToggleGroup tgVista;
    private MaterialAutoCompleteTextView actvFiltroResiduo;
    private Button btnCercaDeMi;
    private RecyclerView rvPuntos;
    private View vMapa;
    private EstadoLista estadoLista;
    private boolean ordenarPorDistancia;

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);

    public PuntosReciclajeFragment() {
        super(R.layout.fragment_puntos_reciclaje);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarLista();
        estadoLista.setOnReintentar(this::cargarDatos);
        tgVista.addOnButtonCheckedListener((grupo, id, marcado) -> {
            if (marcado) {
                cambiarVista(id == R.id.btnVistaMapa);
            }
        });
        btnCercaDeMi.setOnClickListener(this::buscarCercaDeMi);
        actvFiltroResiduo.setOnItemClickListener((padre, v, posicion, id) -> cargarDatos());
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tgVista = view.findViewById(R.id.tgVista);
        actvFiltroResiduo = view.findViewById(R.id.actvFiltroResiduo);
        btnCercaDeMi = view.findViewById(R.id.btnCercaDeMi);
        rvPuntos = view.findViewById(R.id.rvPuntos);
        vMapa = view.findViewById(R.id.vMapa);
        estadoLista = new EstadoLista(view.findViewById(R.id.layoutEstado));
        tgVista.check(R.id.btnVistaLista);
        vMapa.setVisibility(View.GONE);
    }

    // 3. Métodos auxiliares
    private void configurarLista() {
        rvPuntos.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void cambiarVista(boolean mostrarMapa) {
        // TODO HU-09: reemplazar vMapa por un SupportMapFragment cuando exista la API key de Maps.
        vMapa.setVisibility(mostrarMapa ? View.VISIBLE : View.GONE);
        rvPuntos.setVisibility(mostrarMapa ? View.GONE : View.VISIBLE);
        if (mostrarMapa) {
            estadoLista.ocultar();
        } else {
            cargarDatos();
        }
    }

    private void cargarDatos() {
        mostrarCargando();
        // TODO HU-09: PuntoReciclajeRepository.listar(residuo, ...) y, si ordenarPorDistancia,
        // ordenar con util/location/UbicacionActual.
        mostrarVacio();
    }

    private void mostrarCargando() {
        rvPuntos.setVisibility(View.GONE);
        estadoLista.mostrarCargando();
    }

    private void mostrarContenido() {
        rvPuntos.setVisibility(View.VISIBLE);
        estadoLista.ocultar();
    }

    private void mostrarVacio() {
        rvPuntos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(getString(R.string.vacio_puntos), false);
    }

    private void mostrarError(String mensaje) {
        rvPuntos.setVisibility(View.GONE);
        estadoLista.mostrarMensaje(mensaje, true);
    }

    /** Se conecta al adaptador de puntos. */
    private void abrirDetalle(long idPunto) {
        Navegador.desde(this).abrirPantalla(DetallePuntoFragment.newInstance(idPunto));
    }

    // 4. Eventos
    private void buscarCercaDeMi(View view) {
        if (permissionManager.tieneUbicacion()) {
            ordenarPorDistancia = true;
            cargarDatos();
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosUbicacion());
        }
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        ordenarPorDistancia = permissionManager.tieneUbicacion();
        if (!ordenarPorDistancia) {
            Snackbar.make(requireView(), R.string.permiso_ubicacion_denegado_lista, Snackbar.LENGTH_LONG).show();
        }
        cargarDatos();
    }
}
