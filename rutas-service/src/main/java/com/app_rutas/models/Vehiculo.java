package com.app_rutas.models;

import com.app_rutas.models.enums.VehiculoEstadoEnum;

public class Vehiculo {
    private Integer id;
    private String marca;
    private String modelo;
    private String placa;
    private Double capacidad;
    private Integer potencia;
    private Double pesoTara;
    private Double pesoMaximo;
    private Boolean refrigerado;
    private VehiculoEstadoEnum estado;
    private Boolean isAsigned;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMarca() {
        return this.marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return this.modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return this.placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Double getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(Double capacidad) {
        this.capacidad = capacidad;
    }

    public Integer getPotencia() {
        return this.potencia;
    }

    public void setPotencia(Integer potencia) {
        this.potencia = potencia;
    }

    public Double getPesoTara() {
        return this.pesoTara;
    }

    public void setPesoTara(Double pesoTara) {
        this.pesoTara = pesoTara;
    }

    public Double getPesoMaximo() {
        return this.pesoMaximo;
    }

    public void setPesoMaximo(Double pesoMaximo) {
        this.pesoMaximo = pesoMaximo;
    }

    public Boolean getRefrigerado() {
        return this.refrigerado;
    }

    public void setRefrigerado(Boolean refrigerado) {
        this.refrigerado = refrigerado;
    }

    public VehiculoEstadoEnum getEstado() {
        return this.estado;
    }

    public void setEstado(VehiculoEstadoEnum estado) {
        this.estado = estado;
    }

    public Boolean getIsAsigned() {
        return isAsigned;
    }

    public void setIsAsigned(Boolean isAsigned) {
        this.isAsigned = isAsigned;
    }

}