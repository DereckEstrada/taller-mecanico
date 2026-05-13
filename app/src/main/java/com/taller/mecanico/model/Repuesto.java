package com.taller.mecanico.model;

public class Repuesto {
    private int     idRepuesto;
    private String  nombre;
    private double  precio;
    private int     stock;
    private boolean activo;

    public Repuesto() {}

    public int     getIdRepuesto() { return idRepuesto; }
    public void    setIdRepuesto(int v)    { this.idRepuesto = v; }
    public String  getNombre()     { return nombre; }
    public void    setNombre(String v)     { this.nombre = v; }
    public double  getPrecio()     { return precio; }
    public void    setPrecio(double v)     { this.precio = v; }
    public int     getStock()      { return stock; }
    public void    setStock(int v)         { this.stock = v; }
    public boolean isActivo()      { return activo; }
    public void    setActivo(boolean v)    { this.activo = v; }
}
