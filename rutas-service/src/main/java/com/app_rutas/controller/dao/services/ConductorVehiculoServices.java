package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.ConductorVehiculoDao;
import com.app_rutas.models.Conductor;
import com.app_rutas.models.ConductorVehiculo;
import com.app_rutas.models.Vehiculo;
import com.app_rutas.controller.tda.list.LinkedList;

public class ConductorVehiculoServices {
    private ConductorVehiculoDao obj;

    public Object[] listShowAll(LinkedList<ConductorVehiculo> encontrados) throws Exception {
        if (!obj.getListAll().isEmpty()) {
            ConductorVehiculo[] lista = encontrados.toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                Vehiculo v = new VehiculoServices().getById(lista[i].getIdVehiculo());
                Conductor c = new ConductorServices().get(lista[i].getIdConductor());
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("fechaAsignacion", lista[i].getFechaAsignacion());
                mapa.put("fechaDeBaja", lista[i].getFechaDeBaja());
                mapa.put("isActive", lista[i].getIsActive());
                mapa.put("vehiculo", v);
                mapa.put("conductor", c);
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) {
        try {
            ConductorVehiculo cv = obj.getById(id);
            HashMap<String, Object> mapa = new HashMap<>();
            Vehiculo v = new VehiculoServices().get(cv.getIdVehiculo());
            Conductor c = new ConductorServices().get(cv.getIdConductor());
            mapa.put("id", cv.getId());
            mapa.put("fechaAsignacion", cv.getFechaAsignacion());
            mapa.put("fechaDeBaja", cv.getFechaDeBaja());
            mapa.put("isActive", cv.getIsActive());
            mapa.put("vehiculo", v);
            mapa.put("conductor", c);

            return mapa;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el itinerario");
        }
    }

    public ConductorVehiculoServices() {
        this.obj = new ConductorVehiculoDao();
    }

    public LinkedList<ConductorVehiculo> listAll() throws Exception {
        return obj.getListAll();
    }

    public ConductorVehiculo getConductorVehiculo() {
        return obj.getConductor();
    }

    public void setConductorVehiculo(ConductorVehiculo conductorAsignado) {
        obj.setConductor(conductorAsignado);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(ConductorVehiculo conductorAsignado) throws Exception {
        obj.setConductor(conductorAsignado);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public ConductorVehiculo get(Integer index) throws Exception {
        return obj.get(index);
    }

    public LinkedList<ConductorVehiculo> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public ConductorVehiculo buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<ConductorVehiculo> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public ConductorVehiculo getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        ConductorVehiculo persona = this.getConductorVehiculo();
        FieldValidator.validateAndSet(persona, map, field, validations);
    }
}