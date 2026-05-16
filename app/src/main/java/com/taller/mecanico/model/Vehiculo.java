package com.taller.mecanico.model;

public class Vehiculo {
    private int    idVehiculo;
    private int    idCliente;
    private String placa;
    private String marca;
    private String modelo;
    private int    anio;

    public Vehiculo() {}

    public int    getIdVehiculo() { return idVehiculo; }
    public void   setIdVehiculo(int v)   { this.idVehiculo = v; }
    public int    getIdCliente()  { return idCliente; }
    public void   setIdCliente(int v)    { this.idCliente = v; }
    public String getPlaca()      { return placa; }
    public void   setPlaca(String v)     { this.placa = v; }
    public String getMarca()      { return marca; }
    public void   setMarca(String v)     { this.marca = v; }
    public String getModelo()     { return modelo; }
    public void   setModelo(String v)    { this.modelo = v; }
    public int    getAnio()       { return anio; }
    public void   setAnio(int v)         { this.anio = v; }

    public String getDescripcion() { return marca + " " + modelo + " (" + anio + ")"; }
}
