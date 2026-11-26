package co.edu.uniminuto.Movil_EcoBogota.ui.ciudadano;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import co.edu.uniminuto.Movil_EcoBogota.R;
import co.edu.uniminuto.Movil_EcoBogota.data.session.SessionManager;
import co.edu.uniminuto.Movil_EcoBogota.ui.auth.LoginActivity;
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.Navegador;

/**
 * Contenedor del ciudadano: BottomNavigationView + FragmentContainerView (patrón de la guía:
 * obtenerFragment, cargarFragment, configurarBottomNavigation). Cinco destinos (sección 3.2).
 */
public class MainActivity extends AppCompatActivity implements Navegador {

    // 1. Atributos
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (!sessionManager.haySesion() || sessionManager.esAdministrador()) {
            LoginActivity.abrirLimpiandoPila(this);
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        configurarBottomNavigation();
        getSupportFragmentManager().addOnBackStackChangedListener(this::actualizarBotonAtras);
        // Al rotar, el FragmentManager restaura el Fragment activo (sección 3.4).
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_inicio);
        }
        actualizarBotonAtras();
    }

    // 2. Inicialización
    private void initObjects() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        setSupportActionBar(toolbar);
    }

    // 3. Navegación
    private void configurarBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            cargarFragment(obtenerFragment(item.getItemId()));
            return true;
        });
        // Volver a tocar la pestaña activa regresa a su pantalla raíz.
        bottomNavigation.setOnItemReselectedListener(item -> limpiarPila());
    }

    private Fragment obtenerFragment(int itemId) {
        if (itemId == R.id.nav_puntos) {
            return new PuntosReciclajeFragment();
        } else if (itemId == R.id.nav_ecoia) {
            return new EcoIaFragment();
        } else if (itemId == R.id.nav_tramites) {
            return new MisTramitesFragment();
        } else if (itemId == R.id.nav_perfil) {
            return new PerfilFragment();
        }
        return new InicioFragment();
    }

    /** Pestañas: replace sin pila de retroceso (como la guía). */
    private void cargarFragment(Fragment fragment) {
        limpiarPila();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcvContenedor, fragment)
                .commit();
    }

    private void limpiarPila() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void actualizarBotonAtras() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    /** Detalles y formularios: replace + addToBackStack; Atrás vuelve a la lista. */
    @Override
    public void abrirPantalla(Fragment destino) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcvContenedor, destino)
                .addToBackStack(null)
                .commit();
    }

    /** Usado por InicioFragment para ir a la pestaña EcoIA. */
    public void seleccionarPestana(int itemId) {
        bottomNavigation.setSelectedItemId(itemId);
    }

    // 4. Callbacks del sistema
    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
