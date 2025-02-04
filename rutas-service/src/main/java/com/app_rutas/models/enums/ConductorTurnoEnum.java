package com.app_rutas.models.enums;

public enum ConductorTurnoEnum {
    MATUTINO("Matutino"),
    VESPERTINO("Vespertino"),
    NOCTURNO("Nocturbno"),
    MIXTO("Mixto");

    private String estado;

    private ConductorTurnoEnum(String estado) {
        this.estado = estado;
    }

    public String getTurno() {
        return this.estado;
    }

}