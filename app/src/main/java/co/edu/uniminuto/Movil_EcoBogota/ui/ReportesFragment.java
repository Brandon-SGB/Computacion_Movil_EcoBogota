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

//Pantalla de los reportes del usuario (HU-07)
public class ReportesFragment extends Fragment {

    //Vistas
    private RecyclerView rvReportes;
    private ProgressBar pbReportes;
    private TextView tvEstadoReportes;
    private Button btnNuevoReporte;

    //ViewModel de la pantalla
    private ReportesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reportes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnNuevoReporte.setOnClickListener(this::abrirNuevoReporte);
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método del botón Nuevo reporte: abre el formulario y permite volver con Atrás
    private void abrirNuevoReporte(View view) {
        try {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frContenedor, new NuevoReporteFragment())
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.i("navegacion", "error al abrir el nuevo reporte " + e.getMessage());
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvReportes = view.findViewById(R.id.rvReportes);
        pbReportes = view.findViewById(R.id.pbReportes);
        tvEstadoReportes = view.findViewById(R.id.tvEstadoReportes);
        btnNuevoReporte = view.findViewById(R.id.btnNuevoReporte);
        //El adaptador se agrega cuando exista el contrato del backend
        rvReportes.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(ReportesViewModel.class);
    }

    //Método para observar el estado y el mensaje del ViewModel
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoReportes::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        pbReportes.setVisibility(estado == EstadoUi.CARGANDO ? View.VISIBLE : View.GONE);
        rvReportes.setVisibility(estado == EstadoUi.EXITO ? View.VISIBLE : View.GONE);
        boolean conMensaje = estado == EstadoUi.ERROR || estado == EstadoUi.SIN_RESULTADOS;
        tvEstadoReportes.setVisibility(conMensaje ? View.VISIBLE : View.GONE);
        if (estado == EstadoUi.SIN_RESULTADOS) {
            tvEstadoReportes.setText(R.string.sin_resultados);
        }
    }
}
