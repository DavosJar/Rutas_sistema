package com.app_rutas.rest;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.app_rutas.controller.dao.services.VehiculoServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.JwtUtil;
import com.app_rutas.models.Vehiculo;
import com.app_rutas.models.enums.Rol;
import com.app_rutas.models.enums.VehiculoEstadoEnum;
import com.app_rutas.utils.PageUtils;
import com.app_rutas.utils.ResponseBuilder;
import com.app_rutas.utils.StringFormat;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Path("/vehiculo")
public class VehiculoApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{page}")
    public Response getAllProyects(@HeaderParam("Authorization") String authHeader, @PathParam("page") Integer page)
            throws ListEmptyException, Exception {
        VehiculoServices ts = new VehiculoServices();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR, Rol.OPERADOR_FLOTA);
            LinkedList<Vehiculo> lista = ts.listAll();
            return new ResponseBuilder("Success", Status.OK, PageUtils.listInPages(lista, page, 20)).bulidResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o expirado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getVehiculoById(@PathParam("id") Integer id) throws Exception {
        VehiculoServices ts = new VehiculoServices();
        try {
            if (ts.getById(id) == null) {
                return new ResponseBuilder("Vehiculo con id " + id + ": ", Status.NOT_FOUND)
                        .buildBadResponse();
            }
            return new ResponseBuilder("Success", Status.OK, ts.getById(id)).bulidResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        VehiculoServices ts = new VehiculoServices();

        try {

            ts.validateField("marca", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            ts.validateField("modelo", map, "NOT_NULL", "MAX_LENGTH=25", "MIN_LENGTH=1");
            ts.validateField("placa", map, "NOT_NULL", "IS_UNIQUE", "ALPHANUMERIC", "MIN_LENGTH=6", "MAX_LENGTH=8");
            ts.validateField("capacidad", map, "NOT_NULL", "MIN_VALUE=0");
            ts.validateField("potencia", map, "NOT_NULL", "MIN_VALUE=0");
            ts.validateField("pesoTara", map, "NOT_NULL", "MIN_VALUE=0");
            ts.validateField("pesoMaximo", map, "NOT_NULL", "MIN_VALUE=0");
            ts.getVehiculo().setRefrigerado(Boolean.parseBoolean(map.get("refrigerado").toString()));
            if (map.get("estado") != null) {
                ts.getVehiculo().setEstado(ts.getEstado(map.get("estado").toString()));
            }
            ts.save();

            return new ResponseBuilder("Registro guardado con éxito", Status.OK).buildOkResponse();

        } catch (ValueAlreadyExistException e) {
            return new ResponseBuilder("Error", Status.BAD_REQUEST, e.getMessage()).buildBadResponse();

        } catch (IllegalArgumentException e) {
            return new ResponseBuilder("Error", Status.BAD_REQUEST, e.getMessage()).buildBadResponse();

        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") Integer id) throws Exception {

        HashMap<String, Object> res = new HashMap<>();
        VehiculoServices ts = new VehiculoServices();
        try {
            ts.getVehiculo().setId(id);
            ts.delete();
            return new ResponseBuilder("Registro eliminado con éxito", Status.OK).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        VehiculoServices ts = new VehiculoServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }
            int id = (int) map.get("id");
            ts.setVehiculo(ts.getById(id));

            ts.validateField("id", map, "MIN_VALUE=1");
            if (map.get("marca") != null && !map.get("marca").equals(ts.getVehiculo().getMarca())) {
                ts.validateField("marca", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            }
            if (map.get("modelo") != null && !map.get("modelo").equals(ts.getVehiculo().getModelo())) {
                ts.validateField("modelo", map, "NOT_NULL", "MIN_LENGTH=1", "MAX_LENGTH=25");
            }

            if (map.get("placa") != null) {
                String newPlaca = map.get("placa").toString();
                String currentPlaca = ts.getVehiculo().getPlaca();
                if (!newPlaca.equalsIgnoreCase(currentPlaca)) {
                    ts.validateField("placa", map, "NOT_NULL", "IS_UNIQUE", "ALPHANUMERIC", "MIN_LENGTH=6",
                            "MAX_LENGTH=8");
                }
            }
            if (map.get("capacidad") != null
                    && !map.get("capacidad").equals(ts.getVehiculo().getCapacidad())) {
                ts.validateField("capacidad", map, "NOT_NULL", "MIN_VALUE=0");
            }
            if (map.get("potencia") != null
                    && !map.get("potencia").equals(ts.getVehiculo().getPotencia())) {
                ts.validateField("potencia", map, "NOT_NULL", "MIN_VALUE=0");
            }
            if (map.get("pesoTara") != null
                    && !map.get("pesoTara").equals(ts.getVehiculo().getPesoTara())) {
                ts.validateField("pesoTara", map, "NOT_NULL", "MIN_VALUE=0");
            }
            if (map.get("pesoMaximo") != null
                    && !map.get("pesoMaximo").equals(ts.getVehiculo().getPesoMaximo())) {
                ts.validateField("pesoMaximo", map, "NOT_NULL", "MIN_VALUE=0");
            }
            if (map.get("refrigerado") != null && !map.get("refrigerado").equals(ts.getVehiculo().getRefrigerado())) {
                ts.getVehiculo().setRefrigerado(Boolean.parseBoolean(map.get("refrigerado").toString()));
            }
            if (map.get("estado") != null && !map.get("estado").equals(ts.getVehiculo().getEstado().toString())) {
                ts.getVehiculo().setEstado(ts.getEstado(map.get("estado").toString()));
            }
            ts.update();
            return new ResponseBuilder("Registro actualizado con éxito", Status.OK).buildOkResponse();
        } catch (ValueAlreadyExistException e) {
            return new ResponseBuilder("Error", Status.BAD_REQUEST, e.getMessage()).buildBadResponse();
        } catch (IllegalArgumentException e) {
            return new ResponseBuilder("Error", Status.BAD_REQUEST, e.getMessage()).buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/placa/{placa}")
    public Response searchVehiculo(@PathParam("placa") String placa) throws Exception {
        VehiculoServices ts = new VehiculoServices();
        try {
            if (ts.buscarPor("placa", placa) == null) {

                return new ResponseBuilder("No se encontro el vehiculo con placa: " + placa, Status.NOT_FOUND)
                        .buildBadResponse();
            }
            return new ResponseBuilder("Success", Status.OK, ts.buscarPor("placa", placa)).bulidResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/{atributo}/{valor}/{page}")
    public Response buscarConductores(@PathParam("atributo") String atributo, @PathParam("valor") String valor,
            @PathParam("page") Integer page)
            throws Exception {
        VehiculoServices ts = new VehiculoServices();
        try {
            LinkedList<Vehiculo> list = ts.buscar(atributo, valor);
            if (list.isEmpty()) {
                return new ResponseBuilder("success", Status.OK, list.toArray()).buildListResponse();
            }
            return new ResponseBuilder("success", Status.OK, PageUtils.listInPages(list, page, 20)).buildListResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();

        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/order/{atributo}/{orden}/{page}")
    public Response ordenarConductores(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden,
            @PathParam("page") Integer page)
            throws Exception {

        VehiculoServices ts = new VehiculoServices();
        try {
            LinkedList<Vehiculo> list = ts.order(atributo, orden);
            if (ts.order(atributo, orden).isEmpty()) {
                return new ResponseBuilder("success", Status.OK, PageUtils.listInPages(list, page, 20))
                        .buildListResponse();
            }
            return new ResponseBuilder("success", Status.OK, PageUtils.listInPages(list, page, 20))
                    .buildListResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error", Status.INTERNAL_SERVER_ERROR, e.getMessage())
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/estados")
    public Response getEstados() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        VehiculoServices vs = new VehiculoServices();
        for (VehiculoEstadoEnum t : vs.getEstados()) {
            map.put(t.toString(), t.getEstado());
        }
        return new ResponseBuilder("Success", Status.OK, map).bulidResponse();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/criterios")
    public Response getCriterios() throws ListEmptyException, Exception {
        VehiculoServices ts = new VehiculoServices();
        HashMap<String, Object> criterios = new HashMap<>();
        for (String s : ts.getOrdenAttributeLists()) {
            criterios.put(s, StringFormat.camellCaseToNatural(s));
        }
        return new ResponseBuilder("Success", Status.OK, criterios).bulidResponse();

    }
}
