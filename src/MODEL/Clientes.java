package MODEL;

public record Clientes(String dni, String nombre, String apellidos, String direccion) {
    public Clientes {
        if (!dni.matches("[0-9]{8}")) {
            throw new IllegalArgumentException("El DNI debe tener ocho dígitos.");
        }
        nombre = Vuelos.texto(nombre);
        apellidos = Vuelos.texto(apellidos);
        direccion = Vuelos.texto(direccion);
    }

    public String nombreCompleto() {
        return nombre + " " + apellidos;
    }

    @Override
    public String toString() {
        return nombreCompleto() + " | DNI " + dni + " | " + direccion;
    }
}
