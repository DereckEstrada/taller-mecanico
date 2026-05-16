package com.taller.mecanico.model;

public class Usuario {
    private int     idUsuario;
    private String  username;
    private String  password;
    private String  rol;          // ADMIN | MECANICO | CLIENTE
    private int     idReferencia; // id del técnico o cliente vinculado
    private boolean activo;

    public Usuario() {}

    // Getters y Setters
    public int     getIdUsuario()    { return idUsuario; }
    public void    setIdUsuario(int v)      { this.idUsuario = v; }
    public String  getUsername()     { return username; }
    public void    setUsername(String v)    { this.username = v; }
    public String  getPassword()     { return password; }
    public void    setPassword(String v)    { this.password = v; }
    public String  getRol()          { return rol; }
    public void    setRol(String v)         { this.rol = v; }
    public int     getIdReferencia() { return idReferencia; }
    public void    setIdReferencia(int v)   { this.idReferencia = v; }
    public boolean isActivo()        { return activo; }
    public void    setActivo(boolean v)     { this.activo = v; }
}
