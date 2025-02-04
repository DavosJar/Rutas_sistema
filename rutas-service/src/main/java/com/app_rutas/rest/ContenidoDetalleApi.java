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

import com.app_rutas.controller.dao.services.ContenidoDetalleServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.ContenidoDetalle;

@Path("/contenido-detalle")
public class ContenidoDetalleApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAllDetails() throws ListEmptyException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        // EventoCrudServices ev = new EventoCrudServices();
        try {
            LinkedList<ContenidoDetalle> lista = cds.listAll();
            res.put("status", "OK");
            res.put("msg", "Consulta exitosa.");
            res.put("data", lista.toArray());
            if (lista.isEmpty()) {
                res.put("data", new Object[] {});
            }
            // ev.registrarEvento(TipoCrud.LIST, "Se ha consultado la lista de
            // contenidoDetalles.");
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("status", "ERROR");
            res.put("msg", "Error al obtener la lista de contenidoDetalles: " + e.getMessage());
            // ev.registrarEvento(TipoCrud.LIST, "Error inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getContenidoDetalleById(@PathParam("id") Integer id) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        try {
            map.put("msg", "OK");
            map.put("data", cds.getById(id));
            if (cds.getById(id) == null) {
                map.put("msg", "ERROR");
                map.put("error", "No se encontro el contenidoDetalle con id: " + id);
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
        ContenidoDetalleServices cds = new ContenidoDetalleServices();

        try {

            if (map.get("tipo") != null) {
                cds.getContenidoDetalle().setTipo(cds.getContenidoType(map.get("tipo").toString()));
            } else {
                cds.getContenidoDetalle().setTipo(cds.getContenidoType("OTROS"));
            }
            cds.validateField("pesoUnitario", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999999");
            cds.validateField("volumenUnitario", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999999");
            if (map.get("requiereFrio") != null) {
                cds.getContenidoDetalle().setRequiereFrio(Boolean.parseBoolean(map.get("requiereFrio").toString()));
            } else {
                cds.getContenidoDetalle().setRequiereFrio(false);
            }
            cds.save();

            res.put("estado", "Ok");
            res.put("data", "Registro guardado con éxito.");
            return Response.ok(res).build();

        } catch (ValueAlreadyExistException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();

        } catch (IllegalArgumentException e) {
            res.put("estado", "error");
            res.put("data", "Error de validación: " + e.getMessage());
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
    public Response delete(@PathParam("id") Integer id) throws Exception {

        HashMap<String, Object> res = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        try {
            cds.getContenidoDetalle().setId(id);
            cds.delete();
            res.put("estado", "Ok");
            res.put("data", "Registro eliminado con exito.");

            return Response.ok(res).build();
        } catch (Exception e) {
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
        ContenidoDetalleServices cds = new ContenidoDetalleServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }
            int id = (int) map.get("id");
            cds.setContenidoDetalle(cds.getById(id));
            cds.validateField("id", map, "MIN_VALUE=1");

            if (map.get("tipo") != null && !map.get("tipo").equals(cds.getContenidoDetalle().getTipo().getEstado())) {
                cds.getContenidoDetalle().setTipo(cds.getContenidoType(map.get("tipo").toString()));
            } else {
                cds.getContenidoDetalle().setTipo(cds.getContenidoDetalle().getTipo());
            }
            if (map.get("peso") != null && !map.get("peso").equals(cds.getContenidoDetalle().getPeso())) {
                cds.validateField("peso", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999999");
            }
            if (map.get("volumen") != null
                    && !map.get("volumen").equals(cds.getContenidoDetalle().getVolumen())) {
                cds.validateField("volumen", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999999");
            }
            if (map.get("requiereFrio") != null) {
                cds.getContenidoDetalle().setRequiereFrio(Boolean.parseBoolean(map.get("requiereFrio").toString()));
            } else {
                cds.getContenidoDetalle().setRequiereFrio(cds.getContenidoDetalle().getRequiereFrio());
            }

            cds.update();
            res.put("estado", "Ok");
            res.put("data", "Registro actualizado con éxito.");
            return Response.ok(res).build();
        } catch (ValueAlreadyExistException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/ident/{identificacion}")
    public Response searchContenidoDetalle(@PathParam("identificacion") String identificacion) throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cds.obtenerContenidoDetallePor("identificacion", identificacion));
            if (cds.obtenerContenidoDetallePor("identificacion", identificacion) == null) {
                res.put("estado", "error");
                res.put("data", "No se encontro el contenidoDetalle con identificacion: " + identificacion);
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/{atributo}/{valor}")
    public Response buscarContenidoDetallees(@PathParam("atributo") String atributo, @PathParam("valor") String valor)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cds.getContenidoDetalleesBy(atributo, valor).toArray());
            if (cds.getContenidoDetalleesBy(atributo, valor).isEmpty()) {
                res.put("data", new Object[] {});
            }
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/order/{atributo}/{orden}")
    public Response ordenarContenidoDetallees(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cds.order(atributo, orden).toArray());
            if (cds.order(atributo, orden).isEmpty()) {
                res.put("data", new Object[] {});
            }
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/criterios")
    public Response getCriterios() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        ContenidoDetalleServices cds = new ContenidoDetalleServices();
        map.put("msg", "OK");
        map.put("data", cds.getContenidoDetalleAttributeLists());
        return Response.ok(map).build();
    }
}