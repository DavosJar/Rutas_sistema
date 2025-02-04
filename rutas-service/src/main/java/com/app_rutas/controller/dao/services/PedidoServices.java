package com.app_rutas.controller.dao.services;

import java.util.HashMap;
import java.util.Random;

import com.app_rutas.controller.dao.PedidoDao;
import com.app_rutas.models.Cliente;
import com.app_rutas.models.Pedido;
import com.app_rutas.models.PuntoEntrega;
import com.app_rutas.models.enums.ContenidoEnum;
import com.app_rutas.controller.tda.list.LinkedList;

public class PedidoServices {
    private PedidoDao obj;

    public Object[] listShowAll() throws Exception {
        if (!obj.getListAll().isEmpty()) {
            Pedido[] lista = (Pedido[]) obj.getListAll().toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                PuntoEntrega pe = new PuntoEntregaServices().get(lista[i].getIdPuntoEntrega());
                Cliente c = new ClienteServices().get(lista[i].getIdCliente());
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("fechaRegistro", lista[i].getFechaRegistro());
                mapa.put("codigoUnico", lista[i].getCodigoUnico());
                mapa.put("pesoTotal", lista[i].getPesoTotal());
                mapa.put("volumen", lista[i].getVolumenTotal());
                mapa.put("requiereFrio", lista[i].getRequiereFrio());
                mapa.put("contenido", lista[i].getContenido());
                mapa.put("atendido", lista[i].getIsAttended());
                mapa.put("puntoEntrega", pe);
                mapa.put("cliente", c);
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) {
        try {
            Pedido p = obj.getById(id);
            PuntoEntrega pe = new PuntoEntregaServices().get(p.getIdPuntoEntrega());
            System.out.println(p.getCodigoUnico());
            Cliente c = new ClienteServices().get(p.getIdCliente());
            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("fechaRegistro", p.getFechaRegistro());
            mapa.put("codigoUnico", p.getCodigoUnico());
            mapa.put("pesoTotal", p.getPesoTotal());
            mapa.put("volumen", p.getVolumenTotal());
            mapa.put("requiereFrio", p.getRequiereFrio());
            mapa.put("contenido", p.getContenido());
            mapa.put("atendido", p.getIsAttended());
            mapa.put("puntoEntrega", pe);
            mapa.put("cliente", c);
            return mapa;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el pedido");
        }
    }

    public PedidoServices() {
        this.obj = new PedidoDao();
    }

    public LinkedList<Pedido> listAll() throws Exception {
        return obj.getListAll();
    }

    public Pedido getPedido() {
        return obj.getPedido();
    }

    public void setPedido(Pedido pedido) {
        obj.setPedido(pedido);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(Pedido pedido) throws Exception {
        obj.setPedido(pedido);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public Pedido get(Integer index) throws Exception {
        return obj.get(index);
    }

    public ContenidoEnum[] getContenidos() {
        return obj.getContenido();
    }

    public ContenidoEnum getContenido(String estado) {
        return obj.getContenidoEnum(estado);
    }

    public LinkedList<Pedido> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public Pedido buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<Pedido> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public Pedido getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Pedido persona = this.getPedido();
        FieldValidator.validateAndSet(persona, map, field, validations);
    }

    public String generarCodigoUnico(int id) {
        Random random = new Random();
        int aleatorio = random.nextInt(10000);
        return String.format("%04d%04d", id % 10000, aleatorio);
    }

    public void matchAttende(Integer id) throws Exception {
        this.obj.setPedido(getById(id));
        this.obj.getPedido().setIsAttended(true);
        this.obj.update();
    }
}