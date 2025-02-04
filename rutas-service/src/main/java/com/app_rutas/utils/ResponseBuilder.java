package com.app_rutas.utils;

import java.util.HashMap;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ResponseBuilder {

    private String message;
    private Status status;
    private Object data;

    public ResponseBuilder() {
    }

    public ResponseBuilder(String message, Status status) {
        this.message = message;
        this.status = status;
    }

    public ResponseBuilder(String message, Status status, Object data) {
        this.message = message;
        this.status = status;
        setData(data);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        if (data == null) {
            this.data = null;
        } else if (data.getClass().isArray()) {
            this.data = data;
        } else if (!(data instanceof Iterable)) {
            this.data = data;
        } else {
            throw new IllegalArgumentException("Data debe ser un objeto único o un arreglo.");
        }
    }

    private HashMap<String, Object> toHashMap() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", this.message);
        response.put("status", this.status);

        return response;
    }

    public Response buildBadResponse() {
        return Response.status(this.status).entity(this.toHashMap()).build();
    }

    public Response buildOkResponse() {
        return Response.ok(this.toHashMap()).build();
    }

    public Response buildListResponse() {
        return Response.ok(this).build();
    }

    public Response bulidResponse() {
        return Response.status(this.status).entity(this).build();
    }
}