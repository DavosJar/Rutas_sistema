package com.app_rutas.models;

import com.app_rutas.models.enums.ContenidoEnum;

public class Pedido {
    private Integer id;
    private String fechaRegistro;
    private String codigoUnico;
    private Boolean requiereFrio;
    private ContenidoEnum contenido;
    private Double volumenTotal;
    private Double pesoTotal;
    private Integer idPuntoEntrega;
    private Integer idCliente;
    private String detalleContenido;
    private Boolean isAttended;

    public Boolean getIsAttended() {
        return isAttended;
    }

    public void setIsAttended(Boolean isAttended) {
        this.isAttended = isAttended;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getRequiereFrio() {
        return requiereFrio;
    }

    public void setRequiereFrio(Boolean requiereFrio) {
        this.requiereFrio = requiereFrio;
    }

    public Double getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(Double volumenTotal) {
        this.volumenTotal = volumenTotal;
    }

    public Double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public Integer getIdPuntoEntrega() {
        return idPuntoEntrega;
    }

    public void setIdPuntoEntrega(Integer idPuntoEntrega) {
        this.idPuntoEntrega = idPuntoEntrega;
    }

    public String getDetalleContenido() {
        return detalleContenido;
    }

    public void setDetalleContenido(String detalleContenido) {
        this.detalleContenido = detalleContenido;
    }

    public ContenidoEnum getContenido() {
        return contenido;
    }

    public void setContenido(ContenidoEnum contenido) {
        this.contenido = contenido;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
}
