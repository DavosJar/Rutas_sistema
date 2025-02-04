package com.app_rutas.rest;

import java.util.HashMap;

import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
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

import com.app_rutas.controller.dao.services.CuentaServices;
import com.app_rutas.controller.dao.services.TrabajadorServices;
import com.app_rutas.controller.excepcion.ListEmptyException;
import com.app_rutas.controller.excepcion.ResourceNotFoundException;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.losgs.LogBuilderServices;
import com.app_rutas.losgs.LogType;
import com.app_rutas.models.JwtUtil;
import com.app_rutas.models.Trabajador;
import com.app_rutas.models.enums.Rol;
import com.app_rutas.models.enums.Sexo;
import com.app_rutas.models.enums.TipoIdentificacion;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.app_rutas.utils.MsgEnum;
import com.app_rutas.utils.PageUtils;
import com.app_rutas.utils.ResponseBuilder;
import com.app_rutas.utils.StringFormat;

@Path("/trabajador")
public class TrabajadorApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{page}")
    public Response getAllProyects(@HeaderParam("Authorization") String authHeader, @PathParam("page") Integer page)
            throws Exception {
        TrabajadorServices ts = new TrabajadorServices();
        // LogBuilderServices lbs = new LogBuilderServices();//a esto no le pares bola
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);

            LinkedList<Trabajador> lista = ts.listAll();
            // lbs.registreLog(LogType.INFO, username, "Lista de trabajadores obtenida
            // correctamente");

            return new ResponseBuilder(MsgEnum.OK.getMsg(), Status.OK, PageUtils.listInPages(lista, page, 20))
                    .buildListResponse();
        } catch (JWTVerificationException e) {
            // lbs.registreLog(LogType.ERROR, "Anónimo", "Token inválido o expirado: " +
            // e.getMessage());
            return new ResponseBuilder("Token inválido o expirado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            // lbs.registreLog(LogType.ERROR, "Anónimo", "Error interno del servidor: " +
            // e.getMessage());
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getTrabajadorById(@HeaderParam("Authorization") String authHeader, @PathParam("id") Integer id)
            throws Exception {
        TrabajadorServices ts = new TrabajadorServices();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado. Usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());
        
        try {
            
            JwtUtil.validateToken(token, Rol.ADMINISTRADOR);

            if (ts.getById(id) == null) {
                return new ResponseBuilder("No se encontró el trabajador con id: " + id, Status.NOT_FOUND)
                        .buildBadResponse();
            }

            //new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador obtenido correctamente");
            return new ResponseBuilder("Success", Status.OK, ts.getById(id)).buildListResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o no autorizado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @Path("/save")
@POST
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Response add(@HeaderParam("Authorization") String authHeader, HashMap<String, Object> request) {
    TrabajadorServices ts = new TrabajadorServices();
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return new ResponseBuilder("Acceso no autorizado. Usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                .buildBadResponse();
    }
    String token = authHeader.substring("Bearer ".length());

    try {
        DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);

        String username = jwt.getSubject();
        new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador creado correctamente");
        ts.addTrabajador(request);
        return new ResponseBuilder("Success", Status.OK).buildOkResponse();
    } catch (ResourceNotFoundException e) {
        return new ResponseBuilder(e.getMessage(), Status.NOT_FOUND).buildBadResponse();
    } catch (ValueAlreadyExistException e) {
        return new ResponseBuilder(e.getMessage(), Status.BAD_REQUEST).buildBadResponse();
    } catch (ValidationException e) {
        return new ResponseBuilder(e.getMessage(), Status.BAD_REQUEST).buildBadResponse();
    } catch (Exception e) {
        return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                .buildBadResponse();
    }
}

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@HeaderParam("Authorization") String authHeader, @PathParam("id") Integer id)
            throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado, usa 'Authorization' Bearer <token>", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());

        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);
            String username = jwt.getSubject();
            TrabajadorServices ts = new TrabajadorServices();
            ts.setTrabajador(ts.getById(id));
            ts.delete();
            new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador eliminado correctamente");
            return new ResponseBuilder("Success", Status.OK).buildOkResponse();
        } catch (JWTVerificationException e) {
            new LogBuilderServices().registreLog(LogType.ERROR, "Anónimo",
                    "Token inválido o no autorizado: " + e.getMessage());
            return new ResponseBuilder("Token inválido o no autorizado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            new LogBuilderServices().registreLog(LogType.ERROR, "Anónimo",
                    "Error interno del servidor: " + e.getMessage());
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(@HeaderParam("Authorization") String authHeader, HashMap<String, Object> request) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado, usa 'A{uthorization Bearer <token>'.",
                    Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            TrabajadorServices ts = new TrabajadorServices();
            Integer id = Integer.valueOf(request.get("id").toString());

            try {
                JwtUtil.validateToken(token, Rol.ADMINISTRADOR);
                ts.updateTrabajador(request);
                ts.update();
                String username = JwtUtil.extractUsername(token);
                new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador actualizado correctamente");
                return new ResponseBuilder("Success", Status.OK).buildOkResponse();
            } catch (JWTVerificationException adminException) {
                String usernameIn = new CuentaServices().obtenerCuentaPor("idTrabajador", id).getUsername();
                try {
                    JwtUtil.validateToken(token, usernameIn);

                    ts.updateTrabajador(request);
                    ts.update();
                    DecodedJWT jwt = JwtUtil.validateToken(token);
                    String username = jwt.getSubject();
                    new LogBuilderServices().registreLog(LogType.INFO, username,
                            "Trabajador actualizado correctamente");
                    return new ResponseBuilder("Success", Status.OK).buildOkResponse();
                } catch (JWTVerificationException e) {
                    return new ResponseBuilder("No tiene permisos para realizar esta acción.", Status.FORBIDDEN)
                            .buildBadResponse();
                }
            }

        } catch (ValueAlreadyExistException e) {
            return new ResponseBuilder(e.getMessage(), Status.BAD_REQUEST).buildBadResponse();
        } catch (ResourceNotFoundException e) {
            return new ResponseBuilder(e.getMessage(), Status.NOT_FOUND).buildBadResponse();
        } catch (IllegalArgumentException e) {
            return new ResponseBuilder(e.getMessage(), Status.BAD_REQUEST).buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/ident/{identificacion}")
    public Response searchTrabajador(@HeaderParam("Authorization") String authHeader,
            @PathParam("identificacion") String identificacion) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado, usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);
            String username = jwt.getSubject();
            TrabajadorServices ts = new TrabajadorServices();

            if (ts.obtenerTrabajadorPor("identificacion", identificacion) == null) {
                return new ResponseBuilder("No se encontró trabajador con identificación: " + identificacion,
                        Status.NOT_FOUND)
                        .buildBadResponse();
            }
            new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador encontrado correctamente");
            return new ResponseBuilder("Success", Status.OK,
                    ts.obtenerTrabajadorPor("identificacion", identificacion))
                    .buildOkResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o no autorizado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/search/{atributo}/{valor}i{page}")
    public Response buscarTrabajadores(@HeaderParam("Authorization") String authHeader,
            @PathParam("atributo") String atributo, @PathParam("valor") String valor, @PathParam("page") Integer page)
            throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseBuilder("Acceso no autorizado, usa 'Authorization Bearer <token>'.", Status.UNAUTHORIZED)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);
            String username = jwt.getSubject();
            TrabajadorServices ts = new TrabajadorServices();

            if (ts.obtenerTrabajadorPor(atributo, valor) == null) {
                return new ResponseBuilder("No se encontró trabajador con " + atributo + ": " + valor, Status.NOT_FOUND)
                        .buildBadResponse();
            }
            new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajador encontrado correctamente");
            return new ResponseBuilder("Success", Status.OK,
                    PageUtils.listInPages(ts.getTrabajadoresBy(atributo, valor), page, 20))
                    .buildListResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o no autorizado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/order/{atributo}/{orden}/{page}")
    public Response ordenarTrabajadores(@HeaderParam("Authorization") String authHeader,
            @PathParam("atributo") String atributo, @PathParam("orden") Integer orden, @PathParam("page") Integer page)
            throws Exception {
        if (orden != 1 && orden != 0) {
            return new ResponseBuilder("Orden no válido. Use 1 para ascendente y 0 para descendente",
                    Status.BAD_REQUEST)
                    .buildBadResponse();
        }
        String token = authHeader.substring("Bearer ".length());
        try {
            DecodedJWT jwt = JwtUtil.validateToken(token, Rol.ADMINISTRADOR);
            String username = jwt.getSubject();
            TrabajadorServices ts = new TrabajadorServices();
            LinkedList<Trabajador> lista = ts.listAll();
            lista.order(atributo, orden);
            new LogBuilderServices().registreLog(LogType.INFO, username, "Trabajadores ordenados correctamente");
            return new ResponseBuilder("Success", Status.OK, PageUtils.listInPages(lista, page, 20))
                    .buildListResponse();
        } catch (JWTVerificationException e) {
            return new ResponseBuilder("Token inválido o no autorizado: " + e.getMessage(), Status.UNAUTHORIZED)
                    .buildBadResponse();
        } catch (Exception e) {
            return new ResponseBuilder("Error interno del servidor: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR)
                    .buildBadResponse();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sexo")
    public Response getSexo() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        TrabajadorServices ts = new TrabajadorServices();
        for (Sexo s : ts.getSexos()) {
            map.put(s.toString(), s.getDescripcion());
        }
        return new ResponseBuilder("Success", Status.OK, map).bulidResponse();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/tipo_identificacion")
    public Response geTipos() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        TrabajadorServices ts = new TrabajadorServices();
        for (TipoIdentificacion t : ts.getTipos()) {
            map.put(t.toString(), t.getDescripcion());
        }
        return new ResponseBuilder("Success", Status.OK, map).bulidResponse();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/criterios")
    public Response getCriterios() throws ListEmptyException, Exception {
        TrabajadorServices ts = new TrabajadorServices();
        HashMap<String, Object> criterios = new HashMap<>();
        for (String s : ts.getTrabajadorAttributeLists()) {
            criterios.put(s, StringFormat.camellCaseToNatural(s));
        }
        return new ResponseBuilder("Success", Status.OK, criterios).bulidResponse();

    }
}