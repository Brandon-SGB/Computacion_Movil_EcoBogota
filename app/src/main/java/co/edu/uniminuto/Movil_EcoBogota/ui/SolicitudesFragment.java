package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Pantalla de las solicitudes de recolección del usuario (HU-12)
public class SolicitudesFragment extends Fragment {

    //Vistas
    private RecyclerView rvSolicitudes;
    private ProgressBar pbSolicitudes;
    private TextView tvEstadoSolicitudes;
    private Button btnNuevaSolicitud;

    //ViewModel de la pantalla
    private SolicitudesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnNuevaSolicitud.setOnClickListener(this::abrirNuevaSolicitud);
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método del botón Nueva solicitud: abre el formulario y permite volver con Atrás
    private void abrirNuevaSolicitud(View view) {
        try {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frContenedor, new NuevaSolicitudFragment())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.i("navegacion", "error al abrir la nueva solicitud " + e.getMessage());
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes);
        pbSolicitudes = view.findViewById(R.id.pbSolicitudes);
        tvEstadoSolicitudes = view.findViewById(R.id.tvEstadoSolicitudes);
        btnNuevaSolicitud = view.findViewById(R.id.btnNuevaSolicitud);
        //El adaptador se agrega cuando exista el contrato del backend
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(SolicitudesViewModel.class);
    }

    //Método para observar el estado y el mensaje del ViewModel
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoSolicitudes::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        pbSolicitudes.setVisibility(estado == EstadoUi.CARGANDO ? View.VISIBLE : View.GONE);
        rvSolicitudes.setVisibility(estado == EstadoUi.EXITO ? View.VISIBLE : View.GONE);
        boolean conMensaje = estado == EstadoUi.ERROR || estado == EstadoUi.SIN_RESULTADOS;
        tvEstadoSolicitudes.setVisibility(conMensaje ? View.VISIBLE : View.GONE);
        if (estado == EstadoUi.SIN_RESULTADOS) {
            tvEstadoSolicitudes.setText(R.string.sin_resultados);
        }
    }
}
