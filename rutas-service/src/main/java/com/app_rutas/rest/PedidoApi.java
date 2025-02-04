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

import com.app_rutas.controller.dao.services.PedidoServices;
import com.app_rutas.controller.dao.services.PuntoEntregaServices;
import com.app_rutas.controller.excepcion.ExcesiveChargeException;

@Path("/pedido")
public class PedidoApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAll() throws ExcesiveChargeException, Exception {
        HashMap<String, Object> res = new HashMap<>();
        PedidoServices ps = new PedidoServices();
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

    @Path("/listType")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getType() {
        HashMap<String, Object> map = new HashMap<>();
        PedidoServices ps = new PedidoServices();
        map.put("msg", "OK");
        map.put("data", ps.getContenidos());
        return Response.ok(map).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) {
        HashMap<String, Object> map = new HashMap<>();
        PedidoServices ps = new PedidoServices();
        try {
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
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();

        try {
            if (map.get("idPuntoEntrega") != null && map.get("idCliente") != null) {
                PuntoEntregaServices pes = new PuntoEntregaServices();
                ClienteServices cs = new ClienteServices();
                int idPuntoEntrega = Integer.parseInt(map.get("idPuntoEntrega").toString());
                if (pes.getById(idPuntoEntrega) == null) {
                    res.put("estado", "error");
                    res.put("data", "No existe punto de entrega con el ID proporcionado");
                    return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                }
                int idCliente = Integer.parseInt(map.get("idCliente").toString());
                if (cs.getById(idCliente) == null) {
                    res.put("estado", "error");
                    res.put("data", "No existe cliente con el ID proporcionado");
                    return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                }
                PedidoServices ps = new PedidoServices();
                String fechaRegistro = java.time.LocalDate.now().toString();
                ps.getPedido().setFechaRegistro(fechaRegistro);
                ps.getPedido().setCodigoUnico(ps.generarCodigoUnico(idCliente));
                ps.validateField("pesoTotal", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999");
                ps.validateField("volumenTotal", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999");
                ps.getPedido().setRequiereFrio(map.get("requiereFrio") != null
                        && Boolean.parseBoolean(map.get("requiereFrio").toString()));
                if (map.get("contenido") != null) {
                    ps.getPedido().setContenido(ps.getContenido(map.get("contenido").toString()));
                } else {
                    ps.getPedido().setContenido(ps.getContenido("OTROS"));
                }
                ps.getPedido().setIdPuntoEntrega(idPuntoEntrega);
                ps.getPedido().setIdCliente(idCliente);
                ps.getPedido().setIsAttended(false);
                ps.save();

                res.put("estado", "Ok");
                res.put("data", "Pedido guardado con éxito.");
                return Response.ok(res).build();

            } else {
                res.put("estado", "error");
                res.put("data", "No se proporcionaron los IDs de cliente y punto de entrega");
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
        PedidoServices ps = new PedidoServices();
        try {
            ps.getPedido().setId(id);
            ps.delete();
            System.out.println("Orden de entrega eliminada" + id);
            res.put("estado", "Ok");
            res.put("data", "Registro eliminado con exito.");
            return Response.ok(res).build();
        } catch (Exception e) {
            System.out.println("Hasta aqui llega" + ps.getPedido().getId());
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
            PedidoServices ps = new PedidoServices();
            ps.setPedido(ps.getById(Integer.parseInt(map.get("id").toString())));
            if (ps.getPedido().getId() == null) {
                res.put("msg", "Error");
                res.put("data", "El pedido no existe");
                return Response.status(Status.BAD_REQUEST).entity(res).build();
            } else {
                if (map.get("idPuntoEntrega") != null && map.get("idCliente") != null) {
                    PuntoEntregaServices pes = new PuntoEntregaServices();
                    ClienteServices cs = new ClienteServices();
                    pes.setPuntoEntrega(pes.get(Integer.parseInt(map.get("idPuntoEntrega").toString())));
                    cs.setCliente(cs.get(Integer.parseInt(map.get("idCliente").toString())));
                    if (pes.getPuntoEntrega().getId() != null && cs.getCliente().getId() != null) {
                        ps.validateField("pesoTotal", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999");
                        ps.validateField("volumenTotal", map, "NOT_NULL", "MIN_VALUE=0", "MAX_VALUE=99999");
                        ps.getPedido().setRequiereFrio(Boolean.parseBoolean(map.get("requiereFrio").toString()));
                        ps.getPedido().setIdPuntoEntrega(pes.getPuntoEntrega().getId());
                        ps.getPedido().setIdCliente(cs.getCliente().getId());
                        ps.getPedido().setContenido(ps.getContenido(map.get("contenido").toString()));
                        ps.update();
                        res.put("status", "success");
                        res.put("message", "Orden actualizada con exito.");
                        return Response.ok(res).build();
                    } else {
                        res.put("estado", "error");
                        res.put("data", "No existe punto de entrega o orden de entrega con el ID proporcionado");
                        return Response.status(Response.Status.NOT_FOUND).entity(res).build();
                    }
                } else {
                    res.put("estado", "error");
                    res.put("data", "No se proporciono un punto de entrega o una orden de entrega");
                    return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
                }
            }
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Ocurrió un error inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/codigo/{codigo}")
    public Response searchPedido(@PathParam("codigo") String codigo) throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        PedidoServices ps = new PedidoServices();
        try {
            res.put("estado", "Ok");
            res.put("data", ps.showOne(ps.buscarPor("codigoUnico", codigo).getId()));
            if (ps.buscarPor("codigoUnico", codigo) == null) {
                res.put("estado", "error");
                res.put("data", "No se encontro el trabajador con codigo Unico: " + codigo);
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
    public Response buscarTrabajadores(@PathParam("atributo") String atributo, @PathParam("valor") String valor)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        PedidoServices ps = new PedidoServices();
        try {
            res.put("estado", "Ok");
            res.put("data", ps.buscar(atributo, valor).toArray());
            if (ps.buscar(atributo, valor).isEmpty()) {
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
