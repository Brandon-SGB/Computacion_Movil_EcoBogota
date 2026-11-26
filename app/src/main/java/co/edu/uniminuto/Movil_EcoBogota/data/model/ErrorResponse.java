package co.edu.uniminuto.Movil_EcoBogota.data.model;

/**
 * Cuerpo de error común que devuelve el backend (manejo global de errores, T-09).
 */
public class ErrorResponse {

    // 1. Atributos
    private int status;
    private String error;
    private String mensaje;

    // 2. Accesores
    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }
}
