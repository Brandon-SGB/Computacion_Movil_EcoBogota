package co.edu.uniminuto.Movil_EcoBogota.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.data.remote.ApiConfig;
import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;
import co.edu.uniminuto.Movil_EcoBogota.ui.admin.AdminActivity;
import co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano.MainActivity;
import co.edu.uniminuto.Movil_EcoBogota.util.validation.ValidadorFormulario;

/**
 * HU-02. Launcher: si hay sesión abre MainActivity o AdminActivity según el rol; si no, muestra el
 * formulario de inicio de sesión.
 */
public class LoginActivity extends AppCompatActivity {

    // 1. Atributos
    private static final String PREFIJO_CORREO_ADMIN_PRUEBA = "admin";
    private static final String TOKEN_PRUEBA = "token-de-prueba";

    private TextInputLayout tilCorreo;
    private TextInputLayout tilContrasena;
    private TextInputEditText etCorreo;
    private TextInputEditText etContrasena;
    private Button btnIniciarSesion;
    private TextView tvIrRegistro;
    private TextView tvModoPrueba;
    private ProgressBar pbCargando;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<Intent> registroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onResultadoRegistro);

    /** Cierra la sesión visible y vuelve al login limpiando la pila (sección 3.4). */
    public static void abrirLimpiandoPila(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (sessionManager.haySesion()) {
            abrirSegunRol();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        btnIniciarSesion.setOnClickListener(this::iniciarSesion);
        tvIrRegistro.setOnClickListener(this::abrirRegistro);
    }

    // 2. Inicialización
    private void initObjects() {
        tilCorreo = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);
        tvModoPrueba = findViewById(R.id.tvModoPrueba);
        pbCargando = findViewById(R.id.pbCargando);
        tvModoPrueba.setVisibility(ApiConfig.MODO_SIN_BACKEND ? View.VISIBLE : View.GONE);
        mostrarCargando(false);
    }

    // 3. Métodos auxiliares
    private boolean validarFormulario() {
        boolean valido = true;
        tilCorreo.setError(null);
        tilContrasena.setError(null);
        if (!ValidadorFormulario.esCorreoValido(etCorreo.getText())) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
            valido = false;
        }
        if (ValidadorFormulario.estaVacio(etContrasena.getText())) {
            tilContrasena.setError(getString(R.string.error_campo_obligatorio));
            valido = false;
        }
        return valido;
    }

    private void mostrarCargando(boolean cargando) {
        pbCargando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnIniciarSesion.setEnabled(!cargando);
    }

    private void mostrarError(String mensaje) {
        mostrarCargando(false);
        Snackbar.make(btnIniciarSesion, mensaje, Snackbar.LENGTH_LONG).show();
    }

    private void iniciarSesionDePrueba(String correo) {
        String rol = correo.toLowerCase().startsWith(PREFIJO_CORREO_ADMIN_PRUEBA)
                ? SessionManager.ROL_ADMINISTRADOR
                : SessionManager.ROL_CIUDADANO;
        String nombre = correo.substring(0, correo.indexOf('@'));
        sessionManager.guardarSesion(TOKEN_PRUEBA, 0L, nombre, correo, rol);
        abrirSegunRol();
    }

    private void abrirSegunRol() {
        Class<? extends Activity> destino = sessionManager.esAdministrador()
                ? AdminActivity.class
                : MainActivity.class;
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // 4. Eventos
    private void iniciarSesion(View view) {
        if (!validarFormulario()) {
            return;
        }
        mostrarCargando(true);
        String correo = String.valueOf(etCorreo.getText()).trim();
        if (ApiConfig.MODO_SIN_BACKEND) {
            iniciarSesionDePrueba(correo);
            return;
        }
        // TODO HU-02: AuthRepository.login(correo, contrasena, RepositoryCallback<AuthResponse>)
        // y sessionManager.guardarSesion(...) con el token y el rol que devuelva /auth/login.
        mostrarError(getString(R.string.msg_pendiente_api, getString(R.string.hu_02)));
    }

    private void abrirRegistro(View view) {
        registroLauncher.launch(new Intent(this, RegisterActivity.class));
    }

    // 5. Callbacks del sistema
    private void onResultadoRegistro(ActivityResult resultado) {
        if (resultado.getResultCode() != RESULT_OK || resultado.getData() == null) {
            return;
        }
        etCorreo.setText(resultado.getData().getStringExtra(RegisterActivity.EXTRA_CORREO));
        etContrasena.setText(null);
        Snackbar.make(btnIniciarSesion, R.string.msg_registro_exitoso, Snackbar.LENGTH_LONG).show();
    }
}
