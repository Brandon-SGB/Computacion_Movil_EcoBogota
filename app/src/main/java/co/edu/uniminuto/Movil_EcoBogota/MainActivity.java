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

public class MainActivity extends AppCompatActivity {

    //Contexto de la aplicación
    private Context context;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConexion();
    }

    //Método para inicializar los objetos de la actividad
    private void initObjects() {
        context = getApplicationContext();
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