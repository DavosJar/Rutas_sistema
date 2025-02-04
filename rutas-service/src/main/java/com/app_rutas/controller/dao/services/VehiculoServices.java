package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.VehiculoDao;
import com.app_rutas.models.Vehiculo;
import com.app_rutas.models.enums.VehiculoEstadoEnum;
import com.app_rutas.controller.tda.list.LinkedList;

public class VehiculoServices {
    private VehiculoDao obj;

    public VehiculoServices() {
        this.obj = new VehiculoDao();
    }

    public LinkedList<Vehiculo> listAll() throws Exception {
        return obj.getListAll();
    }

    public Vehiculo getVehiculo() {
        return obj.getVehiculo();
    }

    public void setVehiculo(Vehiculo vehiculo) {
        obj.setVehiculo(vehiculo);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(Vehiculo vehiculo) throws Exception {
        obj.setVehiculo(vehiculo);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public Vehiculo get(Integer index) throws Exception {
        return obj.get(index);
    }

    public VehiculoEstadoEnum[] getEstados() {
        return obj.getEstado();
    }

    public VehiculoEstadoEnum getEstado(String estado) {
        return obj.getEstadoEnum(estado);
    }

    public LinkedList<Vehiculo> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public Vehiculo buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<Vehiculo> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public Vehiculo getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Vehiculo vehiculo = this.getVehiculo();
        FieldValidator.validateAndSet(vehiculo, map, field, validations);
    }
}