package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import co.edu.uniminuto.Movil_EcoBogota.R;

//Pantalla de los puntos de reciclaje (HU-09, listado)
public class PuntosFragment extends Fragment {

    //Vistas
    private RecyclerView rvPuntos;
    private ProgressBar pbPuntos;
    private TextView tvEstadoPuntos;
    private Button btnReintentarPuntos;
    private Spinner spTipoResiduo;

    //ViewModel de la pantalla
    private PuntosViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_puntos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        observarViewModel();
        btnReintentarPuntos.setOnClickListener(this::reintentar);
        if (viewModel.getEstado().getValue() == null) {
            viewModel.cargar();
        }
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects(View view) {
        rvPuntos = view.findViewById(R.id.rvPuntos);
        pbPuntos = view.findViewById(R.id.pbPuntos);
        tvEstadoPuntos = view.findViewById(R.id.tvEstadoPuntos);
        btnReintentarPuntos = view.findViewById(R.id.btnReintentarPuntos);
        spTipoResiduo = view.findViewById(R.id.spTipoResiduo);
        //Los tipos de residuo del filtro llegan del backend; mientras tanto el filtro queda deshabilitado
        spTipoResiduo.setEnabled(false);
        //El adaptador se agrega cuando exista el contrato del backend
        rvPuntos.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel = new ViewModelProvider(this).get(PuntosViewModel.class);
    }

    //Método para observar el estado y el mensaje del ViewModel
    private void observarViewModel() {
        viewModel.getMensaje().observe(getViewLifecycleOwner(), tvEstadoPuntos::setText);
        viewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);
    }

    //Método del botón Reintentar: vuelve a consultar el listado
    private void reintentar(View view) {
        viewModel.cargar();
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        pbPuntos.setVisibility(estado == EstadoUi.CARGANDO ? View.VISIBLE : View.GONE);
        rvPuntos.setVisibility(estado == EstadoUi.EXITO ? View.VISIBLE : View.GONE);
        boolean conMensaje = estado == EstadoUi.ERROR || estado == EstadoUi.SIN_RESULTADOS;
        tvEstadoPuntos.setVisibility(conMensaje ? View.VISIBLE : View.GONE);
        btnReintentarPuntos.setVisibility(estado == EstadoUi.ERROR ? View.VISIBLE : View.GONE);
        if (estado == EstadoUi.SIN_RESULTADOS) {
            tvEstadoPuntos.setText(R.string.sin_resultados);
        }
    }
}
