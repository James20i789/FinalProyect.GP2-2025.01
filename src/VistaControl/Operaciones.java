/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VistaControl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import MODEL.Clientes;
import MODEL.Ventas;
import MODEL.Vuelos;
import UTIL.Lectura;

/**
 *
 * @JK James Rios
 */
public class Operaciones {

    static Lectura JK = new Lectura();
    static Vuelos[] vuelo = new Vuelos[100];
    static Clientes[] cliente = new Clientes[100];
    static Ventas[] ventas = new Ventas[100];
    static int vuelosIndex = 0, clientesIndex = 0, ventasIndex = 0;

    // GESTIÓN DE VUELOS
    public static void crearVuelos() {
        System.out.print("Número de vuelos a registrar: ");
        int n = JK.entero();
        for (int i = 0; i < n; i++) {
            System.out.println("\nVuelo " + (i + 1));
            System.out.print("NÚMERO: ");
            String numero = JK.cadena();

            System.out.print("ORIGEN: ");
            String origen = JK.cadena();

            System.out.print("DESTINO: ");
            String destino = JK.cadena();

            System.out.print("COSTO: ");
            double costo = JK.decimal();

            System.out.print("ASIENTOS: ");
            int asientos = JK.entero();

            vuelo[vuelosIndex++] = new Vuelos(numero, origen, destino, costo, asientos);
        }
    }

    public static void verVuelos() {
        if (vuelosIndex > 0) {
            System.out.println(" ----- |VUELOS REGISTRADOS| ----");
            for (int i = 0; i < vuelosIndex; i++) {
                System.out.println(vuelo[i]);
            }
        } else {
            System.out.println("NO SE ENCONTRARON VUELOS DISPONIBLES");
        }
    }

    public static void actualizarVuelo() {
        System.out.print("Código del vuelo a actualizar: ");
        String cod = JK.cadena();
        for (int i = 0; i < vuelosIndex; i++) {
            if (vuelo[i].getNumeroVuelo().equals(cod)) {
                System.out.print("NUEVO ORIGEN: ");
                String origen = JK.cadena();

                System.out.print("NUEVO DESTINO: ");
                String destino = JK.cadena();

                System.out.print("NUEVO COSTO: ");
                double costo = JK.decimal();

                System.out.print("NUEVOS ASIENTOS: ");
                int asientos = JK.entero();

                vuelo[i] = new Vuelos(cod, origen, destino, costo, asientos);
                System.out.println("Vuelo actualizado.");
                return;
            }
        }
        System.out.println("VUELO NO ENCONTRADO.");
    }

    public static void eliminarVuelo() {
        verVuelos();
        System.out.print("\nNúmero de vuelo a eliminar: ");
        String cod = JK.cadena();
        for (int i = 0; i < vuelosIndex; i++) {
            if (vuelo[i].getNumeroVuelo().equals(cod)) {
                vuelo[i] = vuelo[--vuelosIndex];
                System.out.println("Vuelo eliminado.");
                return;
            }
        }
        System.out.println("VUELO NO ENCONTRADO.");
    }

    // GESTIÓN DE CLIENTES
    public static void crearCliente() {
        System.out.print("Cantidad de clientes a registrar: ");
        int n = JK.entero();
        for (int i = 0; i < n; i++) {
            System.out.println("\nCliente " + (i + 1));
            System.out.print("Nombre: ");
            String nombre = JK.cadena();
            System.out.print("Apellidos: ");
            String apellido = JK.cadena();
            System.out.print("DNI: ");
            int dni = JK.entero();
            System.out.print("Dirección: ");
            String direccion = JK.cadena();
            cliente[clientesIndex++] = new Clientes(dni, nombre, apellido, direccion);
        }
    }
    public static void verCliente() {
            if (clientesIndex > 0) {
            System.out.println(" ----- CLIENTES REGISTRADOS -----");
            for (int i = 0; i < clientesIndex; i++) {
                System.out.println(cliente[i].toString());
            }
        } else {
            System.out.println("No hay clientes disponibles");
        }
    }


    public static void actualizarCliente() {
       System.out.print("DNI del cliente a actualizar: ");
        int Dni = JK.entero();
        for (int i = 0; i < clientesIndex; i++) {
            if (cliente[i].getDni() == Dni) {
                System.out.print("Nuevo nombre: ");
                String nombre = JK.cadena();
                System.out.print("Nuevos apellidos: ");
                String apellido = JK.cadena();
                System.out.print("Nueva dirección: ");
                String direccion = JK.cadena();
                cliente[i] = new Clientes(Dni, nombre, apellido, direccion);
                System.out.println("Cliente actualizado.");
                return;
            }
        }
        System.out.println("Cliente no encontrado.");
    }

    public static void eliminarCliente() {
        System.out.print("DNI del cliente a eliminar: ");
        int Dni = JK.entero();
        for (int i = 0; i < clientesIndex; i++) {
            if (cliente[i].getDni()== Dni) {
                cliente[i] = cliente[--clientesIndex];
                System.out.println("Cliente eliminado.");
                return;
            }
        }
        System.out.println("Cliente no encontrado.");
    }

    // GESTIÓN DE VENTAS
    public static void generarVenta() {
        System.out.print("DNI del cliente: ");
        int dni = JK.entero();

        Clientes c = null;
        for (int i = 0; i < clientesIndex; i++) {
            int Dni=0;
            if (cliente[i].getDni()== Dni) {
                c = cliente[i];
                break;
            }
        }
        if (c == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.print("Número de vuelo: ");
        String cod = JK.cadena();
        Vuelos v = null;
        for (int i = 0; i < vuelosIndex; i++) {
            if (vuelo[i].getNumeroVuelo().equals(cod)) {
                v = vuelo[i];
                break;
            }
        }
        if (v == null) {
            System.out.println("Vuelo no encontrado.");
            return;
        }

        if (v.getAsientosDisponibles() <= 0) {
            System.out.println("No hay asientos disponibles.");
            return;
        }

        System.out.print("Número de venta: ");
        String nro = JK.cadena();

        ventas[ventasIndex++] = new Ventas(v, c, nro, 1);
        v.venderAsiento();
        System.out.println("FELICIDADES. Venta realizada.");

        try {
            FileWriter archivo = new FileWriter("venta.txt", true); // true = agregar al final
            PrintWriter escritor = new PrintWriter(archivo);
            escritor.println(ventas[ventasIndex - 1].toString()); // usa el toString formateado como boleta
            escritor.println(); // línea en blanco entre boletas
            escritor.close();
            System.out.println("< Boleta guardada en venta.txt >");
        } catch (IOException e) {
            System.out.println("Error al guardar la boleta: " + e.getMessage());
        }
    }

    public static void verVenta() {
        System.out.println("================== HISTORIAL DE VENTAS ================");
        try {
            BufferedReader lector = new BufferedReader(new FileReader("venta.txt"));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println(linea);
            }
            lector.close();
        } catch (IOException e) {
            System.out.println("Error al leer las ventas: " + e.getMessage());
        }
    }
}
