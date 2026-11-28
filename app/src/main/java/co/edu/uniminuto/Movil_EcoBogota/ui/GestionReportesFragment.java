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

//Administración de reportes (HU-08); solo visible cuando el contrato defina el rol de administrador (G3)
public class GestionReportesFragment extends Fragment {

    //Vistas
    private RecyclerView rvGestionReportes;
    private ProgressBar pbGestionReportes;
    private TextView tvEstadoGestionReportes;
    private Button btnReintentarGestionReportes;
    private Spinner spEstadoReporte;
    private Button btnActualizarEstadoReporte;

    //ViewModel de la pantalla
    private GestionReportesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestion_reportes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnReintentarGestionReportes.setOnClickListener(this::reintentar);
        btnActualizarEstadoReporte.setOnClickListener(this::actualizarEstado);
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvGestionReportes = view.findViewById(R.id.rvGestionReportes);
        pbGestionReportes = view.findViewById(R.id.pbGestionReportes);
        tvEstadoGestionReportes = view.findViewById(R.id.tvEstadoGestionReportes);
        btnReintentarGestionReportes = view.findViewById(R.id.btnReintentarGestionReportes);
        spEstadoReporte = view.findViewById(R.id.spEstadoReporte);
        btnActualizarEstadoReporte = view.findViewById(R.id.btnActualizarEstadoReporte);
        //Los estados posibles llegan con el contrato G7; mientras tanto el selector queda deshabilitado
        spEstadoReporte.setEnabled(false);
        //El adaptador y el detalle se agregan cuando exista el contrato del backend
        rvGestionReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(GestionReportesViewModel.class);
    }

    //Método para observar el estado y los mensajes del ViewModel
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoGestionReportes::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
        viewModel.getMensajeActualizacion().observe(getViewLifecycleOwner(), this::mostrarMensajeActualizacion);
    }

    //Método del botón Reintentar: vuelve a consultar el listado
    private void reintentar(View view) {
        viewModel.cargar();
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        pbGestionReportes.setVisibility(estado == EstadoUi.CARGANDO ? View.VISIBLE : View.GONE);
        rvGestionReportes.setVisibility(estado == EstadoUi.EXITO ? View.VISIBLE : View.GONE);
        boolean conMensaje = estado == EstadoUi.ERROR || estado == EstadoUi.SIN_RESULTADOS;
        tvEstadoGestionReportes.setVisibility(conMensaje ? View.VISIBLE : View.GONE);
        btnReintentarGestionReportes.setVisibility(estado == EstadoUi.ERROR ? View.VISIBLE : View.GONE);
        if (estado == EstadoUi.SIN_RESULTADOS) {
            tvEstadoGestionReportes.setText(R.string.sin_resultados);
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
