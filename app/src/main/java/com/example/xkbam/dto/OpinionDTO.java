package com.example.xkbam.dto;

public class OpinionDTO {
    private int idOpinion;
    private String codigoArticulo;
    private String comentario;
    private int calificacion;
    private String usuario;

    // Constructor
    public OpinionDTO(String codigoArticulo, String comentario, int calificacion, String usuario) {
        this.codigoArticulo = codigoArticulo;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.usuario = usuario;
    }

    // Getters and Setters
    public int getIdOpinion() {
        return idOpinion;
    }

    public void setIdOpinion(int idOpinion) {
        this.idOpinion = idOpinion;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
