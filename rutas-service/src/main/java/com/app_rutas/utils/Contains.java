package com.app_rutas.utils;

import com.app_rutas.controller.excepcion.ResourceNotFoundException;

public class Contains {
    public static Boolean contains(Object[] array, Object value) throws Exception {
        try {
            return exist(array, value);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error al buscar el valor en el array");
        }
    }

    private static Boolean exist(Object[] array, Object value) {
        Boolean exist = false;
        for (Object obj : array) {
            System.err.println(obj.toString());
        }

        for (Object obj : array) {
            if (obj.toString().equalsIgnoreCase(value.toString())) {
                exist = true;
                break;
            }
        }
        return exist;

    }

}
