package com.taller.mecanico.model;

public class Cliente {
    private int     idCliente;
    private String  cedula;
    private String  nombre;
    private String  apellido;
    private String  telefono;
    private String  correo;
    private boolean activo;

    public Cliente() {}

    public int     getIdCliente()  { return idCliente; }
    public void    setIdCliente(int v)    { this.idCliente = v; }
    public String  getCedula()     { return cedula; }
    public void    setCedula(String v)    { this.cedula = v; }
    public String  getNombre()     { return nombre; }
    public void    setNombre(String v)    { this.nombre = v; }
    public String  getApellido()   { return apellido; }
    public void    setApellido(String v)  { this.apellido = v; }
    public String  getTelefono()   { return telefono; }
    public void    setTelefono(String v)  { this.telefono = v; }
    public String  getCorreo()     { return correo; }
    public void    setCorreo(String v)    { this.correo = v; }
    public boolean isActivo()      { return activo; }
    public void    setActivo(boolean v)   { this.activo = v; }

    /** Nombre completo para mostrar en listas */
    public String getNombreCompleto() { return nombre + " " + apellido; }
}
