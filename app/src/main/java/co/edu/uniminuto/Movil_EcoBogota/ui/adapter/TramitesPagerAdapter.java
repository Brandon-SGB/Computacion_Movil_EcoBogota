package co.edu.uniminuto.Movil_EcoBogota.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano.MisReportesFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano.MisSolicitudesFragment;

/**
 * Pestañas de Mis trámites: 0 = Mis reportes, 1 = Mis solicitudes.
 */
public class TramitesPagerAdapter extends FragmentStateAdapter {

    // 1. Constantes
    public static final int PESTANA_REPORTES = 0;
    public static final int PESTANA_SOLICITUDES = 1;
    private static final int TOTAL_PESTANAS = 2;

    public TramitesPagerAdapter(@NonNull Fragment contenedor) {
        super(contenedor);
    }

    // 2. Pestañas
    @NonNull
    @Override
    public Fragment createFragment(int posicion) {
        return posicion == PESTANA_REPORTES ? new MisReportesFragment() : new MisSolicitudesFragment();
    }

    @Override
    public int getItemCount() {
        return TOTAL_PESTANAS;
    }
}
