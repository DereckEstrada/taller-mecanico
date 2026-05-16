package com.taller.mecanico.model;

public class Tecnico {
    private int     idTecnico;
    private String  cedula;
    private String  nombre;
    private String  apellido;
    private String  telefono;
    private String  especialidad;
    private boolean activo;

    public Tecnico() {}

    public int     getIdTecnico()    { return idTecnico; }
    public void    setIdTecnico(int v)      { this.idTecnico = v; }
    public String  getCedula()       { return cedula; }
    public void    setCedula(String v)      { this.cedula = v; }
    public String  getNombre()       { return nombre; }
    public void    setNombre(String v)      { this.nombre = v; }
    public String  getApellido()     { return apellido; }
    public void    setApellido(String v)    { this.apellido = v; }
    public String  getTelefono()     { return telefono; }
    public void    setTelefono(String v)    { this.telefono = v; }
    public String  getEspecialidad() { return especialidad; }
    public void    setEspecialidad(String v){ this.especialidad = v; }
    public boolean isActivo()        { return activo; }
    public void    setActivo(boolean v)     { this.activo = v; }

    public String getNombreCompleto() { return nombre + " " + apellido; }
}
