/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @JK James Rios
 */
public class Clientes {
    int dni;
    String nombre,apellidos,direccion;

    public Clientes(int dni, String nombre, String apellidos, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
    }

    public int getDni() {
        return dni;
    }
    @Override
    public String toString() {
        return nombre + " " + apellidos + " | DNI: " + dni + " | Dirección: " + direccion;
    }
}
