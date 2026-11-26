package co.edu.uniminuto.Movil_EcoBogota.ui.formulario;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;
import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.location.UbicacionActual;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-06. Formulario de reporte. Es una Activity porque concentra permisos de cámara y ubicación,
 * captura de foto y resultado (sección 3.1). Si se niega la cámara se usa la galería (Photo Picker,
 * sin permiso); si se niega la ubicación se escribe la dirección a mano (sección 3.7).
 */
public class NuevoReporteActivity extends AppCompatActivity {

    // 1. Atributos
    private MaterialToolbar toolbar;
    private TextInputLayout tilTipoProblema;
    private TextInputLayout tilTitulo;
    private TextInputLayout tilDescripcion;
    private TextInputLayout tilDireccion;
    private MaterialAutoCompleteTextView actvTipoProblema;
    private TextInputEditText etTitulo;
    private TextInputEditText etDescripcion;
    private TextInputEditText etDireccion;
    private ImageView ivFoto;
    private Button btnTomarFoto;
    private Button btnElegirGaleria;
    private Button btnUsarUbicacion;
    private TextView tvUbicacion;
    private Button btnEnviar;
    private ProgressBar pbCargando;

    private Location ubicacion;
    private boolean pidiendoCamara;

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);
    private final ActivityResultLauncher<Void> camaraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), this::onFotoTomada);
    private final ActivityResultLauncher<PickVisualMediaRequest> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), this::onImagenElegida);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_reporte);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        cargarTiposProblema();
        toolbar.setNavigationOnClickListener(v -> finish());
        btnTomarFoto.setOnClickListener(this::tomarFoto);
        btnElegirGaleria.setOnClickListener(this::elegirDeGaleria);
        btnUsarUbicacion.setOnClickListener(this::usarMiUbicacion);
        btnEnviar.setOnClickListener(this::enviar);
    }

    // 2. Inicialización
    private void initObjects() {
        toolbar = findViewById(R.id.toolbar);
        tilTipoProblema = findViewById(R.id.tilTipoProblema);
        tilTitulo = findViewById(R.id.tilTitulo);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilDireccion = findViewById(R.id.tilDireccion);
        actvTipoProblema = findViewById(R.id.actvTipoProblema);
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etDireccion = findViewById(R.id.etDireccion);
        ivFoto = findViewById(R.id.ivFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnElegirGaleria = findViewById(R.id.btnElegirGaleria);
        btnUsarUbicacion = findViewById(R.id.btnUsarUbicacion);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnEnviar = findViewById(R.id.btnEnviar);
        pbCargando = findViewById(R.id.pbCargando);
        pbCargando.setVisibility(View.GONE);
    }

    // 3. Métodos auxiliares
    private void cargarTiposProblema() {
        // TODO HU-06: ReporteRepository.listarTiposProblema(...) y llenar actvTipoProblema.
    }

    private boolean validarFormulario() {
        boolean valido = true;
        tilTitulo.setError(null);
        tilDescripcion.setError(null);
        tilDireccion.setError(null);
        if (ValidadorFormulario.estaVacio(etTitulo.getText())) {
            tilTitulo.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etDescripcion.getText())) {
            tilDescripcion.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ubicacion == null && ValidadorFormulario.estaVacio(etDireccion.getText())) {
            tilDireccion.setError(getString(R.string.error_ubicacion_o_direccion));
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

    private void mostrarExplicacion(int mensaje, String[] permisos) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.permiso_explicacion_titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.btn_continuar, (d, w) -> permissionManager.getRequestPermissions(permisos))
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    // 4. Eventos
    private void tomarFoto(View view) {
        pidiendoCamara = true;
        if (permissionManager.checkHasPermissions(PermissionManager.getPermisosCamara())) {
            camaraLauncher.launch(null);
        } else if (permissionManager.debeMostrarExplicacion(PermissionManager.getPermisosCamara())) {
            mostrarExplicacion(R.string.permiso_camara_explicacion_reporte, PermissionManager.getPermisosCamara());
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosCamara());
        }
    }

    private void elegirDeGaleria(View view) {
        galeriaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void usarMiUbicacion(View view) {
        pidiendoCamara = false;
        if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else if (permissionManager.debeMostrarExplicacion(PermissionManager.getPermisosUbicacion())) {
            mostrarExplicacion(R.string.permiso_ubicacion_explicacion_reporte, PermissionManager.getPermisosUbicacion());
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosUbicacion());
        }
    }

    private void enviar(View view) {
        if (!validarFormulario()) {
            return;
        }
        // TODO HU-06: ReporteRepository.crear(ReporteRequest, foto, RepositoryCallback<ReporteResponse>);
        // en onSuccess: setResult(RESULT_OK) y finish() para que MisReportesFragment se refresque.
        Snackbar.make(btnEnviar, getString(R.string.msg_pendiente_api, getString(R.string.hu_06)),
                Snackbar.LENGTH_LONG).show();
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        if (pidiendoCamara) {
            if (todosConcedidos) {
                camaraLauncher.launch(null);
            } else {
                Snackbar.make(btnEnviar, R.string.permiso_camara_denegado_galeria, Snackbar.LENGTH_LONG).show();
            }
        } else if (permissionManager.tieneUbicacion()) {
            obtenerUbicacion();
        } else {
            tvUbicacion.setText(R.string.permiso_ubicacion_denegado_manual);
            etDireccion.requestFocus();
        }
    }

    private void onFotoTomada(Bitmap foto) {
        if (foto != null) {
            ivFoto.setImageBitmap(foto);
            ivFoto.setVisibility(View.VISIBLE);
        }
    }

    private void onImagenElegida(Uri uri) {
        if (uri != null) {
            ivFoto.setImageURI(uri);
            ivFoto.setVisibility(View.VISIBLE);
        }
    }
}
