package VistaControl;

import MODEL.Clientes;
import MODEL.Vuelos;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class FlujoAgenciaTest {
    public static void main(String[] args) throws Exception {
        Path temporal = Files.createTempDirectory("agencia-test-");
        try {
            verificar(temporal);
            System.out.println("Flujo de agencia verificado.");
        } finally {
            try (var rutas = Files.walk(temporal)) {
                for (Path ruta : rutas.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(ruta);
                }
            }
        }
    }

    private static void verificar(Path temporal) throws Exception {
        Path historico = temporal.resolve("venta.txt");
        String boletaAnterior = "N° de Venta     : ANTERIOR\nBoleta de ejemplo\n";
        Files.writeString(historico, boletaAnterior);
        Path datos = temporal.resolve("datos");
        Path persistencia = datos.resolve("agencia.tsv");

        try (var archivo = new ArchivoAgencia(datos)) {
            rechaza(IOException.class, () -> {
                try (var segunda = new ArchivoAgencia(datos)) {
                    segunda.cargar();
                }
            });
            var op = new Operaciones(archivo, historico);
            var cliente = new Clientes("01234567", "José", "Pérez", "Lima");
            var vuelo = new Vuelos("pe-101", "Lima", "Cusco", new BigDecimal("19.95"), 3);
            op.guardarCliente(cliente, false);
            op.guardarVuelo(vuelo, false);
            rechaza(IllegalArgumentException.class, () -> op.guardarCliente(cliente, false));
            rechaza(IllegalArgumentException.class, () -> op.guardarVuelo(vuelo, false));
            rechaza(IllegalArgumentException.class, () -> op.vender("ANTERIOR", cliente.dni(), vuelo.numero(), 1));
            rechaza(IllegalArgumentException.class, () -> op.vender("S1", "98765432", vuelo.numero(), 1));
            rechaza(IllegalArgumentException.class, () -> op.vender("S1", cliente.dni(), vuelo.numero(), 4));
            var venta = op.vender("s1", cliente.dni(), "pe-101", 2);
            exige(venta.total().equals(new BigDecimal("39.90")), "Total exacto");
            exige(op.disponibles(vuelo) == 1, "Descontar dos asientos");
            rechaza(IllegalArgumentException.class, () -> op.vender("S1", cliente.dni(), vuelo.numero(), 1));
            rechaza(IllegalArgumentException.class, () -> op.eliminarCliente(cliente.dni()));
            rechaza(IllegalArgumentException.class, () -> op.eliminarVuelo(vuelo.numero()));
            rechaza(IllegalArgumentException.class, () -> op.guardarVuelo(
                    new Vuelos(vuelo.numero(), "Lima", "Cusco", BigDecimal.TEN, 1), true));

            Path respaldo = Files.move(persistencia, datos.resolve("respaldo.tsv"));
            Files.createDirectory(persistencia);
            try {
                rechaza(IOException.class, () -> op.vender("S2", cliente.dni(), vuelo.numero(), 1));
                exige(op.disponibles(vuelo) == 1, "Fallo al guardar conserva la ocupación");
                exige(!op.historial().contains("S2"), "Fallo al guardar no publica la venta");
            } finally {
                Files.delete(persistencia);
                Files.move(respaldo, persistencia);
            }
            op.guardarVuelo(new Vuelos(vuelo.numero(), "Lima", "Arequipa", new BigDecimal("30.00"), 3), true);
        }
        try (var archivo = new ArchivoAgencia(datos)) {
            var op = new Operaciones(archivo, historico);
            exige(op.cliente("01234567").nombreCompleto().equals("José Pérez"), "Conservar DNI y tildes");
            exige(op.disponibles(op.vuelo("PE-101")) == 1, "Conservar ocupación tras reinicio");
            exige(op.historial().contains("Cusco") && op.historial().contains("$39.90"), "Conservar la boleta original");
            op.anular("s1");
            rechaza(IllegalArgumentException.class, () -> op.anular("s1"));
            exige(op.disponibles(op.vuelo("PE-101")) == 3, "Recuperar asientos");
            op.eliminarVuelo("PE-101");
            op.eliminarCliente("01234567");
            String exportacion = Files.readString(op.exportar());
            exige(exportacion.contains("ANULADA") && exportacion.contains(boletaAnterior), "Exportar ambas fuentes");
        }
        try (var archivo = new ArchivoAgencia(datos)) {
            var op = new Operaciones(archivo, historico);
            exige(op.historial().contains("ANULADA"), "Conservar anulación tras reinicio");
            exige(Files.readString(historico).equals(boletaAnterior), "Preservar archivo histórico");
        }
        Files.writeString(persistencia, "AGENCIA\t1\nV\tFORMATO-TRUNCADO\n");
        try (var archivo = new ArchivoAgencia(datos)) {
            rechaza(IOException.class, archivo::cargar);
            exige(Files.readString(persistencia).contains("TRUNCADO"), "No sobrescribir datos inválidos");
        }
    }

    private static void exige(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }

    private static void rechaza(Class<? extends Exception> tipo, Accion accion) throws Exception {
        try {
            accion.ejecutar();
        } catch (Exception e) {
            if (tipo.isInstance(e)) {
                return;
            }
            throw e;
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }
}
