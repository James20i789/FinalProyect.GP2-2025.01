package VistaControl;

import UTIL.Lectura;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

public class INDEX {
    public static void main(String[] args) {
        Lectura entrada = new Lectura();
        try {
            if (!Menu.validar(entrada)) {
                return;
            }
            Path datos = Path.of(args.length == 0 ? "datos" : args[0]);
            try (ArchivoAgencia archivo = new ArchivoAgencia(datos)) {
                new Menu(entrada, new Operaciones(archivo, Path.of("venta.txt"))).principal();
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada. Los cambios confirmados se conservan.");
        } catch (IOException e) {
            System.err.println("No se pudo abrir o cerrar la agencia: " + e.getMessage());
        }
    }
}
