package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-03. Ver y editar los datos autorizados de un usuario; activar o desactivar (baja lógica).
 * El rol solo lo cambia el administrador.
 */
public class AdminUsuarioFormFragment extends Fragment {

    // 1. Atributos
    private static final String ARG_ID_USUARIO = "id_usuario";

    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilTelefono;
    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etCorreo;
    private TextInputEditText etTelefono;
    private TextInputEditText etDireccion;
    private MaterialAutoCompleteTextView actvRol;
    private MaterialSwitch swActivo;
    private Button btnGuardar;
    private long idUsuario;

    public AdminUsuarioFormFragment() {
        super(R.layout.fragment_admin_usuario_form);
    }

    public static AdminUsuarioFormFragment newInstance(long idUsuario) {
        AdminUsuarioFormFragment fragment = new AdminUsuarioFormFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_USUARIO, idUsuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObjects(view);
        btnGuardar.setOnClickListener(this::guardar);
        cargarDatos();
    }

    // 2. Inicialización
    private void initObjects(View view) {
        tilNombres = view.findViewById(R.id.tilNombres);
        tilApellidos = view.findViewById(R.id.tilApellidos);
        tilCorreo = view.findViewById(R.id.tilCorreo);
        tilTelefono = view.findViewById(R.id.tilTelefono);
        etNombres = view.findViewById(R.id.etNombres);
        etApellidos = view.findViewById(R.id.etApellidos);
        etCorreo = view.findViewById(R.id.etCorreo);
        etTelefono = view.findViewById(R.id.etTelefono);
        etDireccion = view.findViewById(R.id.etDireccion);
        actvRol = view.findViewById(R.id.actvRol);
        swActivo = view.findViewById(R.id.swActivo);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        actvRol.setSimpleItems(getResources().getStringArray(R.array.roles_usuario));
        idUsuario = requireArguments().getLong(ARG_ID_USUARIO);
    }

    // 3. Métodos auxiliares
    private void cargarDatos() {
        // TODO HU-03: UsuarioRepository.obtener(idUsuario, ...) y llenar el formulario.
    }

    private boolean validarFormulario() {
        boolean valido = true;
        tilNombres.setError(null);
        tilApellidos.setError(null);
        tilCorreo.setError(null);
        tilTelefono.setError(null);
        if (ValidadorFormulario.estaVacio(etNombres.getText())) {
            tilNombres.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etApellidos.getText())) {
            tilApellidos.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        if (!ValidadorFormulario.esCorreoValido(etCorreo.getText())) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
            valido = false;
        }
        if (!ValidadorFormulario.estaVacio(etTelefono.getText())
                && !ValidadorFormulario.esTelefonoValido(etTelefono.getText())) {
            tilTelefono.setError(getString(R.string.error_telefono_invalido));
            valido = false;
        }
        return valido;
    }

    // 4. Eventos
    private void guardar(View view) {
        if (!validarFormulario()) {
            return;
        }
        // TODO HU-03: UsuarioRepository.actualizar(idUsuario, datos, rol, swActivo.isChecked(), ...)
        // y en onSuccess volver a la lista con getParentFragmentManager().popBackStack().
        Snackbar.make(view, getString(R.string.msg_pendiente_api, getString(R.string.hu_03)),
                Snackbar.LENGTH_LONG).show();
    }
}
