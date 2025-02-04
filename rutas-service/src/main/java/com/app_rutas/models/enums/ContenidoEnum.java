package com.app_rutas.models.enums;

public enum ContenidoEnum {
    FRESCOS("FRESCOS"),
    CONGELADOS("CONGELADOS"),
    BEBIDAS("BEBIDAS"),
    FRUTAS("FRUTAS"),
    VERDURAS("VERDURAS"),
    CARNES("CARNES"),
    LACTEOS("LACTEOS"),
    PANADERIA("PANADERIA"),
    FRAGIL("FRAGIL"),
    LIMPIEZA("LIMPIEZA"),
    CUIDADO_PERSONAL("CUIDADO_PERSONAL"),
    OTROS("OTROS");

    private String estado;

    private ContenidoEnum(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return this.estado;
    }
}
