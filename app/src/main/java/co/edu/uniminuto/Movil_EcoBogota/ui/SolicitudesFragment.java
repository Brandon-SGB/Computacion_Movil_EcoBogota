package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes);
        pbSolicitudes = view.findViewById(R.id.pbSolicitudes);
        tvEstadoSolicitudes = view.findViewById(R.id.tvEstadoSolicitudes);
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
