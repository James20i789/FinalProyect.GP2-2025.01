/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

/**
 *
 * @JK James Rios
 */
public class Ventas {
    private Vuelos vuelo;
    private Clientes cliente;
    private String nroVenta;
    private int cantidadBoletos;

    public Ventas(Vuelos vuelo, Clientes cliente, String nroVenta, int cantidadBoletos) {
        this.vuelo = vuelo;
        this.cliente = cliente;
        this.nroVenta = nroVenta;
        this.cantidadBoletos = cantidadBoletos;
    }
    
    

  @Override
  public String toString() {
    return "======================= BOLETA DE VENTA =======================\n" +
           "N° de Venta     : " + nroVenta + "\n" +
           "Cliente         : " + cliente.getDni() + " - " + cliente + "\n" +
           "--------------------------------------------------------------\n" +
           "Vuelo           : " + vuelo.getNumeroVuelo() + "\n" +
           "Origen          : " + vuelo.getOrigen() + "\n" +
           "Destino         : " + vuelo.getDestino() + "\n" +
           "Precio X Asiento:$" + vuelo.getCosto() + "\n" +
           "-------------------------------------------------------------\n" +
           "TOTAL A PAGAR   :$" + vuelo.getCosto() + "\n" +
           "===============================================================";
}
}
