    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VistaControl;

import UTIL.Lectura;

/**
 *
 * @JK James Rios
 */
public class Menu {
    
    private static Lectura JK = new Lectura();
    private static Operaciones op = new Operaciones();
    private static int intentos = 0;
    private static boolean acceso = false;
    
    //Login situation. Entrada
    public static void validar() {
        do {
            System.out.print("USER: ");
            String user = JK.cadena();
            System.out.print("PASSWORD: ");
            String pass = JK.cadena();
            if (user.equals("James") && pass.equals("202512003")) {
                acceso = true;
                System.out.println("\n><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
                System.out.println("              |BIENVENIDO A UNIONSHOP MARKET - AGENCY|         ");
                System.out.println("><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
                System.out.println("-----------------------------------------------------------------------");
                System.out.println(" |USUARIO| " + user);
                menu_opcion_principal();
                break;
            } else {
                System.out.println(" ------ |CREDENCIALES INCORRECTAS| ------- ");
                intentos++;
            }
        } while (intentos < 3);

        if (!acceso) {
            System.out.println("------ ACCESO DENEGADO: Programa finalizado. ------");
            JK.cerrar();
        }
        
    }
    //MENÚ de OPCIONES - FORMATO PRINCIPAL
    public static void menu_opcion_principal() {
        int opcion;
        do {
            System.out.println("\n=============================================");
            System.out.println(" -------------- MENU PRINCIPAL ------------ ");
            System.out.println("==============================================");
            System.out.println("1. Registrar vuelo");
            System.out.println("2. Registrar cliente");
            System.out.println("3. Registrar venta");
            System.out.println("4. Salir");
            System.out.println("  MAIN MENU SELECTION   |1-4|: ");
            opcion = JK.entero();

            switch (opcion) {
                case 1:
                    menuVuelos();
                    break;
                case 2:
                    menuClientes();
                    break;
                case 3:
                    menuVentas();
                    break;
                case 4:
                    System.out.println(" ---------- |MUCHAS GRACIAS POR USAR EL SISTEMA. HASTA LUEGO| ------");
                    break;
                default:
                    System.out.println("Opción no válida. Intentar nuevamente.");
                   JK.cerrar();
            }
        } while (opcion != 4);

       

    }
    //MENÚ - VUELOS
    private static void menuVuelos() {
        int opV;
        do {
            System.out.println("\n======================================");
            System.out.println("---------------- VUELOS --------------");
            System.out.println("=======================================");
            System.out.println("1. Crear");
            System.out.println("2. Ver");
            System.out.println("3. Eliminar");
            System.out.println("4. Actualizar");
            System.out.println("5. Volver");
            System.out.println("  MAIN MENU SELECTION - VUELOS  |1-5|: ");
            opV = JK.entero();

            switch (opV) {
                case 1:
                    op.crearVuelos();

                    break;
                case 2:
                    op.verVuelos();
                    break;
                case 3:
                    System.out.print("Número de vuelo a eliminar: ");
                    op.eliminarVuelo();
                    break;
                case 4:
                    op.actualizarVuelo();
                    break;
                case 5:
                    System.out.println("VOLVIENDO AL MENÚ PRINCIPAL ........");
                    break;
                default:
                    System.out.println("Opción no válida. Intentar nuevamente");
                    break;
            }
        } while (opV != 5);
    }
    // MENÚ - CLIENTES
    private static void menuClientes() {
        int opC;
        do {
            System.out.println("\n=====================================");
            System.out.println("--------------- CLIENTES --------------");
            System.out.println("========================================v");
            System.out.println("1. Crear");
            System.out.println("2. Ver");
            System.out.println("3. Eliminar");
            System.out.println("4. Actualizar");
            System.out.println("5. Volver");
            System.out.println("  MAIN MENU SELECTION  - CLIENTES |1-5|: ");
            opC = JK.entero();

            switch (opC) {
                case 1:
                    op.crearCliente();
                    break;
                case 2:
                    op.verCliente();
                    break;
                case 3:
                    op.eliminarCliente();
                    break;
                case 4:
                    op.actualizarCliente();
                    break;
                 case 5:
                     System.out.println("VOLVIENDO AL MENÚ PRINCIPAL ........");
                    break;
                default:
                    System.out.println("Opción no válida. Intentar nuevamente");
                    break;
            }
        } while (opC != 5);
    }
    //MENÚ - VENTAS
    private static void menuVentas() {
        int opCV;
        do {
            System.out.println("\n============================================");
            System.out.println("------------------- VENTAS -----------------");
            System.out.println("=============================================");
            System.out.println("1. Crear");
            System.out.println("2. Ver");
            System.out.println("3. Volver");
            System.out.println("  MAIN MENU SELECTION - VENTAS  |1-3|: ");
            opCV = JK.entero();

            switch (opCV) {
                case 1:
                    op.generarVenta();
                    break;
                case 2:
                    op.verVenta();
                    break;
                case 3:
                    System.out.println("VOLVIENDO AL MENÚ PRINCIPAL ......");
                    break;
                default:
                    System.out.println("Opción no válida. Intentar nuevamente");
                    break;

            }
        } while (opCV != 3);
    }

}
