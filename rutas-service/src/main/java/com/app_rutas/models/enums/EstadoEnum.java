package com.app_rutas.models.enums;

public enum EstadoEnum {
    PENDIENTE("PENDIENTE"),
    EN_PROGRESO("EN_PROGRESO"),
    ENTREGADO("ENTREGADO"),
    CANCELADO("CANCELADO");

    private String estado;

    private EstadoEnum(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return this.estado;
    }
}
