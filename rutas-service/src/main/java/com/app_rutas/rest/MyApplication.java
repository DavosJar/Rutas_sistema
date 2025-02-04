package com.app_rutas.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {
    public MyApplication() {
        // Registra el paquete de tus recursos
        packages("com.app_rutas");

        // Registra JacksonFeature para la serialización/deserialización de JSON
        register(JacksonFeature.class);
    }
}