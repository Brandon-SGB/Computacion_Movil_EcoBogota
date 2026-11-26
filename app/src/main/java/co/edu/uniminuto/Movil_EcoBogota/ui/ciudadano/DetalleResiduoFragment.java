package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;

/**
 * HU-04. Detalle de un residuo: nombre, imagen, categoría, color del recipiente, disposición
 * adecuada y puntos de reciclaje que lo reciben.
 */
public class DetalleResiduoFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID_RESIDUO = "id_residuo";

    private TextView tvNombre;
    private ImageView ivImagen;
    private TextView tvCategoria;
    private TextView tvColorRecipiente;
    private TextView tvDisposicion;
    private RecyclerView rvPuntos;
    private long idResiduo;

    public DetalleResiduoFragment() {
        super(R.layout.fragment_detalle_residuo);
    }

    public static DetalleResiduoFragment newInstance(long idResiduo) {
        DetalleResiduoFragment fragment = new DetalleResiduoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_RESIDUO, idResiduo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tvNombre = view.findViewById(R.id.tvNombre);
        ivImagen = view.findViewById(R.id.ivImagen);
        tvCategoria = view.findViewById(R.id.tvCategoria);
        tvColorRecipiente = view.findViewById(R.id.tvColorRecipiente);
        tvDisposicion = view.findViewById(R.id.tvDisposicion);
        rvPuntos = view.findViewById(R.id.rvPuntos);
        rvPuntos.setLayoutManager(new LinearLayoutManager(requireContext()));
        idResiduo = requireArguments().getLong(ARG_ID_RESIDUO);
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-04: ResiduoRepository.obtener(idResiduo, ...) y llenar las vistas;
        // el color se muestra con util/format/ColorRecipiente.
    }
}
