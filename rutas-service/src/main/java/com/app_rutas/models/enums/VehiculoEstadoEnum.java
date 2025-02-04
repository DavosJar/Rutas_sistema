package com.app_rutas.models.enums;

public enum VehiculoEstadoEnum {
    DISPONIBLE("DISPONIBLE"),
    DESCOMPUESTO("DESCOMPUESTO"),
    EN_REPARACION("EN REPARACION"),
    EN_MANTENIMIENTO("EN MANTENIMIENTO"),
    EN_REVISION("EN REVISION"),
    EN_DESUSO("EN DESUSO");

    private String estado;

    private VehiculoEstadoEnum(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }
}