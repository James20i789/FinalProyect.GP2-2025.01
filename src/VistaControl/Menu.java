package VistaControl;

import MODEL.Clientes;
import MODEL.Vuelos;
import UTIL.Lectura;
import java.io.IOException;
import java.util.Arrays;

final class Menu {
    private final Lectura entrada;
    private final Operaciones operaciones;

    Menu(Lectura entrada, Operaciones operaciones) {
        this.entrada = entrada;
        this.operaciones = operaciones;
    }

    static boolean validar(Lectura entrada) {
        String usuario = System.getenv("AGENCIA_USUARIO");
        String clave = System.getenv("AGENCIA_CLAVE");
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            System.err.println("Configure AGENCIA_USUARIO y AGENCIA_CLAVE antes de iniciar.");
            return false;
        }

        char[] esperada = clave.toCharArray();
        try {
            return autenticar(entrada, usuario, esperada);
        } finally {
            Arrays.fill(esperada, '\0');
        }
    }

    private static boolean autenticar(Lectura entrada, String usuario, char[] esperada) {
        for (int intento = 0; intento < 3; intento++) {
            String candidato = entrada.linea("Usuario: ");
            char[] contrasena = entrada.contrasena("Contraseña: ");
            boolean coincide = usuario.equals(candidato) && Arrays.equals(esperada, contrasena);
            Arrays.fill(contrasena, '\0');
            if (coincide) {
                System.out.println("\nUNIONSHOP MARKET - AGENCY");
                return true;
            }
            System.out.println("Credenciales incorrectas.");
        }
        System.out.println("Acceso denegado.");
        return false;
    }

    void principal() {
        while (true) {
            System.out.println("\n1. Vuelos   2. Clientes   3. Ventas   4. Salir");
            switch (entrada.entero("Opción: ", 1, 4)) {
                case 1 -> vuelos();
                case 2 -> clientes();
                case 3 -> ventas();
                case 4 -> { return; }
                default -> throw new AssertionError();
            }
        }
    }

    private void vuelos() {
        while (true) {
            System.out.println("\nVuelos | 1. Crear   2. Ver   3. Eliminar   4. Actualizar   5. Volver");
            int opcion = entrada.entero("Opción: ", 1, 5);
            if (opcion == 5) {
                return;
            }
            try {
                switch (opcion) {
                    case 1, 4 -> {
                        if (opcion == 1) {
                            int cantidad = entrada.entero("Vuelos a registrar: ", 1, Integer.MAX_VALUE);
                            for (int i = 0; i < cantidad; i++) {
                                System.out.println("Vuelo " + (i + 1) + " de " + cantidad);
                                registrarVuelo(false);
                            }
                        } else {
                            registrarVuelo(true);
                        }
                    }
                    case 2 -> operaciones.listarVuelos();
                    case 3 -> {
                        operaciones.eliminarVuelo(entrada.linea("Código de vuelo: "));
                        System.out.println("Vuelo eliminado.");
                    }
                    default -> throw new AssertionError();
                }
            } catch (IOException | IllegalArgumentException e) {
                error(e);
            }
        }
    }

    private void registrarVuelo(boolean actualizar) throws IOException {
        String numero = entrada.linea("Código de vuelo: ");
        if (actualizar) {
            operaciones.vuelo(numero);
        }
        String origen = entrada.linea("Origen: ");
        String destino = entrada.linea("Destino: ");
        var precio = entrada.precio("Precio por asiento: ");
        int capacidad = entrada.entero("Capacidad total: ", 1, Integer.MAX_VALUE);
        operaciones.guardarVuelo(new Vuelos(numero, origen, destino, precio, capacidad), actualizar);
        System.out.println("Vuelo guardado.");
    }

    private void clientes() {
        while (true) {
            System.out.println("\nClientes | 1. Crear   2. Ver   3. Eliminar   4. Actualizar   5. Volver");
            int opcion = entrada.entero("Opción: ", 1, 5);
            if (opcion == 5) {
                return;
            }
            try {
                switch (opcion) {
                    case 1, 4 -> {
                        if (opcion == 1) {
                            int cantidad = entrada.entero("Clientes a registrar: ", 1, Integer.MAX_VALUE);
                            for (int i = 0; i < cantidad; i++) {
                                System.out.println("Cliente " + (i + 1) + " de " + cantidad);
                                registrarCliente(false);
                            }
                        } else {
                            registrarCliente(true);
                        }
                    }
                    case 2 -> operaciones.listarClientes();
                    case 3 -> {
                        operaciones.eliminarCliente(entrada.linea("DNI: "));
                        System.out.println("Cliente eliminado.");
                    }
                    default -> throw new AssertionError();
                }
            } catch (IOException | IllegalArgumentException e) {
                error(e);
            }
        }
    }

    private void registrarCliente(boolean actualizar) throws IOException {
        String dni = entrada.linea("DNI: ").strip();
        if (actualizar) {
            operaciones.cliente(dni);
        }
        String nombre = entrada.linea("Nombre: ");
        String apellidos = entrada.linea("Apellidos: ");
        String direccion = entrada.linea("Dirección: ");
        operaciones.guardarCliente(new Clientes(dni, nombre, apellidos, direccion), actualizar);
        System.out.println("Cliente guardado.");
    }

    private void ventas() {
        while (true) {
            System.out.println("\nVentas | 1. Crear   2. Historial   3. Volver   4. Anular   5. Exportar boletas   6. Resumen");
            switch (entrada.entero("Opción: ", 1, 6)) {
                case 1 -> {
                    try {
                        String dni = operaciones.cliente(entrada.linea("DNI del cliente: ")).dni();
                        var vuelo = operaciones.vuelo(entrada.linea("Código de vuelo: "));
                        int disponibles = (int) operaciones.disponibles(vuelo);
                        if (disponibles == 0) {
                            System.out.println("No quedan asientos disponibles.");
                            continue;
                        }
                        int cantidad = entrada.entero("Cantidad de boletos: ", 1, disponibles);
                        String numero = entrada.linea("Número de venta: ");
                        var venta = operaciones.vender(numero, dni, vuelo.numero(), cantidad);
                        System.out.println("Venta registrada.\n" + venta.boleta());
                    } catch (IOException | IllegalArgumentException e) {
                        error(e);
                    }
                }
                case 2 -> {
                    String historial = operaciones.historial();
                    System.out.println(historial.isBlank() ? "No hay boletas registradas." : historial);
                }
                case 3 -> { return; }
                case 4 -> {
                    try {
                        String numero = entrada.linea("Número de venta: ");
                        if (entrada.linea("¿Confirmar anulación? [s/N]: ").strip().equalsIgnoreCase("s")) {
                            operaciones.anular(numero);
                            System.out.println("Venta anulada. Asientos liberados.");
                        }
                    } catch (IOException | IllegalArgumentException e) {
                        error(e);
                    }
                }
                case 5 -> {
                    try {
                        System.out.println("Boletas exportadas a " + operaciones.exportar());
                    } catch (IOException e) {
                        error(e);
                    }
                }
                case 6 -> operaciones.resumen();
                default -> throw new AssertionError();
            }
        }
    }

    private static void error(Exception e) {
        System.out.println("No se pudo completar la operación. " + e.getMessage());
    }
}
