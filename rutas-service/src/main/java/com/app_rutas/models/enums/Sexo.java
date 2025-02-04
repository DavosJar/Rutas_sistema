package com.app_rutas.models.enums;

public enum Sexo {
    HOMBRE("Hombre"),
    MUJER("Mujer");

    private String descripcion;

    private Sexo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public static void validateSexo(String sexo) throws IllegalArgumentException {
        try {
            Sexo.valueOf(sexo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Sexo no válido: " + sexo);
        }
    }
}
