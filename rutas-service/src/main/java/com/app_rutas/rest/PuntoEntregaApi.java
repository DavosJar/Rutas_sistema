package com.app_rutas.rest;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.app_rutas.controller.dao.services.PuntoEntregaServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.PuntoEntrega;
import com.app_rutas.utils.PageUtils;
import com.app_rutas.utils.ResponseBuilder;

@Path("/punto-entrega")
public class PuntoEntregaApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{page}")
    public Response getAllProyects(@PathParam("page") Integer page) throws ListEmptyException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        PuntoEntregaServices pes = new PuntoEntregaServices();
        try {
            Object[] lista = pes.listShowAll();

            return new ResponseBuilder("Success", Status.OK, PageUtils.listInPages(lista, page, 20))
                    .buildListResponse();
        } catch (Exception e) {
            res.put("status", "ERROR");
            res.put("msg", "Error al obtener la lista de puntoEntregas: " + e.getMessage());
            // ev.registrarEvento(TipoCrud.LIST, "Error inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getPuntoEntregaById(@PathParam("id") Integer id) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        PuntoEntregaServices pes = new PuntoEntregaServices();
        try {
            map.put("msg", "OK");
            map.put("data", pes.showOne(id));
            if (pes.showOne(id) == null) {
                map.put("msg", "ERROR");
                map.put("error", "No se encontro el puntoEntrega con id: " + id);
                return Response.status(Status.NOT_FOUND).entity(map).build();
            }
            return Response.ok(map).build();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "ERROR");
            map.put("error", "Error inesperado: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        PuntoEntregaServices pes = new PuntoEntregaServices();

        try {

            if (map.get("num") == null || map.get("num").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'num' no puede estar vacío.");
            }
            if (map.get("callePrincipal") == null || map.get("callePrincipal").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'callePrincipal' no puede estar vacío.");
            }
            if (map.get("calleSecundaria") == null || map.get("calleSecundaria").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'calleSecundaria' no puede estar vacío.");
            }
            if (map.get("referencia") == null || map.get("referencia").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'referencia' no puede estar vacío.");
            }
            pes.getPuntoEntrega().setNum(map.get("num").toString());
            pes.getPuntoEntrega().setCallePrincipal(map.get("callePrincipal").toString());
            pes.getPuntoEntrega().setCalleSecundaria(map.get("calleSecundaria").toString());
            pes.getPuntoEntrega().setReferencia(map.get("referencia").toString());

            pes.save();
            res.put("estado", "Ok");
            res.put("data", "Registro guardado con éxito.");
            return Response.ok(res).build();

        } catch (IllegalArgumentException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") Integer id) {
        HashMap<String, Object> res = new HashMap<>();
        PuntoEntregaServices pes = new PuntoEntregaServices();
        try {
            pes.getPuntoEntrega().setId(id);
            pes.delete();
            System.out.println("Orden de entrega eliminada" + id);
            res.put("estado", "Ok");
            res.put("data", "Registro eliminado con exito.");
            return Response.ok(res).build();
        } catch (Exception e) {
            System.out.println("Hasta aqui llega" + pes.getPuntoEntrega().getId());
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            PuntoEntregaServices pes = new PuntoEntregaServices();
            pes.setPuntoEntrega(pes.get(Integer.parseInt(map.get("id").toString())));
            if (pes.getPuntoEntrega() == null || pes.getPuntoEntrega().getId() == null) {
                res.put("status", "error");
                res.put("message", "No existe punto de entrega con el ID proporcionado");
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }
            if (map.get("num") == null || map.get("num").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'num' no puede estar vacío.");
            }
            if (map.get("callePrincipal") == null || map.get("callePrincipal").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'callePrincipal' no puede estar vacío.");
            }
            if (map.get("calleSecundaria") == null || map.get("calleSecundaria").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'calleSecundaria' no puede estar vacío.");
            }
            if (map.get("referencia") == null || map.get("referencia").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'referencia' no puede estar vacío.");
            }
            pes.getPuntoEntrega().setNum(map.get("num").toString());
            pes.getPuntoEntrega().setCallePrincipal(map.get("callePrincipal").toString());
            pes.getPuntoEntrega().setCalleSecundaria(map.get("calleSecundaria").toString());
            pes.getPuntoEntrega().setReferencia(map.get("referencia").toString());

            pes.update();
            res.put("status", "success");
            res.put("message", "Punto de entrega actualizado con éxito.");
            return Response.ok(res).build();

        } catch (IllegalArgumentException e) {
            res.put("status", "error");
            res.put("message", e.getMessage());
            return Response.status(Status.BAD_REQUEST).entity(res).build();
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Error interno del servidor: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{attribute}/{value}/{page}")
    public Response binarySearchLin(@PathParam("attribute") String attribute, @PathParam("value") String value,
            @PathParam("page") Integer page) {
        PuntoEntregaServices pes = new PuntoEntregaServices();

        try {
            LinkedList<PuntoEntrega> results;
            try {
                results = pes.buscar(attribute, value);
            } catch (NumberFormatException e) {
                return new ResponseBuilder("Valor no válido", Status.BAD_REQUEST).buildBadResponse();
            }

            if (results != null && !results.isEmpty()) {
                return new ResponseBuilder("Success", Status.OK, PageUtils.listInPages(results, page, 20))
                        .buildListResponse();
            } else {
                return new ResponseBuilder("No se encontraron resultados", Status.NOT_FOUND).buildBadResponse();
            }

        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor", Status.INTERNAL_SERVER_ERROR).buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order/{atributo}/{orden}/{page}")
    public Response ordenar(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden,
            @PathParam("page") Integer page)
            throws Exception {
        PuntoEntregaServices pes = new PuntoEntregaServices();
        try {
            LinkedList<PuntoEntrega> lista = pes.order(atributo, orden);
            if (lista != null && !lista.isEmpty()) {
                return new ResponseBuilder("Success", Status.OK, PageUtils.listInPages(lista, page, 20))
                        .buildListResponse();
            }
            return new ResponseBuilder("No se encontraron resultados", Status.NOT_FOUND).buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor", Status.INTERNAL_SERVER_ERROR).buildBadResponse();
        }
    }
}