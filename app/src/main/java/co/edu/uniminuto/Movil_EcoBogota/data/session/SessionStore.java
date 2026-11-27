package co.edu.uniminuto.Movil_EcoBogota.data.session;

//Fuente del token de sesión; se puede sustituir por una implementación falsa en las pruebas (R-04)
public interface SessionStore {

    //Devuelve el token guardado o null si no hay sesión
    String obtenerToken();

    //Indica si existe un token guardado (no lo decodifica ni valida)
    boolean hayToken();

    //Elimina el token; devuelve true solo si había un token que eliminar
    boolean invalidarSesion();
}
