package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.PuntoEntregaDao;
import com.app_rutas.models.PuntoEntrega;
import com.app_rutas.controller.tda.list.LinkedList;

public class PuntoEntregaServices {
    private PuntoEntregaDao obj;

    public Object[] listShowAll() throws Exception {
        if (!obj.getListAll().isEmpty()) {
            PuntoEntrega[] lista = (PuntoEntrega[]) obj.getListAll().toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("callePrincipal", lista[i].getCallePrincipal());
                mapa.put("calleSecundaria", lista[i].getCalleSecundaria());
                mapa.put("num", lista[i].getNum());
                mapa.put("referencia", lista[i].getReferencia());
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) throws Exception {
        PuntoEntrega puntoEntrega = obj.getById(id);
        if (puntoEntrega == null) {
            return null;
        }

        HashMap<String, Object> mapa = new HashMap<>();
        mapa.put("id", puntoEntrega.getId());
        mapa.put("callePrincipal", puntoEntrega.getCallePrincipal());
        mapa.put("calleSecundaria", puntoEntrega.getCalleSecundaria());
        mapa.put("num", puntoEntrega.getNum());
        mapa.put("referencia", puntoEntrega.getReferencia());
        return mapa;
    }

    public PuntoEntregaServices() {
        this.obj = new PuntoEntregaDao();
    }

    public LinkedList<PuntoEntrega> listAll() throws Exception {
        return obj.getListAll();
    }

    public PuntoEntrega getPuntoEntrega() {
        return obj.getPuntoEntrega();
    }

    public void setPuntoEntrega(PuntoEntrega puntoEntrega) {
        obj.setPuntoEntrega(puntoEntrega);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(PuntoEntrega puntoEntrega) throws Exception {
        obj.setPuntoEntrega(puntoEntrega);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public PuntoEntrega get(Integer index) throws Exception {
        return obj.get(index);
    }

    public LinkedList<PuntoEntrega> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public PuntoEntrega buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<PuntoEntrega> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public PuntoEntrega getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        PuntoEntrega persona = this.getPuntoEntrega();
        FieldValidator.validateAndSet(persona, map, field, validations);
    }
}
