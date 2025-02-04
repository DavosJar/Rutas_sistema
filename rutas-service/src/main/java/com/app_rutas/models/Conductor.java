package com.app_rutas.models;

import com.app_rutas.models.enums.ConductorTurnoEnum;
import com.app_rutas.models.enums.EstadoConductor;

public class Conductor extends Trabajador {
    private String licenciaConducir;
    private String caducidadLicencia;
    private Double salario;
    private ConductorTurnoEnum turno;
    private EstadoConductor estado;
    private Boolean isAsigned;

    public Conductor() {
        super();
    }

    public Boolean getIsAsigned() {
        return this.isAsigned;
    }

    public void setIsAsigned(Boolean isAsigned) {
        this.isAsigned = isAsigned;
    }

    public String getLicenciaConducir() {
        return this.licenciaConducir;
    }

    public void setLicenciaConducir(String licenciaConducir) {
        this.licenciaConducir = licenciaConducir;
    }

    public String getCaducidadLicencia() {
        return this.caducidadLicencia;
    }

    public void setCaducidadLicencia(String caducidadLicencia) {
        this.caducidadLicencia = caducidadLicencia;
    }

    public Double getSalario() {
        return this.salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public ConductorTurnoEnum getTurno() {
        return this.turno;
    }

    public void setTurno(ConductorTurnoEnum turno) {
        this.turno = turno;
    }

    public EstadoConductor getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}