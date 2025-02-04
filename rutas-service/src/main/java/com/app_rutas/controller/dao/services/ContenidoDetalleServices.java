package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.ContenidoDetalleDao;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.ContenidoDetalle;
import com.app_rutas.models.enums.ContenidoEnum;

public class ContenidoDetalleServices {

    private ContenidoDetalleDao obj;

    public ContenidoDetalleServices() {
        obj = new ContenidoDetalleDao();
    }

    public ContenidoDetalle getContenidoDetalle() {
        return obj.getContenidoDetalle();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<ContenidoDetalle> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setContenidoDetalle(ContenidoDetalle contenidoDetalle) {
        obj.setContenidoDetalle(contenidoDetalle);
    }

    public ContenidoDetalle getById(Integer id) throws Exception {
        return obj.getContenidoDetalleById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();

    }

    public LinkedList<ContenidoDetalle> getContenidoDetalleesBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<ContenidoDetalle> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public ContenidoDetalle obtenerContenidoDetallePor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public String[] getContenidoDetalleAttributeLists() {
        return obj.getContenidoDetalleAttributeLists();
    }

    public ContenidoEnum[] getContenidoEnum() {
        return obj.getContenidoTypes();
    }

    public ContenidoEnum getContenidoType(String type) {
        return obj.getContenidoType(type);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        ContenidoDetalle persona = this.getContenidoDetalle();
        FieldValidator.validateAndSet(persona, map, field, validations);
    }

}