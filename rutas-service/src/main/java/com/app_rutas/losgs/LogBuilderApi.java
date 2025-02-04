package com.app_rutas.losgs;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.app_rutas.controller.excepcion.ListEmptyException;

import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.JwtUtil;
import com.app_rutas.models.enums.Rol;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.app_rutas.utils.ResponseBuilder;
import com.app_rutas.utils.StringFormat;

@Path("/historial")
public class LogBuilderApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAllProyects(@HeaderParam("Authorization") String authHeader) {
        LogBuilderServices ts = new LogBuilderServices();

        // Log para verificar el encabezado recibido
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Acceso no autorizado. Encabezado ausente o formato incorrecto.");
            return new ResponseBuilder("Acceso no autorizado. Encabezado ausente o formato incorrecto.",
                    Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT jwt = JwtUtil.validateToken(token);
            System.out.println("Token válido. Usuario: " + jwt.getSubject());
            Rol rol = JwtUtil.getRolFromToken(jwt);
            System.out.println("Rol del usuario: " + rol);
            System.out.println("Llamando a LogBuilderServices.listAll...");
            if (rol != Rol.ADMINISTRADOR) {
                return new ResponseBuilder("Acceso no autorizado. Rol no permitido.", Status.UNAUTHORIZED)
                        .buildBadResponse();
            }
            LinkedList<LogBuilder> lista = ts.listAll();
            return new ResponseBuilder("Success", Status.OK, lista.toArray()).buildOkResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o expirado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getLogBuilderById(@PathParam("id") Integer id) throws Exception {
        LogBuilderServices ts = new LogBuilderServices();
        try {
            if (ts.getById(id) == null) {
                return new ResponseBuilder("No se encontro el logBuilder con id: " + id, Status.NOT_FOUND)
                        .buildBadResponse();
            }
            return new ResponseBuilder("Success", Status.OK, ts.getById(id)).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/{atributo}/{valor}")
    public Response buscarLogBuilderes(@PathParam("atributo") String atributo, @PathParam("valor") String valor)
            throws Exception {
        LogBuilderServices ts = new LogBuilderServices();
        try {

            if (ts.getLogBuilderesBy(atributo, valor).isEmpty()) {
                return new ResponseBuilder("No se encontraron trabajadores", Status.NOT_FOUND).buildBadResponse();
            }
            return new ResponseBuilder("OK", Status.OK, ts.getLogBuilderesBy(atributo, valor).toArray())
                    .buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/order/{atributo}/{orden}")
    public Response ordenarLogBuilderes(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        LogBuilderServices ts = new LogBuilderServices();
        try {

            if (ts.order(atributo, orden).isEmpty()) {
                return new ResponseBuilder("No se encontraron trabajadores", Status.NOT_FOUND).buildBadResponse();
            }
            return new ResponseBuilder("Success", Status.OK, ts.order(atributo, orden).toArray()).buildListResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tipo_operacion")
    public Response geTipos() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        LogBuilderServices ts = new LogBuilderServices();
        for (LogType t : ts.getTipos()) {
            map.put(t.toString(), t.getType());
        }
        return new ResponseBuilder("Success", Status.OK, map).buildOkResponse();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/criterios")
    public Response getCriterios() throws ListEmptyException, Exception {
        LogBuilderServices ts = new LogBuilderServices();
        HashMap<String, Object> criterios = new HashMap<>();
        for (String s : ts.getLogBuilderAttributeLists()) {
            criterios.put(s, StringFormat.camellCaseToNatural(s));
        }
        return new ResponseBuilder("Success", Status.OK, criterios).buildOkResponse();
    }
}