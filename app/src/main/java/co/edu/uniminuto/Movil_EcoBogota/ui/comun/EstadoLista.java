package co.edu.uniminuto.Movil_EcoBogota.ui.comun;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.edu.uniminuto.Movil_EcoBogota.R;

/**
 * Controla layout_estado_lista.xml (progreso, mensaje y botón Reintentar). Cada Fragment con lista
 * lo usa desde sus métodos mostrarCargando(), mostrarContenido(), mostrarVacio() y mostrarError().
 */
public class EstadoLista {

    // 1. Atributos
    private final View contenedor;
    private final ProgressBar pbCargando;
    private final TextView tvMensaje;
    private final Button btnReintentar;

    public EstadoLista(View contenedor) {
        this.contenedor = contenedor;
        this.pbCargando = contenedor.findViewById(R.id.pbCargando);
        this.tvMensaje = contenedor.findViewById(R.id.tvMensajeEstado);
        this.btnReintentar = contenedor.findViewById(R.id.btnReintentar);
    }

    // 2. Estados
    public void mostrarCargando() {
        contenedor.setVisibility(View.VISIBLE);
        pbCargando.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);
        btnReintentar.setVisibility(View.GONE);
    }

    public void mostrarMensaje(String mensaje, boolean permitirReintento) {
        contenedor.setVisibility(View.VISIBLE);
        pbCargando.setVisibility(View.GONE);
        tvMensaje.setVisibility(View.VISIBLE);
        tvMensaje.setText(mensaje);
        btnReintentar.setVisibility(permitirReintento ? View.VISIBLE : View.GONE);
    }

    public void ocultar() {
        contenedor.setVisibility(View.GONE);
    }

    // 3. Eventos
    public void setOnReintentar(Runnable accion) {
        btnReintentar.setOnClickListener(v -> accion.run());
    }
}
