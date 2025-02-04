package com.app_rutas.rest;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8090/api/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     * 
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // Crear y configurar el ObjectMapper para UTF-8
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true); // Asegura que caracteres no ASCII se
                                                                              // manejen correctamente
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Evita enviar valores nulos

        // Crear una configuración de recurso que escanea recursos JAX-RS en el paquete
        // com.app_rutas.rest
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.app_rutas.rest")
                .register(JacksonFeature.class)
                .property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", true)
                .register(new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));

        rc.property("jersey.config.server.encoding", "UTF-8"); // Asegura que todo el servidor use UTF-8

        // Crear e iniciar el servidor HTTP Grizzly
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}
