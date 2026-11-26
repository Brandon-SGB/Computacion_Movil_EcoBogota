package co.edu.uniminuto.Movil_EcoBogota.ui.comun;

import androidx.fragment.app.Fragment;

/**
 * Lo implementan las Activities contenedoras (MainActivity y AdminActivity) para que los Fragments
 * abran pantallas de detalle o formulario con "replace + addToBackStack" (sección 3.4).
 */
public interface Navegador {

    void abrirPantalla(Fragment destino);

    static Navegador desde(Fragment fragment) {
        return (Navegador) fragment.requireActivity();
    }
}
