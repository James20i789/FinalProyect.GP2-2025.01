/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @JK James Rios
 */
public class Vuelos {
    String numeroVuelo, origen, destino;
    double costo;
    int asientosDisponibles;

    public Vuelos(String numeroVuelo, String origen, String destino, double costo, int asientosDisponibles) {
        this.numeroVuelo = numeroVuelo;
        this.origen = origen;
        this.destino = destino;
        this.costo = costo;
        this.asientosDisponibles = asientosDisponibles;
    }
    //MÉTODOS DE MANIPULACIÓN DE LA CLASE. Sirve para obtener datos de la clase    
    public String getNumeroVuelo() {
        return numeroVuelo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getCosto() {
        return costo;
    }

    public int getAsientosDisponibles() {
        return asientosDisponibles;
    }
    
    public void venderAsiento() {
        if (asientosDisponibles > 0) asientosDisponibles--;
    }
    
    @Override
    public String toString() {
        return "Vuelo: " + numeroVuelo + " | Origen: " + origen + " | Destino: " + destino +
               " | Costo: " + costo + " | Asientos: " + asientosDisponibles;
    }
}
  

