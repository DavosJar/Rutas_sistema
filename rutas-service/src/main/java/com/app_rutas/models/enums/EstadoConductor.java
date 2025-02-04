package com.app_rutas.models.enums;

public enum EstadoConductor {
    ACTIVO("ACTIVO"),
    SUSPENDIDO("SUSPENDIDO"),
    BAJA("BAJA");

    private String estado;

    private EstadoConductor(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return this.estado;
    }
}