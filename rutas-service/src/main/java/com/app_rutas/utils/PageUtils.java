package com.app_rutas.utils;

import com.app_rutas.controller.tda.list.LinkedList;

public class PageUtils {

    /*
     * Metodo para paginacion de listas
     * 
     * @param lista Lista a paginar
     * 
     * @param page Pagina a mostrar
     * 
     * @param size Tamaño de la pagina
     * 
     * @return Object[] Lista paginada
     */
    public static Object[] listInPages(LinkedList<?> lista, Integer page, Integer size) {
        if (lista.isEmpty()) {
            return new Object[] {};
        }

        if (page == null || size == null) {
            return lista.toArray();
        }

        Object[] list = lista.toArray();

        int start = (page - 1) * size;
        int end = start + size;

        if (start >= list.length || page <= 0) {
            throw new IllegalArgumentException("Página fuera de rango");
        }

        if (end > list.length) {
            end = list.length;
        }

        Object[] respuesta = new Object[end - start];

        for (int i = start; i < end; i++) {
            respuesta[i - start] = list[i];
        }

        return respuesta;
    }

    public static Object[] listInPages(Object[] lista, Integer page, Integer size) {
        if (lista.length == 0) {
            return new Object[] {};
        }

        if (page == null || size == null) {
            return lista;
        }

        int start = (page - 1) * size;
        int end = start + size;

        if (start >= lista.length || page <= 0) {
            throw new IllegalArgumentException("Página fuera de rango");
        }

        if (end > lista.length) {
            end = lista.length;
        }

        Object[] respuesta = new Object[end - start];

        for (int i = start; i < end; i++) {
            respuesta[i - start] = lista[i];
        }

        return respuesta;
    }
}
