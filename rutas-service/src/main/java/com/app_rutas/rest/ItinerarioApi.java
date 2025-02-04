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
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ResourceNotFoundException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Itinerario;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.enums.EstadoEnum;
import com.app_rutas.models.enums.ItinerarioEstadoEnum;
import com.app_rutas.utils.ResponseBuilder;

@Path("/itinerario")
public class ItinerarioApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ListEmptyException, Exception {
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
        HashMap<ItinerarioEstadoEnum, String> mapEstados = new HashMap<>();
        ItinerarioServices ps = new ItinerarioServices();
        for (ItinerarioEstadoEnum estado : ps.getEstado()) {
            mapEstados.put(estado, estado.getEstado());
        }
        return new ResponseBuilder("Success", Status.OK, mapEstados).bulidResponse();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) {
        HashMap<String, Object> map = new HashMap<>();
        ItinerarioServices is = new ItinerarioServices();
        try {
            if (id == null || id < 1) {
                map.put("msg", "ID invalido");
                return Response.status(Status.BAD_REQUEST).entity(map).build();
            }
            is.setItinerario(is.get(id));
            if (is.getItinerario() == null || is.getItinerario().getId() == null) {
                return new ResponseBuilder("Error: el itinerario no existe o esta vacio", Status.BAD_REQUEST)
                        .buildBadResponse();
            }
            map.put("msg", "OK");
            map.put("data", is.showOne(id));
            return Response.ok(map).build();
        } catch (Exception e) {
            map.put("msg", "Error al obtener el itinerario");
            map.put("error", e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();

        try {
            if (!map.containsKey("idConductorVehiculo")) {
                return new ResponseBuilder("Error: El conductor asignado es obligatorio", Status.BAD_REQUEST)
                        .buildBadResponse();
            }

            ConductorVehiculoServices cas = new ConductorVehiculoServices();
            int idConductorVehiculo = Integer.parseInt(map.get("idConductorVehiculo").toString());
            cas.setConductorVehiculo(cas.get(idConductorVehiculo));

            if (cas.getConductorVehiculo().getId() == null || cas.getConductorVehiculo().getIsWorking()
                    || !cas.getConductorVehiculo().getIsActive()) {
                throw new ResourceNotFoundException("Conductor Vehiculo no encontrado o ya no esta disponible");
            }

            ItinerarioServices ps = new ItinerarioServices();
            ps.getItinerario().setIdConductorVehiculo(idConductorVehiculo);
            ps.getItinerario().setFechaGeneracion(java.time.LocalDate.now().toString());

            if (map.containsKey("fechaProgramada")) {
                ps.getItinerario().setFechaProgramada(map.get("fechaProgramada").toString());
            }

            if (map.containsKey("estado")) {
                ps.getItinerario().setEstado(ps.getEstadoEnum(map.get("estado").toString()));
            }

            ps.save();
            cas.getConductorVehiculo().setIsWorking(true);
            cas.update();

            res.put("message", "Itinerario creado con éxito");
            res.put("status", "Ok");
            res.put("idItinerario", ps.getItinerario().getId());

            return Response.status(Response.Status.OK).entity(res).build();
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            return new ResponseBuilder("Error: " + e.getMessage(), Status.BAD_REQUEST).buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR).buildBadResponse();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") Integer id) {
        ItinerarioServices is = new ItinerarioServices();

        try {

            is.eliminarItinerario(id);

            return new ResponseBuilder("Succes: Itinerario eliminado", Status.OK).buildOkResponse();
        } catch (ResourceNotFoundException e) {
            return new ResponseBuilder("Error: " + e.getMessage(), Status.NOT_FOUND).buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR).buildBadResponse();
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
            OrdenEntrega[] ordenes = ordenesEntrega.toArray();
            ArrayList<OrdenEntrega> ordenes1 = new ArrayList<>();
            for (OrdenEntrega ordenEntrega : ordenes) {
                ordenes1.add(ordenEntrega);
            }

            is.getItinerario().setDetallesEntrega(ordenes1);
            is.update();

            res.put("estado", "Ok");
            res.put("data", "Itinerario generado con éxito.");
            res.put("id", is.getItinerario().getId());
            res.put("ordenes", ordenesEntrega.toArray());

            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("finalizar/{id}")
    public Response completarItinerario(@PathParam("id") Integer id) {
        ItinerarioServices is = new ItinerarioServices();
        try {
            is.finalizarItinerario(id);
            return new ResponseBuilder("Itinerario completado", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cancelar/{id}")
    public Response cancelarItinerario(@PathParam("id") Integer id) {
        ItinerarioServices is = new ItinerarioServices();
        try {
            is.setItinerario(is.get(id));
            ArrayList<OrdenEntrega> ordenes1 = is.getItinerario().getDetallesEntrega();
            for (OrdenEntrega ordenEntrega : ordenes1) {
                System.out.println("Cancelando orden: " + ordenEntrega.getId());
                if (!ordenEntrega.getEstado().equals(EstadoEnum.ENTREGADO)) {
                    ordenEntrega.setEstado(EstadoEnum.CANCELADO);

                }
            }
            is.cancelarItinerario(id);
            is.getItinerario().setDetallesEntrega(ordenes1);
            is.update();
            return new ResponseBuilder("Itinerario cancelado", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("eliminar_orden/{id_orden}")
    public Response eliminarOrden(@PathParam("id_orden") Integer id) {
        ItinerarioServices is = new ItinerarioServices();
        try {
            is.eliminarOrden(id);
            return new ResponseBuilder("Orden eliminada", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("finalizar_orden/{id_orden}")
    public Response finalizarOrden(@PathParam("id_orden") Integer id) {
        ItinerarioServices is = new ItinerarioServices();
        try {
            is.finalizarOrden(id);
            return new ResponseBuilder("Orden finalizada", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("cancelar_orden/{id_itinerario}/{id_orden}")
    public Response modificarOrden(@PathParam("id_itinerario") Integer idItinerario,
            @PathParam("id_orden") Integer idOrden) {
        ItinerarioServices is = new ItinerarioServices();
        try {
            is.setItinerario(is.get(idItinerario));
            ArrayList<OrdenEntrega> ordenes = is.getItinerario().getDetallesEntrega();
            if (ordenes == null) {
                return new ResponseBuilder("No hay órdenes en el itinerario", Status.NOT_FOUND).buildBadResponse();
            }
            boolean encontrada = false;
            for (OrdenEntrega orden : ordenes) {
                if (orden.getId().equals(idOrden)) {
                    System.out.println("Modificando orden: " + orden.getId());
                    orden.setEstado(EstadoEnum.CANCELADO);
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                return new ResponseBuilder("Orden no encontrada en el itinerario", Status.NOT_FOUND).buildBadResponse();
            }
            is.cancelarOrden(idOrden);
            is.getItinerario().setDetallesEntrega(ordenes);
            is.update();
            return new ResponseBuilder("Orden modificada correctamente", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

}