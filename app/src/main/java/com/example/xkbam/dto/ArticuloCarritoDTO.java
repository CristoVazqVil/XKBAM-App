package com.example.xkbam.dto;

public class ArticuloCarritoDTO {
    private int idArticuloCarrito;
    private String codigoArticulo;
    private int cantidadArticulo;
    private int idCarrito;
    private int idTalla;

    // Constructor
    public ArticuloCarritoDTO(String codigoArticulo, int cantidadArticulo) {
        this.codigoArticulo = codigoArticulo;
        this.cantidadArticulo = cantidadArticulo;
    }

    // Getters and Setters
    public int getIdArticuloCarrito() {
        return idArticuloCarrito;
    }

    public void setIdArticuloCarrito(int idArticuloCarrito) {
        this.idArticuloCarrito = idArticuloCarrito;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public int getCantidadArticulo() {
        return cantidadArticulo;
    }

    public void setCantidadArticulo(int cantidadArticulo) {
        this.cantidadArticulo = cantidadArticulo;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public int getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(int idTalla) {
        this.idTalla = idTalla;
    }
}
