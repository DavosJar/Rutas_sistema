package com.app_rutas.models;

import com.app_rutas.models.enums.ContenidoEnum;

public class ContenidoDetalle {
    private Integer id;
    private ContenidoEnum tipo;
    private Double peso;
    private Double volumen;
    private Boolean requiereFrio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public Boolean getRequiereFrio() {
        return requiereFrio;
    }

    public void setRequiereFrio(Boolean requiereFrio) {
        this.requiereFrio = requiereFrio;
    }

    public ContenidoEnum getTipo() {
        return tipo;
    }

    public void setTipo(ContenidoEnum tipo) {
        this.tipo = tipo;
    }

}