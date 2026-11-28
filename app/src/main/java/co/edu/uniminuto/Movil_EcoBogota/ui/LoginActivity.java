package co.edu.uniminuto.Movil_EcoBogota.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import co.edu.uniminuto.Movil_EcoBogota.MainActivity;
import co.edu.uniminuto.Movil_EcoBogota.R;

//Pantalla de inicio de sesión (HU-02)
public class LoginActivity extends AppCompatActivity {

    //Vistas
    private EditText etCorreo;
    private EditText etContrasena;
    private Button btnIngresar;
    private Button btnVolverInicio;
    private ProgressBar pbLogin;
    private TextView tvEstadoLogin;

    //ViewModel de la pantalla
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        observarViewModel();
        btnIngresar.setOnClickListener(this::ingresar);
        btnVolverInicio.setOnClickListener(this::volverAlInicio);
    }

    //Provisional hasta la autenticación real con el backend: sin servicio no hay forma de ingresar,
    //así que se permite volver a la pantalla principal con la pila limpia
    private void volverAlInicio(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Método para enlazar los objetos de la vista con la parte lógica
    private void initObjects() {
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        pbLogin = findViewById(R.id.pbLogin);
        tvEstadoLogin = findViewById(R.id.tvEstadoLogin);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    //Método para observar el estado, el mensaje y los errores de los campos
    private void observarViewModel() {
        viewModel.getMensaje().observe(this, tvEstadoLogin::setText);
        viewModel.getEstado().observe(this, this::mostrarEstado);
        viewModel.getErrorCorreo().observe(this, etCorreo::setError);
        viewModel.getErrorContrasena().observe(this, etContrasena::setError);
    }

    //Método para mostrar las vistas según el estado
    private void mostrarEstado(EstadoUi estado) {
        boolean cargando = estado == EstadoUi.CARGANDO;
        pbLogin.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!cargando);
        tvEstadoLogin.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }

    //Método del botón Ingresar; la contraseña solo se pasa a la validación, no se guarda
    private void ingresar(View view) {
        viewModel.ingresar(etCorreo.getText().toString(), etContrasena.getText().toString());
    }
}
