package com.taller.mecanico.model;

import java.util.List;

public class Reparacion {
    private int idReparacion;
    private Cliente cliente;
    private Tecnico tecnico;
    private Vehiculo vehiculo;
    private String fechaIngreso;
    private String fechaRetiro;
    private String estado;
    private String descripcion;
    private double costo;

    List<RepuestoReparacion> repuestos;
    List<Novedad> novedades;

    public Reparacion() {}

    public int getIdReparacion(){ return idReparacion; }
    public void setIdReparacion(int v){ this.idReparacion = v; }
    public Cliente getCliente(){ return this.cliente; }
    public void setCliente(Cliente c){ this.cliente = c; }
    public Tecnico getTecnico(){ return tecnico; }
    public void setTecnico(Tecnico v){ this.tecnico = v; }
    public Vehiculo getVehiculo(){ return vehiculo; }
    public void setVehiculo(Vehiculo v){ this.vehiculo = v; }
    public String getFechaIngreso()  { return fechaIngreso; }
    public void setFechaIngreso(String v) { this.fechaIngreso = v; }
    public String getFechaRetiro(){ return fechaRetiro; }
    public void setFechaRetiro(String v){ this.fechaRetiro = v; }
    public String getEstado(){ return estado; }
    public void setEstado(String v){ this.estado = v; }
    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String v){ this.descripcion = v; }
    public double getCosto(){ return costo; }
    public void setCosto(double v) { this.costo = v; }

    // JOIN
    public String getNombreCliente()      { return nombreCliente; }
    public void   setNombreCliente(String v)    { this.nombreCliente = v; }
    public String getNombreTecnico()      { return nombreTecnico; }
    public void   setNombreTecnico(String v)    { this.nombreTecnico = v; }
    public String getPlacaVehiculo()      { return placaVehiculo; }
    public void   setPlacaVehiculo(String v)    { this.placaVehiculo = v; }
    public String getInfoVehiculo()       { return infoVehiculo; }
    public void   setInfoVehiculo(String v)     { this.infoVehiculo = v; }

    /** Etiqueta legible del estado para la UI */
    public String getEstadoLabel() {
        if (estado == null) return "—";
        switch (estado) {
            case "EN_DIAGNOSTICO":      return "En Diagnóstico";
            case "EN_REPARACION":       return "En Reparación";
            case "ESPERANDO_REPUESTOS": return "Esperando Repuestos";
            case "LISTO_ENTREGA":       return "Listo para Entrega";
            case "ENTREGADO":           return "Entregado";
            default:                    return estado;
        }
    }
}
