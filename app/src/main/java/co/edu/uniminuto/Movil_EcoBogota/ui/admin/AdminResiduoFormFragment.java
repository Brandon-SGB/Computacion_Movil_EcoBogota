package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-05. Crear o editar un residuo: nombre, categoría, descripción/disposición e imagen;
 * desactivar (solo al editar). id = 0 significa "nuevo".
 */
public class AdminResiduoFormFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID_RESIDUO = "id_residuo";

    private TextInputLayout tilNombre;
    private TextInputLayout tilCategoria;
    private TextInputLayout tilDisposicion;
    private TextInputEditText etNombre;
    private MaterialAutoCompleteTextView actvCategoria;
    private TextInputEditText etDisposicion;
    private ImageView ivImagen;
    private Button btnElegirImagen;
    private MaterialSwitch swActivo;
    private Button btnGuardar;
    private long idResiduo;
    private Uri imagenSeleccionada;

    private final ActivityResultLauncher<PickVisualMediaRequest> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), this::onImagenElegida);

    public AdminResiduoFormFragment() {
        super(R.layout.fragment_admin_residuo_form);
    }

    public static AdminResiduoFormFragment newInstance(long idResiduo) {
        AdminResiduoFormFragment fragment = new AdminResiduoFormFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_RESIDUO, idResiduo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnElegirImagen.setOnClickListener(this::elegirImagen);
        btnGuardar.setOnClickListener(this::guardar);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tilNombre = view.findViewById(R.id.tilNombre);
        tilCategoria = view.findViewById(R.id.tilCategoria);
        tilDisposicion = view.findViewById(R.id.tilDisposicion);
        etNombre = view.findViewById(R.id.etNombre);
        actvCategoria = view.findViewById(R.id.actvCategoria);
        etDisposicion = view.findViewById(R.id.etDisposicion);
        ivImagen = view.findViewById(R.id.ivImagen);
        btnElegirImagen = view.findViewById(R.id.btnElegirImagen);
        swActivo = view.findViewById(R.id.swActivo);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        idResiduo = requireArguments().getLong(ARG_ID_RESIDUO);
        swActivo.setVisibility(esNuevo() ? View.GONE : View.VISIBLE);
        ivImagen.setVisibility(View.GONE);
    }

    // 3. Métodos auxiliares
    private boolean esNuevo() {
        return idResiduo == 0L;
    }

    private void cargarDatos() {
        // TODO HU-05: CategoriaRepository.listar(...) para actvCategoria y, si no es nuevo,
        // ResiduoRepository.obtener(idResiduo, ...) para llenar el formulario.
    }

    private boolean validarFormulario() {
        boolean valido = true;
        tilNombre.setError(null);
        tilCategoria.setError(null);
        tilDisposicion.setError(null);
        if (ValidadorFormulario.estaVacio(etNombre.getText())) {
            tilNombre.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(actvCategoria.getText())) {
            tilCategoria.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etDisposicion.getText())) {
            tilDisposicion.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        return valido;
    }

    // 4. Eventos
    private void elegirImagen(View view) {
        galeriaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void guardar(View view) {
        if (!validarFormulario()) {
            return;
        }
        // TODO HU-05: ResiduoRepository.crear(...) o actualizar(idResiduo, ...) y popBackStack().
        Snackbar.make(view, getString(R.string.msg_pendiente_api, getString(R.string.hu_05)),
                Snackbar.LENGTH_LONG).show();
    }

    // 5. Callbacks del sistema
    private void onImagenElegida(Uri uri) {
        if (uri != null) {
            imagenSeleccionada = uri;
            ivImagen.setImageURI(uri);
            ivImagen.setVisibility(View.VISIBLE);
        }
    }
}
