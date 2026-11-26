package co.edu.uniminuto.Movil_EcoBogota.util.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formatos de fecha que se muestran en la app.
 */
public final class FormatoFecha {

    // 1. Constantes
    private static final Locale LOCALE_CO = new Locale("es", "CO");
    private static final String PATRON_FECHA = "dd/MM/yyyy";
    private static final String PATRON_FECHA_HORA = "dd/MM HH:mm";
    private static final String PATRON_API = "yyyy-MM-dd";

    private FormatoFecha() {
    }

    // 2. Conversión
    public static String fecha(long milisegundos) {
        return new SimpleDateFormat(PATRON_FECHA, LOCALE_CO).format(new Date(milisegundos));
    }

    public static String fechaHora(long milisegundos) {
        return new SimpleDateFormat(PATRON_FECHA_HORA, LOCALE_CO).format(new Date(milisegundos));
    }

    public static String paraApi(long milisegundos) {
        return new SimpleDateFormat(PATRON_API, Locale.US).format(new Date(milisegundos));
    }
}
