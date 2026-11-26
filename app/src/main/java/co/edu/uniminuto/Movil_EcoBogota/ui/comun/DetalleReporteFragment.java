package co.edu.uniminuto.Movil_EcoBogota.ui.comun;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.admin.CambioEstadoDialogFragment;

/**
 * HU-07/08. Detalle compartido entre ciudadano y administrador: Foto, descripción, ubicación, estado e historial de estados.
 * En modo admin muestra el botón "Cambiar estado".
 */
public class DetalleReporteFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID = "id_reporte";
    private static final String ARG_MODO_ADMIN = "modo_admin";
    private static final String ETIQUETA_DIALOGO = "cambio_estado";

    private ImageView ivFoto;
    private TextView tvTitulo;
    private TextView tvTipo;
    private TextView tvDescripcion;
    private TextView tvUbicacion;
    private Chip chipEstado;
    private RecyclerView rvHistorial;
    private Button btnCambiarEstado;
    private long idReporte;
    private boolean modoAdmin;

    public DetalleReporteFragment() {
        super(R.layout.fragment_detalle_reporte);
    }

    public static DetalleReporteFragment newInstance(long idReporte, boolean modoAdmin) {
        DetalleReporteFragment fragment = new DetalleReporteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, idReporte);
        args.putBoolean(ARG_MODO_ADMIN, modoAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnCambiarEstado.setOnClickListener(this::abrirCambioEstado);
        getChildFragmentManager().setFragmentResultListener(CambioEstadoDialogFragment.RESULTADO,
                getViewLifecycleOwner(), (clave, resultado) -> onEstadoCambiado(resultado));
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        ivFoto = view.findViewById(R.id.ivFoto);
        tvTitulo = view.findViewById(R.id.tvTitulo);
        tvTipo = view.findViewById(R.id.tvTipo);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        chipEstado = view.findViewById(R.id.chipEstado);
        rvHistorial = view.findViewById(R.id.rvHistorial);
        btnCambiarEstado = view.findViewById(R.id.btnCambiarEstado);
        rvHistorial.setLayoutManager(new LinearLayoutManager(requireContext()));
        idReporte = requireArguments().getLong(ARG_ID);
        modoAdmin = requireArguments().getBoolean(ARG_MODO_ADMIN);
        btnCambiarEstado.setVisibility(modoAdmin ? View.VISIBLE : View.GONE);
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-07/08: ReporteRepository.obtener(id, ...) y ReporteRepository.listarHistorial(id, ...).
    }

    // 4. Eventos
    private void abrirCambioEstado(View view) {
        CambioEstadoDialogFragment.newInstance(CambioEstadoDialogFragment.TIPO_REPORTE, idReporte)
                .show(getChildFragmentManager(), ETIQUETA_DIALOGO);
    }

    // 5. Callbacks
    private void onEstadoCambiado(Bundle resultado) {
        String estado = resultado.getString(CambioEstadoDialogFragment.EXTRA_ESTADO);
        // TODO HU-07/08: enviar CambioEstadoRequest(estado, comentario) y recargar el historial.
        Snackbar.make(requireView(), getString(R.string.msg_pendiente_api, estado), Snackbar.LENGTH_LONG).show();
    }
}
