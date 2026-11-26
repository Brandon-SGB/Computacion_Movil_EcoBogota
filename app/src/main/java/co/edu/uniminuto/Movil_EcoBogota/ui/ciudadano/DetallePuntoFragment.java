package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import co.edu.uniminuto.Movil_EcoBogota.R;

/**
 * HU-09. Detalle del punto: nombre, dirección, localidad, horario, residuos que recibe y
 * "Cómo llegar" (abre Google Maps).
 */
public class DetallePuntoFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID_PUNTO = "id_punto";
    private static final String URI_NAVEGACION = "geo:0,0?q=%f,%f";

    private TextView tvNombre;
    private TextView tvDireccion;
    private TextView tvLocalidad;
    private TextView tvHorario;
    private TextView tvResiduos;
    private Button btnComoLlegar;
    private long idPunto;
    private Double latitud;
    private Double longitud;

    public DetallePuntoFragment() {
        super(R.layout.fragment_detalle_punto);
    }

    public static DetallePuntoFragment newInstance(long idPunto) {
        DetallePuntoFragment fragment = new DetallePuntoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_PUNTO, idPunto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnComoLlegar.setOnClickListener(this::abrirComoLlegar);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tvNombre = view.findViewById(R.id.tvNombre);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvLocalidad = view.findViewById(R.id.tvLocalidad);
        tvHorario = view.findViewById(R.id.tvHorario);
        tvResiduos = view.findViewById(R.id.tvResiduos);
        btnComoLlegar = view.findViewById(R.id.btnComoLlegar);
        idPunto = requireArguments().getLong(ARG_ID_PUNTO);
        btnComoLlegar.setEnabled(false);
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-09: PuntoReciclajeRepository.obtener(idPunto, ...), llenar las vistas,
        // guardar latitud/longitud y habilitar btnComoLlegar.
    }

    // 4. Eventos
    private void abrirComoLlegar(View view) {
        if (latitud == null || longitud == null) {
            return;
        }
        Uri destino = Uri.parse(String.format(Locale.US, URI_NAVEGACION, latitud, longitud));
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, destino));
        } catch (ActivityNotFoundException e) {
            Snackbar.make(view, R.string.error_sin_app_mapas, Snackbar.LENGTH_LONG).show();
        }
    }
}
