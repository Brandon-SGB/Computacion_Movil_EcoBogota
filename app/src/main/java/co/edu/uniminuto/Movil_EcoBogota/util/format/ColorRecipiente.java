package co.edu.uniminuto.Movil_EcoBogota.util.format;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import co.edu.uniminuto.Movil_EcoBogota.R;

/**
 * Código de colores de recipientes (Resolución 2184 de 2019): blanco (aprovechables),
 * negro (no aprovechables) y verde (orgánicos aprovechables).
 * Pendiente P10: confirmar si categorias_residuos.tipo guarda este valor.
 */
public enum ColorRecipiente {

    BLANCO(R.color.recipiente_blanco, R.string.recipiente_blanco),
    NEGRO(R.color.recipiente_negro, R.string.recipiente_negro),
    VERDE(R.color.recipiente_verde, R.string.recipiente_verde);

    // 1. Atributos
    @ColorRes
    private final int color;
    @StringRes
    private final int nombre;

    ColorRecipiente(@ColorRes int color, @StringRes int nombre) {
        this.color = color;
        this.nombre = nombre;
    }

    // 2. Conversión desde el texto de la API
    public static ColorRecipiente desdeTexto(String valor) {
        if (valor == null) {
            return NEGRO;
        }
        try {
            return valueOf(valor.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NEGRO;
        }
    }

    @ColorRes
    public int getColor() {
        return color;
    }

    @StringRes
    public int getNombre() {
        return nombre;
    }
}
