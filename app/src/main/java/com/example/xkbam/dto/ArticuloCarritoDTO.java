package com.example.xkbam.dto;

public class ArticuloCarritoDTO {
    private int idArticuloCarrito;
    private int cantidadArticulo;
    private double precioUnitario;
    private double precioFinal;
    private String codigoArticulo;
    private int idCarrito;
    private int idTalla;

    // Constructor vacío necesario para Gson
    public ArticuloCarritoDTO() {
    }

    // Getters y setters
    public int getIdArticuloCarrito() {
        return idArticuloCarrito;
    }

    public void setIdArticuloCarrito(int idArticuloCarrito) {
        this.idArticuloCarrito = idArticuloCarrito;
    }

    public int getCantidadArticulo() {
        return cantidadArticulo;
    }

    public void setCantidadArticulo(int cantidadArticulo) {
        this.cantidadArticulo = cantidadArticulo;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
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
