package com.app_rutas.models.enums;

public enum TipoIdentificacion {
    CI("Cedula de Identidad"),
    DNI("Documento Nacional de Identidad"),
    PASAPORTE("Pasaporte"),
    CARNET_EXTRANJERO("Carnet de Extranjería");

    private String descripcion;

    private TipoIdentificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public static void validateTipoIdentificacion(String tipo) throws Exception {
        try {
            TipoIdentificacion.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Tipo de identificación no válido: " + tipo);
        }

    }
}
