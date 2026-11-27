package co.edu.uniminuto.Movil_EcoBogota;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import co.edu.uniminuto.Movil_EcoBogota.ui.PuntosFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.ReportesFragment;
import co.edu.uniminuto.Movil_EcoBogota.ui.SolicitudesFragment;

public class MainActivity extends AppCompatActivity {

    //Contexto de la aplicación
    private Context context;

    //Navegación inferior
    private BottomNavigationView bnvMenu;

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
        }
        return null;
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