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

import com.app_rutas.controller.dao.services.OrdenEntregaServices;
import com.app_rutas.controller.dao.services.PedidoServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.OrdenEntrega;

@Path("/orden-entrega")
public class OrdenEntregaApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ListEmptyException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        OrdenEntregaServices ps = new OrdenEntregaServices();
        try {
            res.put("status", "success");
            res.put("message", "Consulta realizada con exito.");
            res.put("data", ps.listShowAll());
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Error interno del servidor: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @Path("/estado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getType() {
        HashMap<String, Object> res = new HashMap<>();
        OrdenEntregaServices ps = new OrdenEntregaServices();
        res.put("msg", "OK");
        res.put("data", ps.getEstado());
        return Response.ok(res).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) {
        HashMap<String, Object> res = new HashMap<>();
        OrdenEntregaServices ps = new OrdenEntregaServices();
        try {
            if (id == null || id < 1) {
                res.put("msg", "ID invalido");
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            }
            ps.setOrdenEntrega(ps.get(id));
            if (ps.getOrdenEntrega() == null || ps.getOrdenEntrega().getId() == null) {
                res.put("msg", "No existe generador con el ID proporcionado");
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }
            res.put("msg", "OK");
            res.put("data", ps.getOrdenEntrega());
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("msg", "Error al obtener el generador");
            res.put("error", e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        ;
        OrdenEntregaServices ps = new OrdenEntregaServices();

        try {
            if (map.get("pedido") != null) {
                PedidoServices p = new PedidoServices();
                p.setPedido(p.get(Integer.parseInt(map.get("pedido").toString())));
            }
            if (map.get("fechaProgramada") == null || map.get("fechaProgramada").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'fechaProgramada' es obligatorio.");
            }
            if (map.get("horaProgramada") == null || map.get("horaProgramada").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'horaProgramada' es obligatorio.");
            }
            if (map.get("receptor") == null || map.get("receptor").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'receptor' es obligatorio.");
            }
            if (map.get("observaciones") == null || map.get("observaciones").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'observaciones' es obligatorio.");
            }
            if (map.get("estado") == null || map.get("estado").toString().isEmpty()) {
                throw new IllegalArgumentException("El campo 'estado' es obligatorio.");
            }
            ps.getOrdenEntrega().setFechaProgramada(map.get("fechaProgramada").toString());
            ps.getOrdenEntrega().setReceptor(map.get("receptor").toString());
            ps.getOrdenEntrega().setObservaciones(map.get("observaciones").toString());
            ps.getOrdenEntrega().setEstado(ps.getEstadoEnum(map.get("estado").toString()));
            ps.getOrdenEntrega().setIdPedido(Integer.parseInt(map.get("pedido").toString()));
            ps.save();
            res.put("estado", "Ok");
            res.put("data", "Orden de entrega guardado con exito.");
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
        OrdenEntregaServices ps = new OrdenEntregaServices();
        try {
            ps.getOrdenEntrega().setId(id);
            ps.delete();
            System.out.println("Orden de entrega eliminada" + id);
            res.put("estado", "Ok");
            res.put("data", "Orden de entrega eliminado con exito.");
            return Response.ok(res).build();
        } catch (Exception e) {
            System.out.println("Hasta aqui llega" + ps.getOrdenEntrega().getId());
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
            OrdenEntregaServices ps = new OrdenEntregaServices();
            ps.setOrdenEntrega(ps.get(Integer.parseInt(map.get("id").toString())));
            if (ps.getOrdenEntrega() == null || ps.getOrdenEntrega().getId() == null) {
                res.put("status", "error");
                res.put("message", "No se ha seleccionado una orden de entrega para actualizar.");
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            }
            if (map.get("pedido") != null && !map.get("pedido").toString().isEmpty()) {
                PedidoServices p = new PedidoServices();
                p.setPedido(p.get(Integer.parseInt(map.get("pedido").toString())));
            }
            if (map.get("fechaProgramada") != null && !map.get("fechaProgramada").toString().isEmpty()) {
                ps.getOrdenEntrega().setFechaProgramada(map.get("fechaProgramada").toString());
            }

            if (map.get("receptor") != null && !map.get("receptor").toString().isEmpty()) {
                ps.getOrdenEntrega().setReceptor(map.get("receptor").toString());
            }
            if (map.get("observaciones") != null && !map.get("observaciones").toString().isEmpty()) {
                ps.getOrdenEntrega().setObservaciones(map.get("observaciones").toString());
            }
            if (map.get("estado") != null && !map.get("estado").toString().isEmpty()) {
                ps.getOrdenEntrega().setEstado(ps.getEstadoEnum(map.get("estado").toString()));
            }
            ps.update();
            res.put("status", "success");
            res.put("message", "Orden actualizada con exito.");
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Error interno del servidor: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{attribute}/{value}")
    public Response binarySearchLin(@PathParam("attribute") String attribute, @PathParam("value") String value) {
        HashMap<String, Object> map = new HashMap<>();
        OrdenEntregaServices ps = new OrdenEntregaServices();

        try {
            LinkedList<OrdenEntrega> results;
            try {
                results = ps.buscar(attribute, value);
            } catch (NumberFormatException e) {
                map.put("msg", "El valor proporcionado no es un numero valido");
                return Response.status(Status.BAD_REQUEST).entity(map).build();
            }

            if (results != null && !results.isEmpty()) {
                map.put("msg", "OK");
                map.put("data", results.toArray());
                return Response.ok(map).build();
            } else {
                map.put("msg", "No se encontraron ordenes de entrega con los valores proporcionados");
                return Response.status(Status.NOT_FOUND).entity(map).build();
            }

        } catch (Exception e) {
            map.put("msg", "Error en la busqueda");
            map.put("error", e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order/{atributo}/{orden}")
    public Response ordenar(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        PedidoServices ps = new PedidoServices();
        try {
            res.put("estado", "Ok");
            res.put("data", ps.order(atributo, orden).toArray());
            if (ps.order(atributo, orden).isEmpty()) {
                res.put("data", new Object[] {});
            }
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }
}