package VistaControl;

import MODEL.Clientes;
import MODEL.Ventas;
import MODEL.Vuelos;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

final class ArchivoAgencia implements AutoCloseable {
    record Estado(Map<String, Vuelos> vuelos, Map<String, Clientes> clientes, Map<String, Ventas> ventas) {
        Estado() {
            this(new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
        }

        Estado copia() {
            return new Estado(new LinkedHashMap<>(vuelos), new LinkedHashMap<>(clientes), new LinkedHashMap<>(ventas));
        }

        long ocupados(String vuelo) {
            return ventas.values().stream().filter(v -> v.activa() && v.vuelo().equals(vuelo))
                    .mapToLong(Ventas::cantidad).sum();
        }
    }

    private final Path carpeta;
    private final FileChannel canal;
    private final FileLock bloqueo;

    ArchivoAgencia(Path carpeta) throws IOException {
        this.carpeta = carpeta.toAbsolutePath();
        Files.createDirectories(this.carpeta);
        canal = FileChannel.open(this.carpeta.resolve("agencia.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            bloqueo = canal.tryLock();
            if (bloqueo == null) {
                throw new IOException("Ya hay una sesión usando esta carpeta de datos.");
            }
        } catch (IOException | RuntimeException e) {
            try {
                canal.close();
            } catch (IOException cierre) {
                e.addSuppressed(cierre);
            }
            if (e instanceof OverlappingFileLockException) {
                throw new IOException("Ya hay una sesión usando esta carpeta de datos.", e);
            }
            throw e;
        }
    }

    Estado cargar() throws IOException {
        Estado estado = new Estado();
        Path archivo = carpeta.resolve("agencia.tsv");
        if (Files.notExists(archivo)) {
            return estado;
        }
        try (var lector = Files.newBufferedReader(archivo)) {
            if (!"AGENCIA\t1".equals(lector.readLine())) {
                throw new IOException("Formato de datos desconocido.");
            }
            for (String linea; (linea = lector.readLine()) != null;) {
                String[] datos = linea.split("\t", -1);
                switch (datos[0]) {
                    case "V" -> {
                        campos(datos, 6);
                        var vuelo = new Vuelos(datos[1], datos[2], datos[3], new BigDecimal(datos[4]), Integer.parseInt(datos[5]));
                        unico(estado.vuelos(), vuelo.numero(), vuelo);
                    }
                    case "C" -> {
                        campos(datos, 5);
                        var cliente = new Clientes(datos[1], datos[2], datos[3], datos[4]);
                        unico(estado.clientes(), cliente.dni(), cliente);
                    }
                    case "S" -> {
                        campos(datos, 11);
                        var venta = new Ventas(datos[1], datos[2], datos[3], datos[4], datos[5], datos[6],
                                new BigDecimal(datos[7]), Integer.parseInt(datos[8]), Instant.parse(datos[9]),
                                datos[10].isEmpty() ? null : Instant.parse(datos[10]));
                        unico(estado.ventas(), venta.numero(), venta);
                    }
                    default -> throw new IOException("Tipo de registro desconocido.");
                }
            }
            for (Ventas venta : estado.ventas().values()) {
                if (venta.activa() && (!estado.clientes().containsKey(venta.dni()) || !estado.vuelos().containsKey(venta.vuelo()))) {
                    throw new IOException("Venta activa con un cliente o vuelo inexistente.");
                }
            }
            for (Vuelos vuelo : estado.vuelos().values()) {
                if (estado.ocupados(vuelo.numero()) > vuelo.capacidad()) {
                    throw new IOException("La ocupación supera la capacidad de un vuelo.");
                }
            }
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            throw new IOException("El archivo de datos contiene registros inválidos.", e);
        }
        return estado;
    }

    private static void campos(String[] datos, int cantidad) throws IOException {
        if (datos.length != cantidad) {
            throw new IOException("Un registro tiene un número incorrecto de campos.");
        }
    }

    private static <T> void unico(Map<String, T> mapa, String id, T valor) throws IOException {
        if (mapa.putIfAbsent(id, valor) != null) {
            throw new IOException("Hay identificadores duplicados en los datos.");
        }
    }

    void guardar(Estado estado) throws IOException {
        reemplazar("agencia.tsv", salida -> {
            fila(salida, "AGENCIA", "1");
            for (Vuelos v : estado.vuelos().values()) {
                fila(salida, "V", v.numero(), v.origen(), v.destino(), v.precio().toPlainString(), String.valueOf(v.capacidad()));
            }
            for (Clientes c : estado.clientes().values()) {
                fila(salida, "C", c.dni(), c.nombre(), c.apellidos(), c.direccion());
            }
            for (Ventas v : estado.ventas().values()) {
                fila(salida, "S", v.numero(), v.dni(), v.cliente(), v.vuelo(), v.origen(), v.destino(),
                        v.precio().toPlainString(), String.valueOf(v.cantidad()), v.fecha().toString(),
                        v.activa() ? "" : v.anulacion().toString());
            }
        });
    }

    Path exportar(String boletas) throws IOException {
        reemplazar("boletas.txt", salida -> salida.write(boletas));
        return carpeta.resolve("boletas.txt");
    }

    private static void fila(BufferedWriter salida, String... campos) throws IOException {
        salida.write(String.join("\t", campos));
        salida.newLine();
    }

    @FunctionalInterface
    private interface Escritura {
        void escribir(BufferedWriter salida) throws IOException;
    }

    private void reemplazar(String nombre, Escritura escritura) throws IOException {
        Path temporal = Files.createTempFile(carpeta, ".agencia-", ".tmp");
        try {
            try (var salida = Files.newBufferedWriter(temporal)) {
                escritura.escribir(salida);
            }
            try (var archivo = FileChannel.open(temporal, StandardOpenOption.WRITE)) {
                archivo.force(true);
            }
            Files.move(temporal, carpeta.resolve(nombre), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    @Override
    public void close() throws IOException {
        try (canal; bloqueo) {
            // Se libera primero el bloqueo y luego el canal.
        }
    }
}
