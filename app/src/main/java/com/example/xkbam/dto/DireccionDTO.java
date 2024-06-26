package com.example.xkbam.dto;

public class DireccionDTO {
    private String estado;
    private String municipio;
    private String codigoPostal;
    private String calle;
    private int numeroExterno;
    private String usuario;

    // Getters and Setters
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getNumeroExterno() {
        return numeroExterno;
    }

    public void setNumeroExterno(int numeroExterno) {
        this.numeroExterno = numeroExterno;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return getCalle() + ", " + getNumeroExterno() + ", " + getEstado(); // Ejemplo: Calle Ejemplo 123, Ciudad, Estado
    }
}
