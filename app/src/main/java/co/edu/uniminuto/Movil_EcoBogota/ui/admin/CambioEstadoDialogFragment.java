package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-08/13. Diálogo para cambiar el estado de un reporte o una solicitud: nuevo estado + comentario.
 * La lista de estados es provisional hasta que el equipo defina los oficiales (P8).
 * Devuelve el resultado al Fragment padre con la Fragment Result API.
 */
public class CambioEstadoDialogFragment extends DialogFragment {

    // 1. Atributos
    public static final String RESULTADO = "resultado_cambio_estado";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ESTADO = "estado";
    public static final String EXTRA_COMENTARIO = "comentario";
    public static final int TIPO_REPORTE = 0;
    public static final int TIPO_SOLICITUD = 1;
    private static final String ARG_TIPO = "tipo";

    private TextInputLayout tilEstado;
    private MaterialAutoCompleteTextView actvEstado;
    private TextInputEditText etComentario;

    public static CambioEstadoDialogFragment newInstance(int tipo, long id) {
        CambioEstadoDialogFragment dialogo = new CambioEstadoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TIPO, tipo);
        args.putLong(EXTRA_ID, id);
        dialogo.setArguments(args);
        return dialogo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View vista = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cambio_estado, null);
        initObjects(vista);
        AlertDialog dialogo = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_cambiar_estado)
                .setView(vista)
                .setPositiveButton(R.string.btn_guardar, null)
                .setNegativeButton(R.string.btn_cancelar, null)
                .create();
        // Se reemplaza el clic del botón positivo para que el diálogo no se cierre si hay errores.
        dialogo.setOnShowListener(d -> dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> guardar()));
        return dialogo;
    }

    // 2. Inicialización
    private void initObjects(View vista) {
        tilEstado = vista.findViewById(R.id.tilEstado);
        actvEstado = vista.findViewById(R.id.actvEstado);
        etComentario = vista.findViewById(R.id.etComentario);
        int arreglo = requireArguments().getInt(ARG_TIPO) == TIPO_REPORTE
                ? R.array.estados_reporte
                : R.array.estados_solicitud;
        actvEstado.setSimpleItems(getResources().getStringArray(arreglo));
    }

    // 3. Métodos auxiliares
    private boolean validarFormulario() {
        boolean valido = !ValidadorFormulario.estaVacio(actvEstado.getText());
        tilEstado.setError(valido ? null : getString(R.string.error_campo_obligatorio));
        return valido;
    }

    // 4. Eventos
    private void guardar() {
        if (!validarFormulario()) {
            return;
        }
        Bundle resultado = new Bundle();
        resultado.putLong(EXTRA_ID, requireArguments().getLong(EXTRA_ID));
        resultado.putString(EXTRA_ESTADO, actvEstado.getText().toString());
        resultado.putString(EXTRA_COMENTARIO, String.valueOf(etComentario.getText()).trim());
        getParentFragmentManager().setFragmentResult(RESULTADO, resultado);
        dismiss();
    }
}
