package com.app_rutas.utils;

public enum MsgEnum {
    OK("OK"),
    ERROR("ERROR"),
    NOT_FOUND("no encontrado"),
    NO_CONTENT("no hay contenido para mostrar"),
    CREATED("creado correctamente"),
    UPDATED("actualizado correctamente"),
    DELETED("eliminado correctamente"),
    DUPLICATED("duplicado"),
    INVALID("inválido"),
    UNAUTHORIZED("no autorizado"),
    FORBIDDEN("prohibido"),
    BAD_REQUEST("solicitud incorrecta"),
    INTERNAL_SERVER_ERROR("error interno del servidor"),
    NOT_IMPLEMENTED("no implementado"),
    SERVICE_UNAVAILABLE("servicio no disponible"),
    ALREADY_EXISTS("ya existe");

    private String estado;

    private MsgEnum(String estado) {
        this.estado = estado;
    }

    public String getMsg() {
        return this.estado;
    }
}
