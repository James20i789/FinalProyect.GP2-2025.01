package UTIL;

import java.io.Console;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Lectura {
    private final Console consola = System.console();
    private final Scanner entrada = consola == null ? new Scanner(System.in) : null;

    public String linea(String mensaje) {
        if (consola != null) {
            String valor = consola.readLine("%s", mensaje);
            if (valor == null) {
                throw new NoSuchElementException();
            }
            return valor;
        }
        System.out.print(mensaje);
        return entrada.nextLine();
    }

    public char[] contrasena(String mensaje) {
        char[] valor = consola == null ? linea(mensaje).toCharArray() : consola.readPassword("%s", mensaje);
        if (valor == null) {
            throw new NoSuchElementException();
        }
        return valor;
    }

    public int entero(String mensaje, int minimo, int maximo) {
        while (true) {
            try {
                int valor = Integer.parseInt(linea(mensaje).strip());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // Se conserva la sesión para corregir el dato.
            }
            System.out.printf("Ingrese un entero entre %d y %d.%n", minimo, maximo);
        }
    }

    public BigDecimal precio(String mensaje) {
        while (true) {
            String valor = linea(mensaje).strip();
            if (valor.matches("[0-9]{1,10}([.,][0-9]{1,2})?")) {
                BigDecimal precio = new BigDecimal(valor.replace(',', '.'));
                if (precio.signum() > 0) {
                    return precio;
                }
            }
            System.out.println("Ingrese un importe positivo con hasta dos decimales.");
        }
    }
}
