package com.app_rutas.models;

import java.util.ArrayList;

import com.app_rutas.models.enums.ItinerarioEstadoEnum;

public class Itinerario {
    private Integer id;
    private Integer idConductorVehiculo;
    private String fechaGeneracion;
    private ArrayList<OrdenEntrega> detallesEntregas;
    private String fechaProgramada;
    private ItinerarioEstadoEnum estado;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdConductorVehiculo() {
        return this.idConductorVehiculo;
    }

    public void setIdConductorVehiculo(Integer idConductorAsignado) {
        this.idConductorVehiculo = idConductorAsignado;
    }

    public String getFechaGeneracion() {
        return this.fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public ArrayList<OrdenEntrega> getDetallesEntrega() {
        return this.detallesEntregas;
    }

    public void setDetallesEntrega(ArrayList<OrdenEntrega> detallesEntrega) {
        this.detallesEntregas = detallesEntrega;
    }

    public ItinerarioEstadoEnum getEstado() {
        return this.estado;
    }

    public void setEstado(ItinerarioEstadoEnum estado) {
        this.estado = estado;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(String fechaProgranada) {
        this.fechaProgramada = fechaProgranada;
    }
}