package com.app_rutas.controller.excepcion;

public class ValueAlreadyExistException extends Exception {

    public ValueAlreadyExistException(String message) {
        super(message);
    }
}
