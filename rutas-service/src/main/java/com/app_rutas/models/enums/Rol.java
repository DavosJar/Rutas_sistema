package com.app_rutas.models.enums;

public enum Rol {
    ADMINISTRADOR("ADMINISTRADOR"),
    CONDUCTOR("CONDUCTOR"),
    GESTOR_PEDIDOS("GESTOR PEDIDOS"),
    OPERADOR_FLOTA("OPERADOR FLOTA");

    private String rol;

    private Rol(String rol) {
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }
}
