package com.app_rutas.models.enums;

public enum ItinerarioEstadoEnum {
    PENDIENTE("PENDIENTE"),
    EN_PROGRESO("EN PROGRESO"),
    COMPLETADO("COMPLETADO"),
    CANCELADO("CANCELADO"),
    RE_PROGRAMADO("RE PROGRAMADO");

    private String estado;

    private ItinerarioEstadoEnum(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}
