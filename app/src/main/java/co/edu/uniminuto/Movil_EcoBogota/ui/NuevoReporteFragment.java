package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.Map;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Formulario de nuevo reporte (HU-06)
public class NuevoReporteFragment extends Fragment {

    //Permisos de ubicación
    private static final String[] PERMISOS_UBICACION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //Vistas
    private Spinner spTipoProblema;
    private EditText etDescripcion;
    private EditText etDireccion;
    private Button btnGps;
    private Button btnEnviarReporte;
    private ProgressBar pbNuevoReporte;
    private TextView tvEstadoNuevoReporte;
    private TextView tvUbicacionGps;
    private Button btnTomarFoto;
    private Button btnElegirGaleria;
    private Button btnAjustesCamara;
    private ImageView ivFotoReporte;
    private Button btnQuitarFoto;

    //ViewModel de la pantalla
    private NuevoReporteViewModel viewModel;

    //Solicitud del permiso de ubicación; en un Fragment la respuesta llega por este registro
    private final ActivityResultLauncher<String[]> solicitudPermisoUbicacion =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    this::onRespuestaPermisoUbicacion);

    //Solicitud del permiso de cámara, solo al pulsar Tomar foto (D-4)
    private final ActivityResultLauncher<String> solicitudPermisoCamara =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    this::onRespuestaPermisoCamara);

    //Selector de fotos del sistema (Photo Picker); no necesita permisos de almacenamiento (D-8)
    private final ActivityResultLauncher<PickVisualMediaRequest> selectorGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                    this::onImagenElegida);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Recibe la ruta de la foto tomada en CamaraFragment
        getParentFragmentManager().setFragmentResultListener(CamaraFragment.RESULTADO_FOTO, this,
                (clave, datos) -> {
                    if (viewModel != null) {
                        viewModel.setRutaFoto(datos.getString(CamaraFragment.CLAVE_RUTA_FOTO));
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nuevo_reporte, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnGps.setOnClickListener(this::usarGps);
        btnEnviarReporte.setOnClickListener(this::enviarReporte);
        btnTomarFoto.setOnClickListener(this::tomarFoto);
        btnElegirGaleria.setOnClickListener(this::elegirDeGaleria);
        btnQuitarFoto.setOnClickListener(this::quitarFoto);
        btnAjustesCamara.setOnClickListener(this::abrirAjustes);
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        spTipoProblema = view.findViewById(R.id.spTipoProblema);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etDireccion = view.findViewById(R.id.etDireccion);
        btnGps = view.findViewById(R.id.btnGps);
        btnEnviarReporte = view.findViewById(R.id.btnEnviarReporte);
        pbNuevoReporte = view.findViewById(R.id.pbNuevoReporte);
        tvEstadoNuevoReporte = view.findViewById(R.id.tvEstadoNuevoReporte);
        tvUbicacionGps = view.findViewById(R.id.tvUbicacionGps);
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto);
        btnElegirGaleria = view.findViewById(R.id.btnElegirGaleria);
        btnAjustesCamara = view.findViewById(R.id.btnAjustesCamara);
        ivFotoReporte = view.findViewById(R.id.ivFotoReporte);
        btnQuitarFoto = view.findViewById(R.id.btnQuitarFoto);
        //Los tipos de problema llegan del backend; mientras tanto la lista queda deshabilitada
        spTipoProblema.setEnabled(false);
        viewModel = new ViewModelProvider(this).get(NuevoReporteViewModel.class);
    }

    //Método para observar el estado, el mensaje y los errores de los campos
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoNuevoReporte::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
        viewModel.getErrorDescripcion().observe(getViewLifecycleOwner(), etDescripcion::setError);
        viewModel.getErrorDireccion().observe(getViewLifecycleOwner(), etDireccion::setError);
        viewModel.getTextoUbicacionGps().observe(getViewLifecycleOwner(), this::mostrarUbicacionGps);
        viewModel.getRutaFoto().observe(getViewLifecycleOwner(), this::mostrarFoto);
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        boolean cargando = estado == EstadoUi.CARGANDO;
        pbNuevoReporte.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnEnviarReporte.setEnabled(!cargando);
        tvEstadoNuevoReporte.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }

    //Método del botón Enviar
    private void enviarReporte(View view) {
        viewModel.enviar(etDescripcion.getText().toString(), etDireccion.getText().toString());
    }

    //Método del botón GPS: comprueba el permiso y lo solicita si hace falta
    private void usarGps(View view) {
        try {
            if (tienePermisoUbicacion()) {
                viewModel.leerUbicacionGps();
            } else {
                solicitudPermisoUbicacion.launch(PERMISOS_UBICACION);
            }
        } catch (Exception e) {
            Log.i("ubicacion", "error con el permiso de ubicación " + e.getMessage());
        }
    }

    //Método que recibe la respuesta del usuario al permiso de ubicación
    private void onRespuestaPermisoUbicacion(Map<String, Boolean> resultado) {
        if (resultado.containsValue(Boolean.TRUE)) {
            viewModel.leerUbicacionGps();
        } else {
            Toast.makeText(requireContext(), R.string.permiso_ubicacion_denegado, Toast.LENGTH_LONG).show();
            etDireccion.requestFocus();
        }
    }

    //Método para comprobar si se concedió alguno de los permisos de ubicación
    private boolean tienePermisoUbicacion() {
        for (String permiso : PERMISOS_UBICACION) {
            if (ContextCompat.checkSelfPermission(requireContext(), permiso) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    //Método del botón Elegir de la galería: abre el Photo Picker solo con imágenes
    private void elegirDeGaleria(View view) {
        try {
            selectorGaleria.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } catch (Exception e) {
            Log.i("galeria", "error al abrir el selector de fotos " + e.getMessage());
        }
    }

    //Método que recibe la imagen elegida; si el usuario cancela llega null y no cambia nada
    private void onImagenElegida(Uri imagen) {
        if (imagen != null && !viewModel.usarFotoGaleria(imagen)) {
            Toast.makeText(requireContext(), R.string.galeria_error, Toast.LENGTH_LONG).show();
        }
    }

    //Método del botón Tomar foto: el permiso de cámara se pide solo en este momento (D-4)
    private void tomarFoto(View view) {
        try {
            if (!requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Toast.makeText(requireContext(), R.string.camara_no_disponible, Toast.LENGTH_LONG).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                mostrarExplicacionCamara();
            } else {
                solicitudPermisoCamara.launch(Manifest.permission.CAMERA);
            }
        } catch (Exception e) {
            Log.i("camara", "error con el permiso de cámara " + e.getMessage());
        }
    }

    //Método que recibe la respuesta del usuario al permiso de cámara
    private void onRespuestaPermisoCamara(boolean concedido) {
        NuevoReporteViewModel.AccionPermisoCamara accion = NuevoReporteViewModel.decidirAccionPermisoCamara(
                concedido, shouldShowRequestPermissionRationale(Manifest.permission.CAMERA));
        boolean irAAjustes = accion == NuevoReporteViewModel.AccionPermisoCamara.IR_A_AJUSTES;
        btnAjustesCamara.setVisibility(irAAjustes ? View.VISIBLE : View.GONE);
        if (accion == NuevoReporteViewModel.AccionPermisoCamara.ABRIR_CAMARA) {
            abrirCamara();
        } else if (accion == NuevoReporteViewModel.AccionPermisoCamara.EXPLICAR_Y_REINTENTAR) {
            mostrarExplicacionCamara();
        } else {
            Toast.makeText(requireContext(), R.string.permiso_camara_permanente, Toast.LENGTH_LONG).show();
        }
    }

    //Explicación del uso de la cámara con opción de reintentar (D-5); el resto del formulario sigue disponible
    private void mostrarExplicacionCamara() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.titulo_permiso_camara)
                .setMessage(R.string.explicacion_permiso_camara)
                .setPositiveButton(R.string.btn_reintentar,
                        (dialogo, boton) -> solicitudPermisoCamara.launch(Manifest.permission.CAMERA))
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    //Método del botón de ajustes: abre los detalles de la app para conceder el permiso (D-6)
    private void abrirAjustes(View view) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", requireContext().getPackageName(), null));
            startActivity(intent);
        } catch (Exception e) {
            Log.i("camara", "error al abrir los ajustes " + e.getMessage());
        }
    }

    //Método para abrir la pantalla de cámara; al terminar se vuelve al formulario
    private void abrirCamara() {
        btnAjustesCamara.setVisibility(View.GONE);
        try {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frContenedor, new CamaraFragment())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.i("navegacion", "error al abrir la cámara " + e.getMessage());
        }
    }

    //Método del botón Quitar foto
    private void quitarFoto(View view) {
        viewModel.quitarFoto();
    }

    //Método para mostrar la miniatura de la foto tomada; Quitar foto solo se ve si hay foto
    private void mostrarFoto(String ruta) {
        btnQuitarFoto.setVisibility(View.GONE);
        if (ruta == null) {
            ivFotoReporte.setVisibility(View.GONE);
            return;
        }
        try {
            BitmapFactory.Options opciones = new BitmapFactory.Options();
            opciones.inSampleSize = 4;
            Bitmap foto = BitmapFactory.decodeFile(ruta, opciones);
            if (foto == null) {
                ivFotoReporte.setVisibility(View.GONE);
                return;
            }
            ivFotoReporte.setImageBitmap(rotarSegunExif(foto, ruta));
            ivFotoReporte.setVisibility(View.VISIBLE);
            btnQuitarFoto.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.i("camara", "error al mostrar la foto " + e.getMessage());
            ivFotoReporte.setVisibility(View.GONE);
        }
    }

    //La cámara guarda la orientación en los datos EXIF; se aplica para mostrar la miniatura derecha
    private Bitmap rotarSegunExif(Bitmap foto, String ruta) throws IOException {
        int orientacion = new ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int grados;
        if (orientacion == ExifInterface.ORIENTATION_ROTATE_90) {
            grados = 90;
        } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_180) {
            grados = 180;
        } else if (orientacion == ExifInterface.ORIENTATION_ROTATE_270) {
            grados = 270;
        } else {
            return foto;
        }
        Matrix matriz = new Matrix();
        matriz.postRotate(grados);
        return Bitmap.createBitmap(foto, 0, 0, foto.getWidth(), foto.getHeight(), matriz, true);
    }

    //Método para mostrar el resultado de la lectura del GPS en el formulario
    private void mostrarUbicacionGps(String texto) {
        tvUbicacionGps.setText(texto);
        tvUbicacionGps.setVisibility(texto == null ? View.GONE : View.VISIBLE);
    }
}
