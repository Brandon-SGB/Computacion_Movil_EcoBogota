package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Pantalla de cámara para tomar la foto del reporte (HU-06) con CameraX
public class CamaraFragment extends Fragment {

    //Clave del resultado que recibe el formulario del reporte
    public static final String RESULTADO_FOTO = "resultado_foto_reporte";
    public static final String CLAVE_RUTA_FOTO = "ruta_foto";

    //Archivo temporal de la foto; se guarda en la caché de la app, no en la galería
    static final String CARPETA_FOTOS = "fotos";
    static final String NOMBRE_FOTO = "foto_reporte.jpg";

    //Vistas
    private PreviewView pvCamara;
    private Button btnCapturar;
    private Button btnCancelarCamara;

    //Casos de uso de CameraX
    private ImageCapture imageCapture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camara, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnCapturar.setOnClickListener(this::capturarFoto);
        btnCancelarCamara.setOnClickListener(this::cancelar);
        iniciarCamara();
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        pvCamara = view.findViewById(R.id.pvCamara);
        btnCapturar = view.findViewById(R.id.btnCapturar);
        btnCancelarCamara = view.findViewById(R.id.btnCancelarCamara);
    }

    //Método para iniciar la cámara trasera con vista previa y captura
    private void iniciarCamara() {
        ListenableFuture<ProcessCameraProvider> futuro = ProcessCameraProvider.getInstance(requireContext());
        futuro.addListener(() -> {
            try {
                if (!isAdded() || getView() == null) {
                    return;
                }
                ProcessCameraProvider proveedor = futuro.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(pvCamara.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                proveedor.unbindAll();
                //Aquí se agregará ImageAnalysis como tercer caso de uso cuando se defina el análisis de imagen
                proveedor.bindToLifecycle(getViewLifecycleOwner(), CameraSelector.DEFAULT_BACK_CAMERA,
                        preview, imageCapture);
            } catch (Exception e) {
                Log.i("camara", "error al iniciar la cámara " + e.getMessage());
                mostrarErrorYSalir();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    //Método del botón Capturar: guarda la foto en la caché y la devuelve al formulario
    private void capturarFoto(View view) {
        if (imageCapture == null) {
            return;
        }
        try {
            File carpeta = new File(requireContext().getCacheDir(), CARPETA_FOTOS);
            if (!carpeta.exists() && !carpeta.mkdirs()) {
                mostrarErrorYSalir();
                return;
            }
            File archivo = new File(carpeta, NOMBRE_FOTO);
            ImageCapture.OutputFileOptions opciones = new ImageCapture.OutputFileOptions.Builder(archivo).build();
            btnCapturar.setEnabled(false);
            imageCapture.takePicture(opciones, ContextCompat.getMainExecutor(requireContext()),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults resultado) {
                            if (!isAdded()) {
                                return;
                            }
                            Bundle datos = new Bundle();
                            datos.putString(CLAVE_RUTA_FOTO, archivo.getAbsolutePath());
                            getParentFragmentManager().setFragmentResult(RESULTADO_FOTO, datos);
                            getParentFragmentManager().popBackStack();
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException e) {
                            Log.i("camara", "error al capturar la foto " + e.getMessage());
                            if (isAdded()) {
                                btnCapturar.setEnabled(true);
                                Toast.makeText(requireContext(), R.string.camara_error_captura, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("camara", "error al capturar la foto " + e.getMessage());
            btnCapturar.setEnabled(true);
            Toast.makeText(requireContext(), R.string.camara_error_captura, Toast.LENGTH_LONG).show();
        }
    }

    //Método del botón Cancelar: vuelve al formulario sin foto nueva
    private void cancelar(View view) {
        getParentFragmentManager().popBackStack();
    }

    //Si la cámara no se puede usar se informa y se vuelve al formulario; la app no se cierra (D-7)
    private void mostrarErrorYSalir() {
        if (!isAdded()) {
            return;
        }
        Toast.makeText(requireContext(), R.string.camara_no_disponible, Toast.LENGTH_LONG).show();
        getParentFragmentManager().popBackStack();
    }
}
