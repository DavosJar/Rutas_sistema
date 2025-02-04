package com.app_rutas.rest;

import java.util.ArrayList;
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

import com.app_rutas.controller.dao.services.ConductorVehiculoServices;
import com.app_rutas.controller.dao.services.ItinerarioServices;
import com.app_rutas.controller.excepcion.ExcesiveChargeException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Itinerario;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.enums.ItinerarioEstadoEnum;
import com.app_rutas.utils.ResponseBuilder;

@Path("/itinerario")
public class ItinerarioApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ExcesiveChargeException, Exception {
        ItinerarioServices ps = new ItinerarioServices();
        try {
            return new ResponseBuilder("success", Status.OK, ps.listShowAll()).buildListResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @Path("/listType")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getType() {
        HashMap<String, Object> map = new HashMap<>();
        ItinerarioServices ps = new ItinerarioServices();
        map.put("msg", "OK");
        map.put("data", ps.getEstado());
        return Response.ok(map).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) {
        ItinerarioServices ps = new ItinerarioServices();
        try {
            if (id == null || id < 1) {
                return new ResponseBuilder("ID no valido", Status.BAD_REQUEST).buildBadResponse();
            }
            ps.setItinerario(ps.get(id));
            if (ps.getItinerario() == null || ps.getItinerario().getId() == null) {
                return new ResponseBuilder("Itinerario no encontrado", Status.NOT_FOUND).buildBadResponse();
            }
            return new ResponseBuilder("success", Status.OK, ps.showOne(id)).bulidResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();

        try {
            if (map.get("idConductorVehiculo") != null) {
                ConductorVehiculoServices cas = new ConductorVehiculoServices();
                cas.setConductorVehiculo(cas.get(Integer.parseInt(map.get("idConductorVehiculo").toString())));
                if (cas.getConductorVehiculo().getId() != null) {
                    ItinerarioServices ps = new ItinerarioServices();

                    String fehcaGeneracion = java.time.LocalDate.now().toString();
                    ps.getItinerario().setFechaGeneracion(fehcaGeneracion);
                    ps.getItinerario().setFechaProgramada(map.get("fechaProgramada").toString());
                    ps.getItinerario().setEstado(ItinerarioEstadoEnum.PENDIENTE);
                    ps.getItinerario().setIdConductorVehiculo(cas.getConductorVehiculo().getId());
                    Thread.sleep(20);
                    ps.save();
                    res.put("Status", "Ok");
                    res.put("message", "Itinerario creado con exito.");
                    res.put("id", ps.getItinerario().getId());

                    cas.getConductorVehiculo().setIsWorking(true);
                    return Response.ok(res).build();
                } else {
                    res.put("estado", "error");
                    res.put("data", "Conductor no encontrado.");
                    return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
                }
            } else {
                res.put("estado", "error");
                res.put("data", "El campo 'conductor-asignado' es obligatorio.");
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }

        } catch (IllegalArgumentException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (InterruptedException e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
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
        ItinerarioServices ps = new ItinerarioServices();
        try {
            ps.getItinerario().setId(id);
            if (ps.getItinerario().getDetallesEntrega() != null && !ps.getItinerario().getDetallesEntrega().isEmpty()) {
                throw new IllegalAccessException("No se puede eliminar un itinerario con ordenes de entrega");
            }
            ps.delete();

            return new ResponseBuilder("Itinerario eliminado con exito", Status.OK).buildOkResponse();
        } catch (IllegalAccessException e) {
            return new ResponseBuilder("No se puede eliminar un itinerario con ordenes de entrega", Status.BAD_REQUEST)
                    .buildBadResponse();

        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            ItinerarioServices ps = new ItinerarioServices();
            ps.setItinerario(ps.get(Integer.parseInt(map.get("id").toString())));
            if (ps.getItinerario().getId() == null) {
                return new ResponseBuilder("Itinerario no encontrado", Status.NOT_FOUND).buildBadResponse();
            } else {
                if (map.get("conductor-asignado") != null) {
                    ConductorVehiculoServices cas = new ConductorVehiculoServices();
                    cas.setConductorVehiculo(cas.get(Integer.parseInt(map.get("conductor-asignado").toString())));
                    if (cas.getConductorVehiculo().getId() != null) {

                        if (map.get("fechaGeneracion") != null) {
                            ps.getItinerario().setFechaGeneracion(map.get("fechaGeneracion").toString());
                        }
                        ps.getItinerario().setEstado(ps.getEstadoEnum(map.get("estado").toString()));
                        ps.getItinerario().setIdConductorVehiculo(cas.getConductorVehiculo().getId());
                        ps.update();
                        res.put("status", "success");
                        res.put("message", "Itinerario actualizado con exito.");
                        return Response.ok(res).build();
                    } else {
                        res.put("status", "error");
                        res.put("message", "Conductor no encontrado.");
                        return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                    }
                } else {
                    res.put("estado", "error");
                    res.put("data", "No se proporciono un itinerario");
                    return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
                }
            }
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{attribute}/{value}")
    public Response binarySearchLin(@PathParam("attribute") String attribute, @PathParam("value") String value) {
        HashMap<String, Object> map = new HashMap<>();
        ItinerarioServices ps = new ItinerarioServices();

        try {
            LinkedList<Itinerario> results;
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
                map.put("msg", "No se encontraron itinerarios");
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
        ItinerarioServices ps = new ItinerarioServices();
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/generar")
    public Response generarItinerario(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        ItinerarioServices is = new ItinerarioServices();
        try {
            Integer id = Integer.parseInt(map.get("id").toString());

            is.setItinerario(is.getById(id));

            LinkedList<OrdenEntrega> ordenesEntrega = is.generarOrdenList(id);

            if (ordenesEntrega.isEmpty()) {
                return new ResponseBuilder("pedidos compatibles no encontrados", Status.NOT_FOUND).buildBadResponse();
            }

            OrdenEntrega[] ordenes = ordenesEntrega.toArray();
            ArrayList<OrdenEntrega> ordenes1 = new ArrayList<>();
            for (OrdenEntrega ordenEntrega : ordenes) {
                ordenes1.add(ordenEntrega);
            }

            is.getItinerario().setDetallesEntrega(ordenes1);
            is.update();

            return new ResponseBuilder("Itinerario generado con exito", Status.OK).buildOkResponse();

        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

}