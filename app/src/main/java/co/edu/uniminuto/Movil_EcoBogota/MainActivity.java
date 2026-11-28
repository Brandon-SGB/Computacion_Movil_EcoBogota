package co.edu.uniminuto.Movil_EcoBogota;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;
import co.edu.uniminuto.Movil_EcoBogota.ui.GestionReportesFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.GestionSolicitudesFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.LoginActivity;
import co.edu.uniminuto.Movil_EcoBogota.ui.PuntosFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.ReportesFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.SolicitudesFragment;

public class MainActivity extends AppCompatActivity {

    //Contexto de la aplicación
    private Context context;

    //Navegación inferior
    private BottomNavigationView bnvMenu;

    //Sesión del usuario
    private SessionManager sessionManager;

    //Conexión a internet
    private ConnectivityManager conexion;
    private boolean hayConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initObjects();
        configurarBottomNavigation();
        configurarOpcionesAdministrador();
        observarSesion();
        //Pantalla inicial: solo la primera vez, no al rotar
        if (savedInstanceState == null) {
            bnvMenu.setSelectedItemId(R.id.nav_reportes);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConexion();
    }

    //Método para inicializar los objetos de la actividad
    private void initObjects() {
        context = getApplicationContext();
        bnvMenu = findViewById(R.id.bnvMenu);
        setSupportActionBar(findViewById(R.id.tbPrincipal));
        sessionManager = SessionManager.getInstance(context);
    }

    //Menú de la barra superior con la opción Cerrar sesión
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_superior, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.opcion_cerrar_sesion) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Método para cerrar la sesión (A6); si había token, el aviso de sesión abre el login
    private void cerrarSesion() {
        try {
            if (!sessionManager.invalidarSesion()) {
                abrirLogin();
            }
        } catch (Exception e) {
            Log.i("sesion", "error al cerrar la sesión " + e.getMessage());
        }
    }

    //Método para observar el aviso de sesión invalidada (P-04, A11)
    private void observarSesion() {
        sessionManager.getSesionInvalidada().observe(this, invalidada -> {
            if (Boolean.TRUE.equals(invalidada)) {
                sessionManager.limpiarAvisoSesion();
                abrirLogin();
            }
        });
    }

    //Método para abrir el inicio de sesión con la pila de pantallas limpia (A6)
    private void abrirLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Método para configurar la navegación inferior
    private void configurarBottomNavigation() {
        bnvMenu.setOnItemSelectedListener(item -> {
            Fragment fragment = obtenerFragment(item.getItemId());
            if (fragment == null) {
                return false;
            }
            cargarFragment(fragment);
            return true;
        });
    }

    //Método que devuelve el fragment de cada opción del menú
    private Fragment obtenerFragment(int itemId) {
        if (itemId == R.id.nav_reportes) {
            return new ReportesFragment();
        } else if (itemId == R.id.nav_solicitudes) {
            return new SolicitudesFragment();
        } else if (itemId == R.id.nav_puntos) {
            return new PuntosFragment();
        } else if (itemId == R.id.nav_gestion_reportes && esAdministrador()) {
            return new GestionReportesFragment();
        } else if (itemId == R.id.nav_gestion_solicitudes && esAdministrador()) {
            return new GestionSolicitudesFragment();
        }
        return null;
    }

    //El rol de administrador llegará con el contrato del backend (G3); no se guarda ni se simula localmente
    private boolean esAdministrador() {
        return false;
    }

    //Método para mostrar u ocultar las opciones de administrador según el rol
    private void configurarOpcionesAdministrador() {
        boolean administrador = esAdministrador();
        bnvMenu.getMenu().findItem(R.id.nav_gestion_reportes).setVisible(administrador);
        bnvMenu.getMenu().findItem(R.id.nav_gestion_solicitudes).setVisible(administrador);
    }

    //Método para mostrar un fragment en el contenedor
    private void cargarFragment(Fragment fragment) {
        try {
            //Al cambiar de pestaña se descartan los formularios abiertos
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frContenedor, fragment)
                    .commit();
        } catch (Exception e) {
            Log.i("navegacion", "error al cargar la pantalla " + e.getMessage());
        }
    }

    //Método para verificar si el dispositivo tiene conexión a internet
    private void checkConexion() {
        try {
            conexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean conectado = false;
            if (conexion != null) {
                Network red = conexion.getActiveNetwork();
                NetworkCapabilities caps = conexion.getNetworkCapabilities(red);
                conectado = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
            hayConexion = conectado;
            //Mensaje desde strings.xml
            Toast.makeText(context, conectado ? R.string.conexion_disponible : R.string.conexion_no_disponible, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i("conexion", "error con la conexión " + e.getMessage());
        }
    }
}

//Prueba de Git//