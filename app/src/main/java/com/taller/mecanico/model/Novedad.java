package com.taller.mecanico.model;

public class Novedad {
    private int    idNovedad;
    private int    idReparacion;
    private String fechaNovedad;
    private String descripcion;

    public Novedad() {}

    public int    getIdNovedad()     { return idNovedad; }
    public void   setIdNovedad(int v)       { this.idNovedad = v; }
    public int    getIdReparacion()  { return idReparacion; }
    public void   setIdReparacion(int v)    { this.idReparacion = v; }
    public String getFechaNovedad()  { return fechaNovedad; }
    public void   setFechaNovedad(String v) { this.fechaNovedad = v; }
    public String getDescripcion()   { return descripcion; }
    public void   setDescripcion(String v)  { this.descripcion = v; }
}
