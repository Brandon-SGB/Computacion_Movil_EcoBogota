package co.edu.uniminuto.Movil_EcoBogota.data.remote;

import co.edu.uniminuto.Movil_EcoBogota.BuildConfig;

/**
 * Constantes de conexión con el backend. Los valores salen de build.gradle.kts (buildConfigField).
 */
public final class ApiConfig {

    // 1. Constantes
    public static final String BASE_URL = BuildConfig.BASE_URL;
    /** Temporal: en debug permite navegar sin backend (login y registro de prueba). */
    public static final boolean MODO_SIN_BACKEND = BuildConfig.MODO_SIN_BACKEND;
    public static final long TIMEOUT_CONEXION_SEGUNDOS = 15;
    public static final long TIMEOUT_LECTURA_SEGUNDOS = 30;

    private ApiConfig() {
    }
}
