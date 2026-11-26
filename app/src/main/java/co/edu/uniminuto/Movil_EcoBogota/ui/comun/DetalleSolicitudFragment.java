package co.edu.uniminuto.Movil_EcoBogota.ui.comun;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
 * HU-12/13. Detalle compartido entre ciudadano y administrador: Residuo, cantidad, dirección, fecha preferida, estado e historial de estados.
 * En modo admin muestra el botón "Cambiar estado".
 */
public class DetalleSolicitudFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID = "id_solicitud";
    private static final String ARG_MODO_ADMIN = "modo_admin";
    private static final String ETIQUETA_DIALOGO = "cambio_estado";

    private TextView tvResiduo;
    private TextView tvCantidad;
    private TextView tvDireccion;
    private TextView tvFechaPreferida;
    private TextView tvObservaciones;
    private Chip chipEstado;
    private RecyclerView rvHistorial;
    private Button btnCambiarEstado;
    private long idSolicitud;
    private boolean modoAdmin;

    public DetalleSolicitudFragment() {
        super(R.layout.fragment_detalle_solicitud);
    }

    public static DetalleSolicitudFragment newInstance(long idSolicitud, boolean modoAdmin) {
        DetalleSolicitudFragment fragment = new DetalleSolicitudFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, idSolicitud);
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
        tvResiduo = view.findViewById(R.id.tvResiduo);
        tvCantidad = view.findViewById(R.id.tvCantidad);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvFechaPreferida = view.findViewById(R.id.tvFechaPreferida);
        tvObservaciones = view.findViewById(R.id.tvObservaciones);
        chipEstado = view.findViewById(R.id.chipEstado);
        rvHistorial = view.findViewById(R.id.rvHistorial);
        btnCambiarEstado = view.findViewById(R.id.btnCambiarEstado);
        rvHistorial.setLayoutManager(new LinearLayoutManager(requireContext()));
        idSolicitud = requireArguments().getLong(ARG_ID);
        modoAdmin = requireArguments().getBoolean(ARG_MODO_ADMIN);
        btnCambiarEstado.setVisibility(modoAdmin ? View.VISIBLE : View.GONE);
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-12/13: SolicitudRepository.obtener(id, ...) y SolicitudRepository.listarHistorial(id, ...).
    }

    // 4. Eventos
    private void abrirCambioEstado(View view) {
        CambioEstadoDialogFragment.newInstance(CambioEstadoDialogFragment.TIPO_SOLICITUD, idSolicitud)
                .show(getChildFragmentManager(), ETIQUETA_DIALOGO);
    }

    // 5. Callbacks
    private void onEstadoCambiado(Bundle resultado) {
        String estado = resultado.getString(CambioEstadoDialogFragment.EXTRA_ESTADO);
        // TODO HU-12/13: enviar CambioEstadoRequest(estado, comentario) y recargar el historial.
        Snackbar.make(requireView(), getString(R.string.msg_pendiente_api, estado), Snackbar.LENGTH_LONG).show();
    }
}
