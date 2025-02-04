package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.CuentaDao;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Cuenta;
import com.app_rutas.models.enums.Rol;
import com.auth0.jwt.exceptions.JWTVerificationException;

public class CuentaServices {

    private CuentaDao obj;

    public CuentaServices() {
        obj = new CuentaDao();
    }

    public Cuenta getCuenta() {
        return obj.getCuenta();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Cuenta> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setCuenta(Cuenta cuenta) {
        obj.setCuenta(cuenta);
    }

    public Cuenta getById(Integer id) throws Exception {
        return obj.getCuentaById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();

    }

    public LinkedList<Cuenta> getCuentaesBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Cuenta> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Cuenta obtenerCuentaPor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Rol getRol(String rol) {
        return obj.getRol(rol);
    }

    public Rol[] getRolList() {
        return obj.getRolList();
    }

    public String[] getCuentaAttributeLists() {
        return obj.getCuentaAttributeLists();
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Cuenta cuenta = this.getCuenta();
        FieldValidator.validateAndSet(cuenta, map, field, validations);
    }

    public Boolean exist(Integer id) throws Exception {

        if (new TrabajadorServices().getById(id) != null || new ConductorServices().getConductorById(id) != null) {
            return true;
        } else {
            throw new Exception("El trabajador no existe.");
        }
    }

    public String authenticate(String username, String password) throws Exception {
        return obj.authenticate(username, password);
    }

    public void validarToken(String authHeader) throws JWTVerificationException {
        CuentaDao cuentaDao = new CuentaDao();
        cuentaDao.validarToken(authHeader);
    }

}