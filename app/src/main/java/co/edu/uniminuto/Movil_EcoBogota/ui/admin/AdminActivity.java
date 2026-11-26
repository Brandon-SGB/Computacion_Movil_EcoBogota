package co.edu.uniminuto.Movil_EcoBogota.ui.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import co.edu.uniminuto.Movil_EcoBogota.ui.comun.PermisosFragment;

/**
 * HU-03/05/08/10/13. Contenedor del administrador, mismo patrón que MainActivity.
 * Bottom Navigation: Usuarios, Residuos, Puntos, Reportes, Solicitudes (sección 3.3).
 * Toolbar: Permisos y Cerrar sesión.
 */
public class AdminActivity extends AppCompatActivity implements Navegador {

    // 1. Atributos
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        if (!sessionManager.esAdministrador()) {
            LoginActivity.abrirLimpiandoPila(this);
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initObjects();
        configurarBottomNavigation();
        getSupportFragmentManager().addOnBackStackChangedListener(this::actualizarBotonAtras);
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_admin_usuarios);
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
        bottomNavigation.setOnItemReselectedListener(item -> limpiarPila());
    }

    private Fragment obtenerFragment(int itemId) {
        if (itemId == R.id.nav_admin_residuos) {
            return new AdminResiduosFragment();
        } else if (itemId == R.id.nav_admin_puntos) {
            return new AdminPuntosFragment();
        } else if (itemId == R.id.nav_admin_reportes) {
            return new AdminReportesFragment();
        } else if (itemId == R.id.nav_admin_solicitudes) {
            return new AdminSolicitudesFragment();
        }
        return new AdminUsuariosFragment();
    }

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

    private void cerrarSesion() {
        sessionManager.cerrarSesion();
        LoginActivity.abrirLimpiandoPila(this);
        finish();
    }

    @Override
    public void abrirPantalla(Fragment destino) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fcvContenedor, destino)
                .addToBackStack(null)
                .commit();
    }

    // 4. Callbacks del sistema
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.accion_permisos) {
            abrirPantalla(new PermisosFragment());
            return true;
        } else if (item.getItemId() == R.id.accion_cerrar_sesion) {
            cerrarSesion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
