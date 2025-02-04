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

import com.app_rutas.controller.dao.services.ConductorServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Conductor;
import com.app_rutas.utils.StringFormat;
import com.app_rutas.utils.ResponseBuilder;

@Path("/conductor")
public class ConductorApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response getAllProyects() throws ListEmptyException, Exception {
        ConductorServices cs = new ConductorServices();
        // EventoCrudServices ev = new EventoCrudServices();
        try {
            LinkedList<Conductor> lista = cs.listAll();
            return new ResponseBuilder("Succes", Status.OK, lista.toArray()).buildListResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error al obtener la lista de conductores: " + e.getMessage(),
                    Status.INTERNAL_SERVER_ERROR).buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getConductorById(@PathParam("id") Integer id) throws Exception {

        ConductorServices cs = new ConductorServices();
        try {
            if (cs.get(id) == null) {
                return new ResponseBuilder("No se encontro el conductor con id: " + id, Status.NOT_FOUND)
                        .bulidResponse();
            }
            return new ResponseBuilder("Succes", Status.OK, cs.get(id)).buildOkResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error al obtener el conductor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        ConductorServices cs = new ConductorServices();

        try {

            cs.validateField("nombre", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            cs.validateField("apellido", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");

            if (map.get("tipoIdentificacion") != null) {
                cs.getConductor().setTipoIdentificacion(cs.getTipo(map.get("tipoIdentificacion").toString()));
            }
            cs.validateField("identificacion", map, "NOT_NULL", "IS_UNIQUE", "NUMERIC", "LENGTH=10");
            cs.validateField("fechaNacimiento", map, "DATE", "NOT_NULL", "MIN_DATE=1900-01-01", "MAX_DATE=2021-12-31");
            cs.validateField("direccion", map, "NOT_NULL", "ALPHANUMERIC", "MAX_LENGTH=50", "MIN_LENGTH=5");
            cs.validateField("telefono", map, "NOT_NULL", "NUMERIC", "LENGTH=10");
            cs.validateField("email", map, "NOT_NULL", "VALID_EMAIL", "IS_UNIQUE");
            if (map.get("sexo") != null) {
                cs.getConductor().setSexo(cs.getSexo(map.get("sexo").toString()));
            }
            cs.validateField("licenciaConducir", map, "NOT_NULL", "IS_UNIQUE", "NUMERIC", "LENGTH=10");

            cs.validateField("caducidadLicencia", map, "DATE", "NOT_NULL", "MIN_DATE=1950-01-01",
                    "MAX_DATE=2021-12-31");
            cs.validateField("salario", map, "NOT_NULL", "MIN_VALUE=0");
            if (map.get("turno") != null) {
                cs.getConductor().setTurno(cs.getTurno(map.get("turno").toString()));
            }
            if (map.get("estado") != null) {
                cs.getConductor().setEstado(cs.getEstado(map.get("estado").toString()));
            }
            cs.getConductor().setIsAsigned(false);
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
        ConductorServices cs = new ConductorServices();
        try {
            cs.getConductor().setId(id);
            cs.delete();
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
        ConductorServices cs = new ConductorServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }
            int id = (int) map.get("id");
            cs.setConductor(cs.getConductorById(id));
            System.out.println("Conductor obtenido " + cs.getConductorById(id));

            cs.validateField("id", map, "MIN_VALUE=1");
            if (map.get("nombre") != null && !map.get("nombre").equals(cs.getConductor().getNombre())) {
                cs.validateField("nombre", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            }
            if (map.get("apellido") != null && !map.get("apellido").equals(cs.getConductor().getApellido())) {
                cs.validateField("apellido", map, "NOT_NULL", "ALPHABETIC", "MAX_LENGTH=25", "MIN_LENGTH=3");
            }
            if (map.get("tipoIdentificacion") != null
                    && !map.get("tipoIdentificacion").equals(cs.getConductor().getTipoIdentificacion())) {
                cs.getConductor().setTipoIdentificacion(cs.getTipo(map.get("tipoIdentificacion").toString()));
            }
            if (map.get("identificacion") != null) {
                String newIdentificacion = map.get("identificacion").toString();
                String currentIdentificacion = cs.getConductor().getEmail();
                if (newIdentificacion.equalsIgnoreCase(currentIdentificacion)) {
                    cs.getConductor().setIdentificacion(currentIdentificacion);
                } else {
                    cs.validateField("identificacion", map, "NOT_NULL", "IS_UNIQUE", "NUMERIC", "LENGTH=10");
                }
            }
            if (map.get("fechaNacimiento") != null
                    && !map.get("fechaNacimiento").equals(cs.getConductor().getFechaNacimiento())) {
                cs.getConductor().setFechaNacimiento(map.get("fechaNacimiento").toString());
            }
            cs.validateField("direccion", map, "NOT_NULL", "ALPHANUMERIC", "MAX_LENGTH=50", "MIN_LENGTH=5");

            if (map.get("telefono") != null && !map.get("telefono").equals(cs.getConductor().getTelefono())) {
                cs.validateField("telefono", map, "NOT_NULL", "NUMERIC", "LENGTH=10");

            }
            if (map.get("email") != null) {
                String newEmail = map.get("email").toString();
                String currentEmail = cs.getConductor().getEmail();
                if (newEmail.equalsIgnoreCase(currentEmail)) {
                    cs.getConductor().setEmail(currentEmail);
                } else {
                    cs.validateField("email", map, "NOT_NULL", "VALID_EMAIL", "IS_UNIQUE");
                }
            }
            if (map.get("sexo") != null && !map.get("sexo").equals(cs.getConductor().getSexo())) {
                cs.getConductor().setSexo(cs.getSexo(map.get("sexo").toString()));
            }
            if (map.get("licenciaConducir") != null) {
                String newLicencia = map.get("licenciaConducir").toString();
                String currentLic = cs.getConductor().getLicenciaConducir();
                if (newLicencia.equalsIgnoreCase(currentLic)) {
                    cs.getConductor().setLicenciaConducir(currentLic);
                } else {
                    cs.validateField("licenciaConducir", map, "NOT_NULL", "IS_UNIQUE", "NUMERIC", "LENGTH=10");
                }
            }
            if (map.get("salario") != null && !map.get("salario").equals(cs.getConductor().getNombre())) {
                cs.validateField("salario", map, "NOT_NULL", "MIN_VALUE=0");
            }
            if (map.get("turno") != null
                    && !map.get("turno").equals(cs.getConductor().getTurno())) {
                cs.getConductor().setTurno(cs.getTurno(map.get("turno").toString()));
            }
            if (map.get("estado") != null
                    && !map.get("estado").equals(cs.getConductor().getEstado())) {
                cs.getConductor().setEstado(cs.getEstado(map.get("estado").toString()));
            }
            if (map.get("isAsigend") != null) {
                cs.getConductor().setIsAsigned(Boolean.parseBoolean(map.get("isAsigned").toString()));
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
    @Path("/list/search/ident/{identificacion}")
    public Response searchConductor(@PathParam("identificacion") String identificacion) throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ConductorServices cs = new ConductorServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cs.obtenerConductorPor("identificacion", identificacion));
            if (cs.obtenerConductorPor("identificacion", identificacion) == null) {
                res.put("estado", "error");
                res.put("data", "No se encontro el conductor con identificacion: " + identificacion);
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
    public Response buscarConductores(@PathParam("atributo") String atributo, @PathParam("valor") String valor)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ConductorServices cs = new ConductorServices();
        try {
            res.put("estado", "Ok");
            res.put("data", cs.getConductoresBy(atributo, valor).toArray());
            if (cs.getConductoresBy(atributo, valor).isEmpty()) {
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
    public Response ordenarConductores(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        ConductorServices cs = new ConductorServices();
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sexo")
    public Response getSexo() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        ConductorServices cs = new ConductorServices();
        HashMap<String, Object> types = new HashMap<>();
        for (int i = 0; i < cs.getSexos().length; i++) {
            types.put(cs.getSexos()[i].name(), cs.getSexos()[i].getDescripcion());
        }
        map.put("msg", "OK");
        map.put("data", types);
        return Response.ok(map).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tipo_identificacion")
    public Response geTipos() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        ConductorServices cs = new ConductorServices();
        HashMap<String, Object> types = new HashMap<>();
        for (int i = 0; i < cs.getTipos().length; i++) {
            types.put(cs.getTipos()[i].name(), cs.getTipos()[i].getDescripcion());
        }
        map.put("msg", "OK");
        map.put("data", types);
        return Response.ok(map).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/criterios")
    public Response getCriterios() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        ConductorServices cs = new ConductorServices();
        HashMap<String, Object> types = new HashMap<>();
        for (int i = 0; i < cs.getAttributeList().length; i++) {
            types.put(cs.getAttributeList()[i], StringFormat.camellCaseToNatural(cs.getAttributeList()[i]));
        }
        map.put("msg", "OK");
        map.put("data", types);
        return Response.ok(map).build();
    }

    @Path("/turno")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getType() {
        HashMap<String, Object> map = new HashMap<>();
        ConductorServices ps = new ConductorServices();
        HashMap<String, Object> types = new HashMap<>();
        for (int i = 0; i < ps.getTurnos().length; i++) {
            types.put(ps.getTurnos()[i].name(), ps.getTurnos()[i].getTurno());
        }
        map.put("msg", "OK");
        map.put("data", types);

        return Response.ok(map).build();
    }

    @Path("/estado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEstadoC() {
        HashMap<String, Object> map = new HashMap<>();
        ConductorServices ps = new ConductorServices();
        HashMap<String, Object> types = new HashMap<>();
        for (int i = 0; i < ps.getEstados().length; i++) {
            types.put(ps.getEstados()[i].name(), ps.getEstados()[i].getEstado());
        }
        map.put("msg", "OK");
        map.put("data", types);
        return Response.ok(map).build();
    }
}