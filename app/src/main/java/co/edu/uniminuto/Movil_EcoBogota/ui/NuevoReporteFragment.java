package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    //ViewModel de la pantalla
    private NuevoReporteViewModel viewModel;

    //Solicitud del permiso de ubicación; en un Fragment la respuesta llega por este registro
    private final ActivityResultLauncher<String[]> solicitudPermisoUbicacion =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    this::onRespuestaPermisoUbicacion);

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
                avisarGpsPendiente();
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
            avisarGpsPendiente();
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

    //La lectura de la ubicación queda pendiente hasta habilitar play-services-location (DA-7)
    private void avisarGpsPendiente() {
        Toast.makeText(requireContext(), R.string.gps_pendiente, Toast.LENGTH_LONG).show();
        etDireccion.requestFocus();
    }
}
