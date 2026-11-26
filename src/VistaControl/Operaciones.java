package VistaControl;

import MODEL.Clientes;
import MODEL.Ventas;
import MODEL.Vuelos;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class Operaciones {
    private static final Pattern NUMERO_HISTORICO = Pattern.compile("(?m)^N° de Venta\\h*:\\h*(.+)$");
    private final ArchivoAgencia archivo;
    private final String historico;
    private final Set<String> numerosHistoricos;
    private ArchivoAgencia.Estado estado;

    Operaciones(ArchivoAgencia archivo, Path ventaHistorica) throws IOException {
        this.archivo = archivo;
        estado = archivo.cargar();
        historico = Files.notExists(ventaHistorica) ? "" : Files.readString(ventaHistorica);
        numerosHistoricos = NUMERO_HISTORICO.matcher(historico).results()
                .map(m -> m.group(1).strip().toUpperCase(Locale.ROOT)).collect(Collectors.toUnmodifiableSet());
    }

    private void confirmar(ArchivoAgencia.Estado cambio) throws IOException {
        archivo.guardar(cambio);
        estado = cambio;
    }

    void guardarVuelo(Vuelos vuelo, boolean actualizar) throws IOException {
        comprobarExistencia(estado.vuelos().containsKey(vuelo.numero()), actualizar, "vuelo");
        if (vuelo.capacidad() < estado.ocupados(vuelo.numero())) {
            throw new IllegalArgumentException("La capacidad no puede ser menor que los asientos vendidos.");
        }
        var cambio = estado.copia();
        cambio.vuelos().put(vuelo.numero(), vuelo);
        confirmar(cambio);
    }

    void eliminarVuelo(String numero) throws IOException {
        String id = vuelo(numero).numero();
        if (estado.ocupados(id) > 0) {
            throw new IllegalArgumentException("El vuelo tiene ventas activas.");
        }
        var cambio = estado.copia();
        cambio.vuelos().remove(id);
        confirmar(cambio);
    }

    Vuelos vuelo(String numero) {
        Vuelos vuelo = estado.vuelos().get(Vuelos.codigo(numero));
        comprobarExistencia(vuelo != null, true, "vuelo");
        return vuelo;
    }

    long disponibles(Vuelos vuelo) {
        return vuelo.capacidad() - estado.ocupados(vuelo.numero());
    }

    void listarVuelos() {
        if (estado.vuelos().isEmpty()) {
            System.out.println("No hay vuelos registrados.");
        }
        for (Vuelos v : estado.vuelos().values()) {
            System.out.printf("%s | %s → %s | $%s | %d/%d libres%n",
                    v.numero(), v.origen(), v.destino(), v.precio(), disponibles(v), v.capacidad());
        }
    }

    void guardarCliente(Clientes cliente, boolean actualizar) throws IOException {
        comprobarExistencia(estado.clientes().containsKey(cliente.dni()), actualizar, "cliente");
        var cambio = estado.copia();
        cambio.clientes().put(cliente.dni(), cliente);
        confirmar(cambio);
    }

    Clientes cliente(String dni) {
        Clientes cliente = estado.clientes().get(dni.strip());
        comprobarExistencia(cliente != null, true, "cliente");
        return cliente;
    }

    void eliminarCliente(String dni) throws IOException {
        String id = cliente(dni).dni();
        if (estado.ventas().values().stream().anyMatch(v -> v.activa() && v.dni().equals(id))) {
            throw new IllegalArgumentException("El cliente tiene ventas activas.");
        }
        var cambio = estado.copia();
        cambio.clientes().remove(id);
        confirmar(cambio);
    }

    void listarClientes() {
        if (estado.clientes().isEmpty()) {
            System.out.println("No hay clientes registrados.");
        }
        estado.clientes().values().forEach(System.out::println);
    }

    Ventas vender(String numero, String dni, String vuelo, int cantidad) throws IOException {
        String id = Vuelos.codigo(numero);
        if (estado.ventas().containsKey(id) || numerosHistoricos.contains(id)) {
            throw new IllegalArgumentException("El número de venta ya existe.");
        }
        Clientes c = cliente(dni);
        Vuelos v = vuelo(vuelo);
        if (cantidad < 1 || cantidad > disponibles(v)) {
            throw new IllegalArgumentException("La cantidad excede los asientos disponibles.");
        }
        Ventas venta = new Ventas(id, c.dni(), c.nombreCompleto(), v.numero(), v.origen(), v.destino(),
                v.precio(), cantidad, Instant.now(), null);
        var cambio = estado.copia();
        cambio.ventas().put(id, venta);
        confirmar(cambio);
        return venta;
    }

    void anular(String numero) throws IOException {
        String id = Vuelos.codigo(numero);
        Ventas venta = estado.ventas().get(id);
        if (venta == null || !venta.activa()) {
            throw new IllegalArgumentException("No existe una venta activa con ese número.");
        }
        var cambio = estado.copia();
        Instant ahora = Instant.now();
        cambio.ventas().put(id, venta.anular(ahora.isBefore(venta.fecha()) ? venta.fecha() : ahora));
        confirmar(cambio);
    }

    String historial() {
        String actual = estado.ventas().values().stream().map(Ventas::boleta).collect(Collectors.joining("\n"));
        if (historico.isBlank()) {
            return actual;
        }
        return actual + "\n=== ARCHIVO HISTÓRICO (SOLO CONSULTA) ===\n" + historico;
    }

    Path exportar() throws IOException {
        return archivo.exportar(historial());
    }

    void resumen() {
        var activas = estado.ventas().values().stream().filter(Ventas::activa).toList();
        long boletos = activas.stream().mapToLong(Ventas::cantidad).sum();
        BigDecimal ingresos = activas.stream().map(Ventas::total).reduce(new BigDecimal("0.00"), BigDecimal::add);
        System.out.printf("Vuelos: %d | Clientes: %d%n", estado.vuelos().size(), estado.clientes().size());
        System.out.printf("Ventas activas: %d | Anuladas: %d | Boletos activos: %d | Importe activo: $%s%n",
                activas.size(), estado.ventas().size() - activas.size(), boletos, ingresos);
    }

    private static void comprobarExistencia(boolean existe, boolean esperado, String entidad) {
        if (existe != esperado) {
            throw new IllegalArgumentException(esperado ? "No se encontró el " + entidad + "." : "Ya existe un " + entidad + " con ese identificador.");
        }
    }
}
