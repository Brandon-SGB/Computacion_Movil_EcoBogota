package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.location.UbicacionActual;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-10. Crear o editar un punto de reciclaje: nombre, dirección, localidad, horario,
 * latitud/longitud (o "usar mi ubicación"), residuos que recibe (selección múltiple); desactivar.
 */
public class AdminPuntoFormFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID_PUNTO = "id_punto";

    private TextInputLayout tilNombre;
    private TextInputLayout tilDireccion;
    private TextInputLayout tilLocalidad;
    private TextInputLayout tilLatitud;
    private TextInputLayout tilLongitud;
    private TextInputEditText etNombre;
    private TextInputEditText etDireccion;
    private TextInputEditText etLocalidad;
    private TextInputEditText etHorario;
    private TextInputEditText etLatitud;
    private TextInputEditText etLongitud;
    private Button btnUsarMiUbicacion;
    private Button btnElegirResiduos;
    private TextView tvResiduosSeleccionados;
    private MaterialSwitch swActivo;
    private Button btnGuardar;
    private long idPunto;

    /** Nombres de residuos disponibles; se llenan desde la API (HU-10). */
    private final List<String> residuosDisponibles = new ArrayList<>();
    private boolean[] residuosMarcados = new boolean[0];

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);

    public AdminPuntoFormFragment() {
        super(R.layout.fragment_admin_punto_form);
    }

    public static AdminPuntoFormFragment newInstance(long idPunto) {
        AdminPuntoFormFragment fragment = new AdminPuntoFormFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_PUNTO, idPunto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnUsarMiUbicacion.setOnClickListener(this::usarMiUbicacion);
        btnElegirResiduos.setOnClickListener(this::elegirResiduos);
        btnGuardar.setOnClickListener(this::guardar);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tilNombre = view.findViewById(R.id.tilNombre);
        tilDireccion = view.findViewById(R.id.tilDireccion);
        tilLocalidad = view.findViewById(R.id.tilLocalidad);
        tilLatitud = view.findViewById(R.id.tilLatitud);
        tilLongitud = view.findViewById(R.id.tilLongitud);
        etNombre = view.findViewById(R.id.etNombre);
        etDireccion = view.findViewById(R.id.etDireccion);
        etLocalidad = view.findViewById(R.id.etLocalidad);
        etHorario = view.findViewById(R.id.etHorario);
        etLatitud = view.findViewById(R.id.etLatitud);
        etLongitud = view.findViewById(R.id.etLongitud);
        btnUsarMiUbicacion = view.findViewById(R.id.btnUsarMiUbicacion);
        btnElegirResiduos = view.findViewById(R.id.btnElegirResiduos);
        tvResiduosSeleccionados = view.findViewById(R.id.tvResiduosSeleccionados);
        swActivo = view.findViewById(R.id.swActivo);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        idPunto = requireArguments().getLong(ARG_ID_PUNTO);
        swActivo.setVisibility(idPunto == 0L ? View.GONE : View.VISIBLE);
        mostrarResiduosSeleccionados();
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-10: ResiduoRepository.listar(...) -> residuosDisponibles y residuosMarcados;
        // si no es nuevo, PuntoReciclajeRepository.obtener(idPunto, ...) para llenar el formulario.
    }

    private void mostrarResiduosSeleccionados() {
        List<String> elegidos = new ArrayList<>();
        for (int i = 0; i < residuosMarcados.length; i++) {
            if (residuosMarcados[i]) {
                elegidos.add(residuosDisponibles.get(i));
            }
        }
        tvResiduosSeleccionados.setText(elegidos.isEmpty()
                ? getString(R.string.residuos_ninguno)
                : String.join(", ", elegidos));
    }

    private boolean validarCoordenada(TextInputLayout til, TextInputEditText et, double limite) {
        til.setError(null);
        if (ValidadorFormulario.estaVacio(et.getText())) {
            til.setError(getString(R.string.error_campo_obligatorio));
            return false;
        }
        try {
            double valor = Double.parseDouble(et.getText().toString().trim());
            if (Math.abs(valor) <= limite) {
                return true;
            }
        } catch (NumberFormatException ignorada) {
            // Se reporta abajo como coordenada inválida.
        }
        til.setError(getString(R.string.error_coordenada_invalida));
        return false;
    }

    private boolean validarFormulario() {
        boolean valido = true;
        tilNombre.setError(null);
        tilDireccion.setError(null);
        tilLocalidad.setError(null);
        if (ValidadorFormulario.estaVacio(etNombre.getText())) {
            tilNombre.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etDireccion.getText())) {
            tilDireccion.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etLocalidad.getText())) {
            tilLocalidad.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        valido &= validarCoordenada(tilLatitud, etLatitud, 90);
        valido &= validarCoordenada(tilLongitud, etLongitud, 180);
        return valido;
    }

    private void obtenerUbicacion() {
        UbicacionActual.obtener(requireContext(), new UbicacionActual.Callback() {
            @Override
            public void onUbicacion(Location ubicacion) {
                etLatitud.setText(String.format(Locale.US, "%.6f", ubicacion.getLatitude()));
                etLongitud.setText(String.format(Locale.US, "%.6f", ubicacion.getLongitude()));
            }

            @Override
            public void onSinUbicacion() {
                if (getView() != null) {
                    Snackbar.make(getView(), R.string.ubicacion_no_disponible, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    // 4. Eventos
    private void usarMiUbicacion(View view) {
        if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosUbicacion());
        }
    }

    private void elegirResiduos(View view) {
        if (residuosDisponibles.isEmpty()) {
            Snackbar.make(view, R.string.vacio_residuos, Snackbar.LENGTH_SHORT).show();
            return;
        }
        boolean[] seleccion = residuosMarcados.clone();
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.campo_residuos_que_recibe)
                .setMultiChoiceItems(residuosDisponibles.toArray(new String[0]), seleccion,
                        (d, posicion, marcado) -> seleccion[posicion] = marcado)
                .setPositiveButton(R.string.btn_aceptar, (d, w) -> {
                    residuosMarcados = seleccion;
                    mostrarResiduosSeleccionados();
                })
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    private void guardar(View view) {
        if (!validarFormulario()) {
            return;
        }
        // TODO HU-10: PuntoReciclajeRepository.crear(...) o actualizar(idPunto, ...) con
        // etHorario, residuos marcados y swActivo; en onSuccess popBackStack().
        Snackbar.make(view, getString(R.string.msg_pendiente_api, getString(R.string.hu_10)),
                Snackbar.LENGTH_LONG).show();
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else if (getView() != null) {
            Snackbar.make(getView(), R.string.permiso_ubicacion_denegado_coordenadas, Snackbar.LENGTH_LONG).show();
        }
    }
}
