package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.graphics.Bitmap;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;
import co.edu.uniminuto.Movil_EcoBogota.util.permissions.PermissionManager;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-17/18. Consulta a EcoIA por foto o texto; muestra categoría, recipiente y recomendación.
 * Si se niega la cámara, se usa la galería o la consulta por texto (sección 3.7).
 */
public class EcoIaFragment extends Fragment {

    // 1. Atributos
    private Button btnTomarFoto;
    private Button btnElegirImagen;
    private ImageView ivFoto;
    private TextInputLayout tilConsulta;
    private TextInputEditText etConsulta;
    private Button btnConsultar;
    private MaterialCardView cvResultado;
    private Button btnHistorial;

    private final PermissionManager permissionManager = new PermissionManager(this, this::onResultadoPermisos);
    private final ActivityResultLauncher<Void> camaraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(), this::onFotoTomada);
    private final ActivityResultLauncher<PickVisualMediaRequest> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(), this::onImagenElegida);

    public EcoIaFragment() {
        super(R.layout.fragment_ecoia);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnTomarFoto.setOnClickListener(this::tomarFoto);
        btnElegirImagen.setOnClickListener(this::elegirImagen);
        btnConsultar.setOnClickListener(this::consultar);
        btnHistorial.setOnClickListener(this::abrirHistorial);
    }

    // 2. Inicialización
    private void initObjects(View view) {
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        btnElegirImagen = view.findViewById(R.id.btnElegirImagen);
        ivFoto = view.findViewById(R.id.ivFoto);
        tilConsulta = view.findViewById(R.id.tilConsulta);
        etConsulta = view.findViewById(R.id.etConsulta);
        btnConsultar = view.findViewById(R.id.btnConsultar);
        cvResultado = view.findViewById(R.id.cvResultado);
        btnHistorial = view.findViewById(R.id.btnHistorial);
        ivFoto.setVisibility(View.GONE);
        cvResultado.setVisibility(View.GONE);
    }

    // 3. Métodos auxiliares
    private boolean hayImagen() {
        return ivFoto.getVisibility() == View.VISIBLE;
    }

    // 4. Eventos
    private void tomarFoto(View view) {
        if (permissionManager.checkHasPermissions(PermissionManager.getPermisosCamara())) {
            camaraLauncher.launch(null);
        } else {
            permissionManager.getRequestPermissions(PermissionManager.getPermisosCamara());
        }
    }

    private void elegirImagen(View view) {
        galeriaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void consultar(View view) {
        tilConsulta.setError(null);
        if (!hayImagen() && ValidadorFormulario.estaVacio(etConsulta.getText())) {
            tilConsulta.setError(getString(R.string.error_ecoia_sin_datos));
            return;
        }
        // TODO HU-17: EcoIaRepository.consultar(ConsultaEcoIaRequest, ...) y llenar cvResultado.
        Snackbar.make(view, getString(R.string.msg_pendiente_api, getString(R.string.hu_17)),
                Snackbar.LENGTH_LONG).show();
    }

    private void abrirHistorial(View view) {
        Navegador.desde(this).abrirPantalla(new HistorialEcoIaFragment());
    }

    // 5. Callbacks del sistema
    private void onResultadoPermisos(boolean todosConcedidos, Map<String, Boolean> resultado) {
        if (todosConcedidos) {
            camaraLauncher.launch(null);
        } else {
            Snackbar.make(requireView(), R.string.permiso_camara_denegado_ecoia, Snackbar.LENGTH_LONG).show();
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
