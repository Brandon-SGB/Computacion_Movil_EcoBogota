package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.adapter.TramitesPagerAdapter;
import co.edu.uniminuto.Movil_EcoBogota.ui.formulario.NuevaSolicitudActivity;
import co.edu.uniminuto.Movil_EcoBogota.ui.formulario.NuevoReporteActivity;

/**
 * HU-07/12. TabLayout + ViewPager2 con Mis reportes y Mis solicitudes. El FAB "Nuevo" abre la
 * Activity del formulario según la pestaña; al volver con éxito se refresca la lista.
 */
public class MisTramitesFragment extends Fragment {

    // 1. Atributos
    private static final String PREFIJO_ETIQUETA_PAGINA = "f";

    private TabLayout tlTramites;
    private ViewPager2 vpTramites;
    private ExtendedFloatingActionButton fabNuevo;

    private final ActivityResultLauncher<Intent> formularioLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onResultadoFormulario);

    public MisTramitesFragment() {
        super(R.layout.fragment_mis_tramites);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        configurarPestanas();
        fabNuevo.setOnClickListener(this::abrirFormulario);
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tlTramites = view.findViewById(R.id.tlTramites);
        vpTramites = view.findViewById(R.id.vpTramites);
        fabNuevo = view.findViewById(R.id.fabNuevo);
    }

    // 3. Métodos auxiliares
    private void configurarPestanas() {
        vpTramites.setAdapter(new TramitesPagerAdapter(this));
        new TabLayoutMediator(tlTramites, vpTramites, (pestana, posicion) ->
                pestana.setText(posicion == TramitesPagerAdapter.PESTANA_REPORTES
                        ? R.string.tab_mis_reportes
                        : R.string.tab_mis_solicitudes)).attach();
    }

    private void refrescarPestanaActual() {
        // FragmentStateAdapter etiqueta cada página como "f" + id del ítem.
        Fragment pagina = getChildFragmentManager()
                .findFragmentByTag(PREFIJO_ETIQUETA_PAGINA + vpTramites.getCurrentItem());
        if (pagina instanceof MisReportesFragment) {
            ((MisReportesFragment) pagina).refrescar();
        } else if (pagina instanceof MisSolicitudesFragment) {
            ((MisSolicitudesFragment) pagina).refrescar();
        }
    }

    // 4. Eventos
    private void abrirFormulario(View view) {
        Class<? extends Activity> destino = vpTramites.getCurrentItem() == TramitesPagerAdapter.PESTANA_REPORTES
                ? NuevoReporteActivity.class
                : NuevaSolicitudActivity.class;
        formularioLauncher.launch(new Intent(requireContext(), destino));
    }

    // 5. Callbacks del sistema
    private void onResultadoFormulario(ActivityResult resultado) {
        if (resultado.getResultCode() == Activity.RESULT_OK) {
            refrescarPestanaActual();
        }
    }
}
