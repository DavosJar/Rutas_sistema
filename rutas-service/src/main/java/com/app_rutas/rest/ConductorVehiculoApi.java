package com.app_rutas.rest;

import java.util.HashMap;

import javax.ws.rs.Consumes;
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
import com.app_rutas.controller.dao.services.ConductorServices;
import com.app_rutas.controller.dao.services.ItinerarioServices;
import com.app_rutas.controller.dao.services.VehiculoServices;
import com.app_rutas.controller.excepcion.ExcesiveChargeException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.ConductorVehiculo;
import com.app_rutas.models.enums.VehiculoEstadoEnum;
import com.app_rutas.models.enums.EstadoConductor;

@Path("/conductor-vehiculo")
public class ConductorVehiculoApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ExcesiveChargeException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        ConductorVehiculoServices ps = new ConductorVehiculoServices();
        try {
            res.put("status", "success");
            res.put("message", "Consulta realizada con exito.");
            res.put("data", ps.listShowAll(ps.listAll()));
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Error interno del servidor: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) {
        HashMap<String, Object> map = new HashMap<>();
        ConductorVehiculoServices ps = new ConductorVehiculoServices();
        try {
            if (id == null || id < 1) {
                map.put("msg", "ID invalido");
                return Response.status(Status.BAD_REQUEST).entity(map).build();
            }
            ps.setConductorVehiculo(ps.get(id));
            if (ps.getConductorVehiculo() == null || ps.getConductorVehiculo().getId() == null) {
                map.put("msg", "No existe pedido con el ID proporcionado");
                return Response.status(Status.NOT_FOUND).entity(map).build();
            }
            map.put("msg", "OK");
            map.put("data", ps.showOne(id));
            return Response.ok(map).build();
        } catch (Exception e) {
            map.put("msg", "Error al obtener el pedido");
            map.put("error", e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();

        try {
            if (map.get("idVehiculo") != null && map.get("idConductor") != null) {
                VehiculoServices pes = new VehiculoServices();
                pes.setVehiculo(pes.get(Integer.parseInt(map.get("idVehiculo").toString())));
                ConductorServices cs = new ConductorServices();
                cs.setConductor(cs.getConductorById(Integer.parseInt(map.get("idConductor").toString())));

                if (pes.getVehiculo().getId() != null && cs.getConductor().getId() != null) {
                    if ((pes.getVehiculo().getIsAsigned() == false
                            && pes.getVehiculo().getEstado().equals(VehiculoEstadoEnum.DISPONIBLE))
                            && cs.getConductor().getIsAsigned() == false
                            && cs.getConductor().getEstado().equals(EstadoConductor.ACTIVO)) {
                        ConductorVehiculoServices ps = new ConductorVehiculoServices();
                        String fechaRegistro = java.time.LocalDate.now().toString();
                        ps.getConductorVehiculo().setFechaAsignacion(fechaRegistro);
                        ps.getConductorVehiculo().setIdVehiculo(pes.getVehiculo().getId());
                        ps.getConductorVehiculo().setIdConductor(cs.getConductor().getId());
                        ps.getConductorVehiculo().setIsActive(true);
                        ps.getConductorVehiculo().setIsWorking(false);
                        ps.save();
                        cs.getConductor().setIsAsigned(true);
                        pes.getVehiculo().setIsAsigned(true);
                        cs.update();
                        pes.update();
                        // System.out.println("Orden de entrega guardada" + pes);
                        res.put("estado", "Ok");
                        res.put("data", "Registro guardado con exito.");
                        return Response.ok(res).build();
                    } else {
                        res.put("estado", "error");
                        res.put("data", "El vehiculo o el conductor no estan disponibles");
                        return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
                    }
                } else {
                    res.put("estado", "error");
                    res.put("data",
                            "No existe punto de entrega o conductor asignado de entrega con el ID proporcionado");
                    return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                }
            } else {
                res.put("estado", "error");
                res.put("data", "No se proporciono un punto de entrega o una conductor asignado de entrega");
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
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
        ConductorVehiculoServices ps = new ConductorVehiculoServices();
        try {
            ps.getConductorVehiculo().setId(id);
            ps.delete();
            System.out.println("Orden de entrega eliminada" + id);
            res.put("estado", "Ok");
            res.put("data", "Registro eliminado con exito.");
            return Response.ok(res).build();
        } catch (Exception e) {
            System.out.println("Hasta aqui llega" + ps.getConductorVehiculo().getId());
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
            ConductorVehiculoServices ps = new ConductorVehiculoServices();
            ps.setConductorVehiculo(ps.get(Integer.parseInt(map.get("id").toString())));
            if (ps.getConductorVehiculo().getId() == null) {
                res.put("msg", "Error");
                res.put("data", "El equipo no existe");
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            } else {
                if (map.get("vehiculo") != null && map.get("conductor") != null) {
                    VehiculoServices pes = new VehiculoServices();
                    pes.setVehiculo(pes.get(Integer.parseInt(map.get("vehiculo").toString())));
                    ConductorServices cs = new ConductorServices();
                    cs.setConductor(cs.getConductorById(Integer.parseInt(map.get("conductor").toString())));
                    if (pes.getVehiculo().getId() != null && cs.getConductor().getId() != null) {
                        String fechaBaja = java.time.LocalDate.now().toString();
                        ps.getConductorVehiculo().setFechaAsignacion(fechaBaja);
                        ps.getConductorVehiculo().setIdVehiculo(pes.getVehiculo().getId());
                        ps.getConductorVehiculo().setIdConductor(cs.getConductor().getId());
                        ps.getConductorVehiculo().setIsWorking(false);
                        ps.getConductorVehiculo().setIsActive(false);
                        ps.update();
                        res.put("status", "success");
                        res.put("message", "Conductor asignado actualizado con exito.");
                        return Response.ok(res).build();
                    } else {
                        res.put("estado", "error");
                        res.put("data",
                                "No existe punto de entrega o conductor asignado de entrega con el ID proporcionado");
                        return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                    }
                } else {
                    res.put("estado", "error");
                    res.put("data", "No se proporciono un punto de entrega o una conductor asignado de entrega");
                    return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
                }
            }
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Ocurrió un error inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/anular")
    public Response anular(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        try {
            if (map.get("id") == null) {
                res.put("estado", "error");
                res.put("data", "ID de asignación no proporcionado");
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            }

            int idAsignacion = Integer.parseInt(map.get("id").toString());
            ConductorVehiculoServices ps = new ConductorVehiculoServices();
            ps.setConductorVehiculo(ps.get(idAsignacion));

            if (ps.getConductorVehiculo().getId() == null) {
                res.put("estado", "error");
                res.put("data", "La asignación no existe");
                return Response.status(Status.NOT_FOUND).entity(res).build();
            }
            String fechaBaja = java.time.LocalDate.now().toString();
            ps.getConductorVehiculo().setFechaDeBaja(fechaBaja);

            ps.getConductorVehiculo().setIsActive(false);
            ps.update();

            ConductorServices cs = new ConductorServices();
            cs.setConductor(cs.getConductorById(ps.getConductorVehiculo().getIdConductor()));
            cs.getConductor().setIsAsigned(false);
            cs.update();

            VehiculoServices vs = new VehiculoServices();
            vs.setVehiculo(vs.getById(ps.getConductorVehiculo().getIdVehiculo()));
            vs.getVehiculo().setIsAsigned(false);
            vs.update();

            res.put("estado", "success");
            res.put("data", "Equipo, conductor y vehículo dados de baja correctamente");
            return Response.ok(res).build();

        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error al procesar la solicitud: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{attribute}/{value}")
    public Response binarySearchLin(@PathParam("attribute") String attribute, @PathParam("value") String value) {
        HashMap<String, Object> map = new HashMap<>();
        ConductorVehiculoServices ps = new ConductorVehiculoServices();

        try {
            LinkedList<ConductorVehiculo> results;
            try {
                results = ps.buscar(attribute, value);
            } catch (NumberFormatException e) {
                map.put("msg", "El valor proporcionado no es un numero valido");
                return Response.status(Status.BAD_REQUEST).entity(map).build();
            }

            if (results != null && !results.isEmpty()) {
                map.put("msg", "OK");
                map.put("data", ps.listShowAll(results));
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
}