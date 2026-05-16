package com.taller.mecanico.model;

public class RepuestoReparacion {
    private int    idDetalle;
    private int    idReparacion;
    private int    idRepuesto;
    private int    cantidad;
    private double subtotal;

    // JOIN
    private String nombreRepuesto;
    private double precioRepuesto;

    public RepuestoReparacion() {}

    public int    getIdDetalle()      { return idDetalle; }
    public void   setIdDetalle(int v)        { this.idDetalle = v; }
    public int    getIdReparacion()   { return idReparacion; }
    public void   setIdReparacion(int v)     { this.idReparacion = v; }
    public int    getIdRepuesto()     { return idRepuesto; }
    public void   setIdRepuesto(int v)       { this.idRepuesto = v; }
    public int    getCantidad()       { return cantidad; }
    public void   setCantidad(int v)         { this.cantidad = v; }
    public double getSubtotal()       { return subtotal; }
    public void   setSubtotal(double v)      { this.subtotal = v; }
    public String getNombreRepuesto() { return nombreRepuesto; }
    public void   setNombreRepuesto(String v){ this.nombreRepuesto = v; }
    public double getPrecioRepuesto() { return precioRepuesto; }
    public void   setPrecioRepuesto(double v){ this.precioRepuesto = v; }
}
