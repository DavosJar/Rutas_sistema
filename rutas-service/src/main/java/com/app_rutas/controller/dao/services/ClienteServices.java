package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.ClienteDao;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Cliente;

public class ClienteServices {

    private ClienteDao obj;

    public ClienteServices() {
        obj = new ClienteDao();
    }

    public Cliente getCliente() {
        return obj.getCliente();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Cliente> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setCliente(Cliente cliente) {
        obj.setCliente(cliente);
    }

    public Cliente getById(Integer id) throws Exception {
        return obj.getClienteById(id);

    }

    public Cliente get(Integer index) throws Exception {
        return obj.get(index);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public LinkedList<Cliente> getClientesBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Cliente> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Cliente obtenerClientePor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public String[] getClienteAttributeLists() {
        return obj.getClienteAttributeLists();
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Cliente cliente = this.getCliente();
        FieldValidator.validateAndSet(cliente, map, field, validations);
    }
}