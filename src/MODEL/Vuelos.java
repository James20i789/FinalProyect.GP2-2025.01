package MODEL;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public record Vuelos(String numero, String origen, String destino, BigDecimal precio, int capacidad) {
    public Vuelos {
        numero = codigo(numero);
        origen = texto(origen);
        destino = texto(destino);
        if (origen.equalsIgnoreCase(destino)) {
            throw new IllegalArgumentException("El origen y el destino deben ser distintos.");
        }
        if (precio.signum() <= 0 || precio.scale() < 0 || precio.scale() > 2 || precio.precision() - precio.scale() > 10 || capacidad <= 0) {
            throw new IllegalArgumentException("Indique un precio positivo de hasta dos decimales y una capacidad positiva.");
        }
        precio = precio.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static String texto(String valor) {
        return texto(valor, 200);
    }

    static String texto(String valor, int maximo) {
        String limpio = valor.strip();
        if (limpio.isEmpty() || limpio.length() > maximo || limpio.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Ingrese texto válido de hasta " + maximo + " caracteres.");
        }
        return limpio;
    }

    public static String codigo(String valor) {
        String limpio = texto(valor).toUpperCase(Locale.ROOT);
        if (!limpio.matches("[A-Z0-9][A-Z0-9_-]{0,29}")) {
            throw new IllegalArgumentException("Use de 1 a 30 letras, dígitos, guiones o guiones bajos para el código.");
        }
        return limpio;
    }
}
