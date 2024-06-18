package com.example.xkbam.utilidades;

public class SesionSingleton {
    private static SesionSingleton instance;
    private String usuario;
    private String nombre;
    private String correo;
    private static String JWT;
    private int rol;

    private SesionSingleton() { }

    public static SesionSingleton getInstance() {
        if (instance == null) {
            instance = new SesionSingleton();
        }
        return instance;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public static String getJWT() {
        return JWT;
    }

    public static void setJWT(String jwt) {
        JWT = jwt;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public void clear() {
        usuario = null;
        nombre = null;
        correo = null;
        JWT = null;
        rol = 0;
    }
}
