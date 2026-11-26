package co.edu.uniminuto.Movil_EcoBogota.data.repository;

/**
 * Respuesta asíncrona de los repositorios (decisión P6): éxito con el dato o error con un
 * mensaje listo para mostrar.
 */
public interface RepositoryCallback<T> {

    void onSuccess(T resultado);

    void onError(String mensaje);
}
