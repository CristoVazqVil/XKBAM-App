package com.example.xkbam.dto;

public class ArticuloCompraDTO {
    private int idArticuloCompra;
    private int cantidadArticulo;
    private double precioUnitario;
    private double precioFinal;
    private String codigoArticulo;
    private int idCompra;
    private int idTalla;

    // Constructor vacío necesario para Gson
    public ArticuloCompraDTO() {
    }

    // Getters y setters
    public int getIdArticuloCompra() {
        return idArticuloCompra;
    }

    public void setIdArticuloCompra(int idArticuloCarrito) {
        this.idArticuloCompra = idArticuloCarrito;
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

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdTalla() {
        return idTalla;
    }

    public void setIdTalla(int idTalla) {
        this.idTalla = idTalla;
    }
}
