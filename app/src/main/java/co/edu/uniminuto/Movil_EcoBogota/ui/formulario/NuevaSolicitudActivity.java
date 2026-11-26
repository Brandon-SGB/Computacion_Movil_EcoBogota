package co.edu.uniminuto.Movil_EcoBogota.ui.formulario;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.format.FormatoFecha;
import co.edu.uniminuto.Movil_EcoBogota.util.location.UbicacionActual;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-11. Formulario de solicitud de recolección. Si se niega la ubicación, dirección manual.
 */
public class NuevaSolicitudActivity extends AppCompatActivity {

    // 1. Atributos
    private static final String ETIQUETA_FECHA = "selector_fecha";

    private MaterialToolbar toolbar;
    private TextInputLayout tilResiduo;
    private TextInputLayout tilCantidad;
    private TextInputLayout tilUnidad;
    private TextInputLayout tilDireccion;
    private TextInputLayout tilFecha;
    private MaterialAutoCompleteTextView actvResiduo;
    private MaterialAutoCompleteTextView actvUnidad;
    private TextInputEditText etCantidad;
    private TextInputEditText etDireccion;
    private TextInputEditText etFecha;
    private TextInputEditText etObservaciones;
    private Button btnUsarUbicacion;
    private TextView tvUbicacion;
    private Button btnEnviar;
    private ProgressBar pbCargando;

    private Location ubicacion;
    private Long fechaPreferida;

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_solicitud);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        cargarResiduos();
        toolbar.setNavigationOnClickListener(v -> finish());
        etFecha.setOnClickListener(this::elegirFecha);
        btnUsarUbicacion.setOnClickListener(this::usarMiUbicacion);
        btnEnviar.setOnClickListener(this::enviar);
    }

    // 2. Inicialización
    private void initObjects() {
        toolbar = findViewById(R.id.toolbar);
        tilResiduo = findViewById(R.id.tilResiduo);
        tilCantidad = findViewById(R.id.tilCantidad);
        tilUnidad = findViewById(R.id.tilUnidad);
        tilDireccion = findViewById(R.id.tilDireccion);
        tilFecha = findViewById(R.id.tilFecha);
        actvResiduo = findViewById(R.id.actvResiduo);
        actvUnidad = findViewById(R.id.actvUnidad);
        etCantidad = findViewById(R.id.etCantidad);
        etDireccion = findViewById(R.id.etDireccion);
        etFecha = findViewById(R.id.etFecha);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnEnviar = findViewById(R.id.btnEnviar);
        pbCargando = findViewById(R.id.pbCargando);
        pbCargando.setVisibility(View.GONE);
        actvUnidad.setSimpleItems(getResources().getStringArray(R.array.unidades_cantidad));
    }

    // 3. Métodos auxiliares
    private void cargarResiduos() {
        // TODO HU-11: ResiduoRepository.listar(...) y llenar actvResiduo.
    }

    private boolean validarFormulario() {
        boolean valido = true;
        tilCantidad.setError(null);
        tilUnidad.setError(null);
        tilDireccion.setError(null);
        tilFecha.setError(null);
        if (!ValidadorFormulario.esNumeroPositivo(etCantidad.getText())) {
            tilCantidad.setError(getString(R.string.error_cantidad_invalida));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(actvUnidad.getText())) {
            tilUnidad.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ubicacion == null && ValidadorFormulario.estaVacio(etDireccion.getText())) {
            tilDireccion.setError(getString(R.string.error_ubicacion_o_direccion));
            valido = false;
        }
        if (fechaPreferida == null) {
            tilFecha.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        return valido;
    }

    private void obtenerUbicacion() {
        tvUbicacion.setText(R.string.ubicacion_buscando);
        UbicacionActual.obtener(this, new UbicacionActual.Callback() {
            @Override
            public void onUbicacion(Location actual) {
                ubicacion = actual;
                tvUbicacion.setText(getString(R.string.ubicacion_coordenadas,
                        String.format(Locale.US, "%.5f", actual.getLatitude()),
                        String.format(Locale.US, "%.5f", actual.getLongitude())));
            }

            @Override
            public void onSinUbicacion() {
                tvUbicacion.setText(R.string.ubicacion_no_disponible);
            }
        });
    }

    // 4. Eventos
    private void elegirFecha(View view) {
        MaterialDatePicker<Long> selector = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.campo_fecha_preferida)
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();
        selector.addOnPositiveButtonClickListener(seleccion -> {
            fechaPreferida = seleccion;
            etFecha.setText(FormatoFecha.fecha(seleccion));
        });
        selector.show(getSupportFragmentManager(), ETIQUETA_FECHA);
    }

    private void usarMiUbicacion(View view) {
        if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosUbicacion());
        }
    }

    private void enviar(View view) {
        if (!validarFormulario()) {
            return;
        }
        // TODO HU-11: SolicitudRepository.crear(SolicitudRequest, RepositoryCallback<SolicitudResponse>);
        // en onSuccess: setResult(RESULT_OK) y finish() para que MisSolicitudesFragment se refresque.
        Snackbar.make(btnEnviar, getString(R.string.msg_pendiente_api, getString(R.string.hu_11)),
                Snackbar.LENGTH_LONG).show();
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else {
            tvUbicacion.setText(R.string.permiso_ubicacion_denegado_manual);
            etDireccion.requestFocus();
        }
    }
}
