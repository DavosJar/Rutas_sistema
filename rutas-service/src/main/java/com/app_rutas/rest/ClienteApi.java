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

import com.app_rutas.controller.dao.services.ClienteServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;

@Path("/cliente")
public class ClienteApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAllProyeccs() throws ListEmptyException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        ClienteServices cs = new ClienteServices();
        // EventoCrudServices ev = new EventoCrudServices();
        try {
            res.put("status", "OK");
            res.put("msg", "Consulta exitosa.");
            res.put("data", cs.listAll().toArray());
            if (cs.listAll().isEmpty()) {
                res.put("data", new Object[] {});
            }
            // ev.registrarEvento(TipoCrud.LIST, "Se ha consultado la lista de
            // conductors.");
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("status", "ERROR");
            res.put("msg", "Error al obtener la lista de conductors: " + e.getMessage());
            // ev.registrarEvento(TipoCrud.LIST, "Error inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getClienteById(@PathParam("id") Integer id) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        ClienteServices cs = new ClienteServices();
        try {
            map.put("msg", "OK");
            map.put("data", cs.getById(id));
            if (cs.getById(id) == null) {
                map.put("msg", "ERROR");
                map.put("error", "No se encontro el conductor con id: " + id);
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
        ClienteServices cs = new ClienteServices();

        try {

            cs.validateField("razonSocial", map, "NOT_NULL", "ALPHANUMERIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            cs.validateField("ruc", map, "NOT_NULL", "IS_UNIQUE", "NUMERIC", "LENGTH=13");
            cs.validateField("telefono", map, "NOT_NULL", "NUMERIC", "LENGTH=10");
            cs.validateField("email", map, "NOT_NULL", "VALID_EMAIL", "IS_UNIQUE");
            cs.save();

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
        ClienteServices cs = new ClienteServices();
        try {
            cs.getCliente().setId(id);
            cs.delete();
            res.put("estado", "Ok");
            res.put("data", "Cliente eliminado con exito.");

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
        ClienteServices cs = new ClienteServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }
            int id = (int) map.get("id");
            cs.setCliente(cs.getById(id));
            cs.validateField("id", map, "MIN_VALUE=1");
            if (map.get("razonSocial") != null && !map.get("razonSocial").equals(cs.getCliente().getRazonSocial())) {
                cs.validateField("razonSocial", map, "NOT_NULL", "ALPHANUMERIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            }

            if (map.get("ruc") != null) {
                String newRuc = map.get("ruc").toString();
                String currentRuc = cs.getCliente().getRuc();

                if (newRuc.equalsIgnoreCase(currentRuc)) {
                    cs.getCliente().setRuc(currentRuc);
                } else {
                    cs.validateField("ruc", map, "IS_UNIQUE", "NUMERIC", "LENGTH=13");
                }
            }
            if (map.get("telefono") != null && !map.get("telefono").equals(cs.getCliente().getTelefono())) {
                cs.validateField("telefono", map, "NOT_NULL", "NUMERIC", "LENGTH=10");
            }
            if (map.get("email") != null) {
                String newEmail = map.get("email").toString();
                String currentEmail = cs.getCliente().getEmail();
                if (newEmail.equalsIgnoreCase(currentEmail)) {
                    cs.getCliente().setEmail(currentEmail);
                } else {
                    cs.validateField("email", map, "NOT_NULL", "VALID_EMAIL", "IS_UNIQUE");
                }
            }

            cs.update();
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
    @Path("/list/search/{atributo}/{valor}")
    public Response buscarClientes(@PathParam("atributo") String atributo, @PathParam("valor") String valor)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ClienteServices cs = new ClienteServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cs.getClientesBy(atributo, valor).toArray());
            if (cs.getClientesBy(atributo, valor).isEmpty()) {
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
    public Response ordenarClientes(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ClienteServices cs = new ClienteServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cs.order(atributo, orden).toArray());
            if (cs.order(atributo, orden).isEmpty()) {
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