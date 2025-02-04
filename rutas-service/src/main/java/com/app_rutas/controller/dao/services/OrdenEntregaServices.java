package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.OrdenEntregaDao;
import com.app_rutas.controller.excepcion.ResourceNotFoundException;
import com.app_rutas.models.Itinerario;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.Pedido;
import com.app_rutas.models.PuntoEntrega;
import com.app_rutas.models.enums.EstadoEnum;
import com.app_rutas.controller.tda.list.LinkedList;

public class OrdenEntregaServices {
    private OrdenEntregaDao obj;

    public Object[] listShowAll() throws Exception {
        if (!obj.getListAll().isEmpty()) {
            OrdenEntrega[] lista = (OrdenEntrega[]) obj.getListAll().toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                Pedido p = new PedidoServices().get(lista[i].getIdPedido());
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("fechaProgramada", lista[i].getFechaProgramada());
                mapa.put("observaciones", lista[i].getObservaciones());
                mapa.put("estado", lista[i].getEstado());
                mapa.put("pedido", p);
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) {
        try {
            OrdenEntrega oe = obj.getById(id);
            Pedido p = new PedidoServices().get(oe.getIdPedido());
            System.out.println(p);
            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("id", oe.getId());
            mapa.put("fechaProgranada", oe.getFechaProgramada());
            mapa.put("estado", oe.getEstado());
            mapa.put("pedido", p);
            return mapa;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar La orden de entrega");
        }
    }

    public OrdenEntregaServices() {
        this.obj = new OrdenEntregaDao();
    }

    public LinkedList<OrdenEntrega> listAll() throws Exception {
        return obj.getListAll();
    }

    public OrdenEntrega getOrdenEntrega() {
        return obj.getOrdenEntrega();
    }

    public void setOrdenEntrega(OrdenEntrega ordenEntrega) {
        obj.setOrdenEntrega(ordenEntrega);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(OrdenEntrega ordenEntrega) throws Exception {
        obj.setOrdenEntrega(ordenEntrega);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public OrdenEntrega get(Integer index) throws Exception {
        return obj.get(index);
    }

    public EstadoEnum[] getEstado() {
        return obj.getEstado();
    }

    public EstadoEnum getEstadoEnum(String estado) {
        return obj.getEstadoEnum(estado);
    }

    public LinkedList<OrdenEntrega> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public OrdenEntrega buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<OrdenEntrega> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public OrdenEntrega getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public String codigoU(String input) {
        return obj.codigoU(input);
    }

    public OrdenEntrega generarOrdenEntrega(Pedido pedido, Integer idItinerario) throws Exception {
        if (pedido == null) {
            throw new Exception("El pedido no existe");
        }

        if (idItinerario == null) {
            throw new Exception("El idItinerario no puede ser nulo");
        }

        Itinerario itinerario = new ItinerarioServices().getById(idItinerario);

        if (itinerario == null) {
            throw new Exception("No se encontró un itinerario con el ID: " + idItinerario);
        }

        obj.setOrdenEntrega(new OrdenEntrega());
        obj.getOrdenEntrega().setIdPedido(pedido.getId());
        obj.getOrdenEntrega().setIdItinerario(idItinerario);
        obj.getOrdenEntrega().setFechaProgramada(itinerario.getFechaProgramada());
        obj.getOrdenEntrega().setEstado(EstadoEnum.PENDIENTE);

        return obj.getOrdenEntrega();
    }

    public Boolean cancelarOrden() throws Exception {
        if (obj.getOrdenEntrega() == null) {
            throw new IllegalStateException("No se ha seleccionado una orden de entrega");
        }

        if (obj.getOrdenEntrega().getEstado() == EstadoEnum.CANCELADO) {
            throw new IllegalStateException("La orden de entrega ya ha sido cancelada");
        }

        obj.getOrdenEntrega().setEstado(EstadoEnum.CANCELADO);
        obj.update();

        Integer idPedido = obj.getOrdenEntrega().getIdPedido();
        if (idPedido == null) {
            throw new IllegalStateException("La orden de entrega no tiene un pedido asociado");
        }

        PedidoServices pedidoServices = new PedidoServices();
        Pedido pedido = pedidoServices.getById(idPedido);
        if (pedido == null) {
            throw new ResourceNotFoundException("No se encontró el pedido asociado a la orden");
        }

        pedido.setIsAttended(false);
        pedidoServices.setPedido(pedido);
        pedidoServices.update();

        return true;
    }

    public Boolean finalizarOrden(Integer id) throws Exception {
        OrdenEntrega orden = obj.getById(id);
        if (orden == null) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }

        obj.setOrdenEntrega(orden);
        obj.getOrdenEntrega().setEstado(EstadoEnum.ENTREGADO);
        obj.update();
        return true;
    }

    public Boolean cancelarOrden(Integer id) throws Exception {
        OrdenEntrega orden = obj.getById(id);
        if (orden == null) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }

        obj.setOrdenEntrega(orden);
        if (!orden.getEstado().equals(EstadoEnum.ENTREGADO)) {
            obj.getOrdenEntrega().setEstado(EstadoEnum.CANCELADO);
        }
        obj.update();

        actualizarPedidoAsociado(orden.getIdPedido());
        return true;
    }

    public Boolean eliminarOrden(Integer id) throws Exception {
        OrdenEntrega orden = obj.getById(id);
        if (orden == null) {
            throw new ResourceNotFoundException("Orden no encontrada");
        }

        obj.setOrdenEntrega(orden);
        obj.delete();

        actualizarPedidoAsociado(orden.getIdPedido());
        return true;
    }

    private void actualizarPedidoAsociado(Integer idPedido) throws Exception {
        PedidoServices pedidoServices = new PedidoServices();
        Pedido pedido = pedidoServices.getById(idPedido);
        if (pedido == null) {
            throw new ResourceNotFoundException("Pedido no encontrado");
        }

        pedidoServices.setPedido(pedido);
        pedidoServices.matchUnAttende(pedido.getId());
    }
}
