package MODEL;

import java.math.BigDecimal;
import java.time.Instant;

public record Ventas(String numero, String dni, String cliente, String vuelo, String origen,
        String destino, BigDecimal precio, int cantidad, Instant fecha, Instant anulacion) {
    public Ventas {
        numero = Vuelos.codigo(numero);
        vuelo = Vuelos.codigo(vuelo);
        if (!dni.matches("[0-9]{8}") || cantidad <= 0 || fecha == null
                || (anulacion != null && anulacion.isBefore(fecha))) {
            throw new IllegalArgumentException("Datos de venta inválidos.");
        }
        cliente = Vuelos.texto(cliente, 401);
        Vuelos datos = new Vuelos(vuelo, origen, destino, precio, cantidad);
        origen = datos.origen();
        destino = datos.destino();
        precio = datos.precio();
    }

    public boolean activa() {
        return anulacion == null;
    }

    public Ventas anular(Instant momento) {
        return new Ventas(numero, dni, cliente, vuelo, origen, destino, precio, cantidad, fecha, momento);
    }

    public BigDecimal total() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    public String boleta() {
        return """
                ======================= BOLETA DE VENTA =======================
                N° de Venta     : %s
                Fecha (UTC)     : %s
                Cliente         : %s | DNI %s
                Vuelo           : %s | %s → %s
                Cantidad        : %d
                Precio unitario : $%s
                TOTAL           : $%s
                Estado          : %s
                ===============================================================
                """.formatted(numero, fecha, cliente, dni, vuelo, origen, destino, cantidad,
                precio.toPlainString(), total().toPlainString(),
                activa() ? "ACTIVA" : "ANULADA (" + anulacion + ")");
    }
}
