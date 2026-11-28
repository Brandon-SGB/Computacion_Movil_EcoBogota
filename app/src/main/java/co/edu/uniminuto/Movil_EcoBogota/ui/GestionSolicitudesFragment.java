package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Administración de solicitudes de recolección (HU-13); solo visible cuando el contrato defina el rol de administrador (G3)
public class GestionSolicitudesFragment extends Fragment {

    //Vistas
    private RecyclerView rvGestionSolicitudes;
    private ProgressBar pbGestionSolicitudes;
    private TextView tvEstadoGestionSolicitudes;
    private Button btnReintentarGestionSolicitudes;
    private Spinner spEstadoSolicitud;
    private Button btnActualizarEstadoSolicitud;

    //ViewModel de la pantalla
    private GestionSolicitudesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestion_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnReintentarGestionSolicitudes.setOnClickListener(this::reintentar);
        btnActualizarEstadoSolicitud.setOnClickListener(this::actualizarEstado);
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvGestionSolicitudes = view.findViewById(R.id.rvGestionSolicitudes);
        pbGestionSolicitudes = view.findViewById(R.id.pbGestionSolicitudes);
        tvEstadoGestionSolicitudes = view.findViewById(R.id.tvEstadoGestionSolicitudes);
        btnReintentarGestionSolicitudes = view.findViewById(R.id.btnReintentarGestionSolicitudes);
        spEstadoSolicitud = view.findViewById(R.id.spEstadoSolicitud);
        btnActualizarEstadoSolicitud = view.findViewById(R.id.btnActualizarEstadoSolicitud);
        //Los estados posibles llegan con el contrato G7; mientras tanto el selector queda deshabilitado
        spEstadoSolicitud.setEnabled(false);
        //El adaptador y el detalle se agregan cuando exista el contrato del backend
        rvGestionSolicitudes.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(GestionSolicitudesViewModel.class);
    }

    //Método para observar el estado y los mensajes del ViewModel
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoGestionSolicitudes::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
        viewModel.getMensajeActualizacion().observe(getViewLifecycleOwner(), this::mostrarMensajeActualizacion);
    }

    //Método del botón Reintentar: vuelve a consultar el listado
    private void reintentar(View view) {
        viewModel.cargar();
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        pbGestionSolicitudes.setVisibility(estado == EstadoUi.CARGANDO ? View.VISIBLE : View.GONE);
        rvGestionSolicitudes.setVisibility(estado == EstadoUi.EXITO ? View.VISIBLE : View.GONE);
        boolean conMensaje = estado == EstadoUi.ERROR || estado == EstadoUi.SIN_RESULTADOS;
        tvEstadoGestionSolicitudes.setVisibility(conMensaje ? View.VISIBLE : View.GONE);
        btnReintentarGestionSolicitudes.setVisibility(estado == EstadoUi.ERROR ? View.VISIBLE : View.GONE);
        if (estado == EstadoUi.SIN_RESULTADOS) {
            tvEstadoGestionSolicitudes.setText(R.string.sin_resultados);
        }
    }

    //Método del botón Actualizar estado
    private void actualizarEstado(View view) {
        viewModel.actualizarEstado();
    }

    private void mostrarMensajeActualizacion(String mensaje) {
        if (mensaje != null) {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show();
        }
    }
}
