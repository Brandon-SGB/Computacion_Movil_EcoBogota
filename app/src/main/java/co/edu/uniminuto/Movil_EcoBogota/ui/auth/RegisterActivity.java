package co.edu.uniminuto.Movil_EcoBogota.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.data.remote.ApiConfig;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-01. Registro del ciudadano. Al terminar vuelve a LoginActivity con el correo registrado.
 */
public class RegisterActivity extends AppCompatActivity {

    // 1. Atributos
    public static final String EXTRA_CORREO = "extra_correo";

    private MaterialToolbar toolbar;
    private TextInputLayout tilNombres;
    private TextInputLayout tilApellidos;
    private TextInputLayout tilCorreo;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilDireccion;
    private TextInputLayout tilContrasena;
    private TextInputLayout tilConfirmacion;
    private TextInputEditText etNombres;
    private TextInputEditText etApellidos;
    private TextInputEditText etCorreo;
    private TextInputEditText etTelefono;
    private TextInputEditText etDireccion;
    private TextInputEditText etContrasena;
    private TextInputEditText etConfirmacion;
    private Button btnRegistrarse;
    private ProgressBar pbCargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        toolbar.setNavigationOnClickListener(v -> finish());
        btnRegistrarse.setOnClickListener(this::registrarse);
    }

    // 2. Inicialización
    private void initObjects() {
        toolbar = findViewById(R.id.toolbar);
        tilNombres = findViewById(R.id.tilNombres);
        tilApellidos = findViewById(R.id.tilApellidos);
        tilCorreo = findViewById(R.id.tilCorreo);
        tilTelefono = findViewById(R.id.tilTelefono);
        tilDireccion = findViewById(R.id.tilDireccion);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilConfirmacion = findViewById(R.id.tilConfirmacion);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmacion = findViewById(R.id.etConfirmacion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        pbCargando = findViewById(R.id.pbCargando);
        mostrarCargando(false);
    }

    // 3. Métodos auxiliares
    private boolean validarFormulario() {
        boolean valido = validarObligatorio(tilNombres, etNombres);
        valido &= validarObligatorio(tilApellidos, etApellidos);
        valido &= validarObligatorio(tilDireccion, etDireccion);

        tilCorreo.setError(ValidadorFormulario.esCorreoValido(etCorreo.getText())
                ? null : getString(R.string.error_correo_invalido));
        valido &= tilCorreo.getError() == null;

        tilTelefono.setError(ValidadorFormulario.esTelefonoValido(etTelefono.getText())
                ? null : getString(R.string.error_telefono_invalido));
        valido &= tilTelefono.getError() == null;

        tilContrasena.setError(ValidadorFormulario.esContrasenaValida(etContrasena.getText())
                ? null : getString(R.string.error_contrasena_corta, ValidadorFormulario.LONGITUD_MINIMA_CONTRASENA));
        valido &= tilContrasena.getError() == null;

        boolean coinciden = String.valueOf(etContrasena.getText()).equals(String.valueOf(etConfirmacion.getText()));
        tilConfirmacion.setError(coinciden ? null : getString(R.string.error_contrasenas_no_coinciden));
        valido &= coinciden;
        return valido;
    }

    private boolean validarObligatorio(TextInputLayout til, TextInputEditText et) {
        boolean vacio = ValidadorFormulario.estaVacio(et.getText());
        til.setError(vacio ? getString(R.string.error_campo_obligatorio) : null);
        return !vacio;
    }

    private void mostrarCargando(boolean cargando) {
        pbCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnRegistrarse.setEnabled(!cargando);
    }

    private void mostrarError(String mensaje) {
        mostrarCargando(false);
        Snackbar.make(btnRegistrarse, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private void volverAlLogin(String correo) {
        Intent datos = new Intent();
        datos.putExtra(EXTRA_CORREO, correo);
        setResult(RESULT_OK, datos);
        finish();
    }

    // 4. Eventos
    private void registrarse(View view) {
        if (!validarFormulario()) {
            return;
        }
        mostrarCargando(true);
        String correo = String.valueOf(etCorreo.getText()).trim();
        if (ApiConfig.MODO_SIN_BACKEND) {
            volverAlLogin(correo);
            return;
        }
        // TODO HU-01: AuthRepository.registrar(RegisterRequest, RepositoryCallback<UsuarioResponse>)
        // y en onSuccess volverAlLogin(correo).
        mostrarError(getString(R.string.msg_pendiente_api, getString(R.string.hu_01)));
    }
}
