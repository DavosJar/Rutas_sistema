package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.ItinerarioDao;
import com.app_rutas.models.Conductor;
import com.app_rutas.models.Itinerario;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.Pedido;
import com.app_rutas.models.Vehiculo;
import com.app_rutas.models.enums.ItinerarioEstadoEnum;
import com.app_rutas.controller.tda.list.LinkedList;

public class ItinerarioServices {
    private ItinerarioDao obj;

    public Object[] listShowAll() throws Exception {
        if (!obj.getListAll().isEmpty()) {
            Itinerario[] lista = (Itinerario[]) obj.getListAll().toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("detallesEntrega", lista[i].getDetallesEntrega());
                mapa.put("fechaGeneracion", lista[i].getFechaGeneracion());
                mapa.put("fechaProgranada", lista[i].getFechaProgramada());
                mapa.put("estado", lista[i].getEstado());
                mapa.put("idConductorVeiculo",
                        new ConductorVehiculoServices().showOne(lista[i].getIdConductorVehiculo()));
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) {
        try {
            Itinerario i = obj.getById(id);
            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("id", i.getId());
            mapa.put("detallesEntrega", i.getDetallesEntrega());
            mapa.put("fechaGeneracion", i.getFechaGeneracion());
            mapa.put("fechaProgranada", i.getFechaProgramada());
            mapa.put("estado", i.getEstado());
            mapa.put("idConductorVehiculo", new ConductorVehiculoServices().showOne(id));
            return mapa;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el itinerario");
        }
    }

    public ItinerarioServices() {
        this.obj = new ItinerarioDao();
    }

    public LinkedList<Itinerario> listAll() throws Exception {
        return obj.getListAll();
    }

    public Itinerario getItinerario() {
        return obj.getItinerario();
    }

    public void setItinerario(Itinerario itinerario) {
        obj.setItinerario(itinerario);
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public Boolean update(Itinerario itinerario) throws Exception {
        obj.setItinerario(itinerario);
        return obj.update();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public Itinerario get(Integer index) throws Exception {
        return obj.get(index);
    }

    public ItinerarioEstadoEnum[] getEstado() {
        return obj.getEstado();
    }

    public ItinerarioEstadoEnum getEstadoEnum(String estado) {
        return obj.getEstadoEnum(estado);
    }

    public LinkedList<Itinerario> buscar(String attribute, Object value) throws Exception {
        return obj.buscar(attribute, value);
    }

    public Itinerario buscarPor(String attribute, Object value) throws Exception {
        return obj.buscarPor(attribute, value);
    }

    public String[] getOrdenAttributeLists() {
        return obj.getOrdenAttributeLists();
    }

    public LinkedList<Itinerario> order(String attribute, Integer type) throws Exception {
        return obj.order(attribute, type);
    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public Itinerario getById(Integer id) throws Exception {
        return obj.getById(id);
    }

    public String getByJson(Integer index) throws Exception {
        return obj.getByJson(index);
    }

    public LinkedList<OrdenEntrega> generarOrdenList(Integer id) throws Exception {
        Itinerario itinerario = obj.getById(id);
        Vehiculo vehiculo = new VehiculoServices().getById(itinerario.getIdConductorVehiculo());
        PedidoServices ps = new PedidoServices();

        Pedido[] pedidos = ps.listAll().toArray();
        LinkedList<OrdenEntrega> ordenesEntrega = new LinkedList<>();
        OrdenEntregaServices oes = new OrdenEntregaServices();

        Double capacidadOcupada = 0.0;
        Double capacidadMaxima = vehiculo.getCapacidad();

        for (Pedido pedido : pedidos) {
            Double volumenPedido = pedido.getVolumenTotal();
            Double pesoPedido = pedido.getPesoTotal();
            boolean requiereFrio = pedido.getRequiereFrio();

            if (isFull(capacidadMaxima, capacidadOcupada + volumenPedido) ||
                    isFull(vehiculo.getPesoMaximo(), capacidadOcupada + pesoPedido)) {
                continue;
            }

            boolean esRefrigerado = vehiculo.getRefrigerado() != null && vehiculo.getRefrigerado();

            if ((requiereFrio && !esRefrigerado) || (!requiereFrio && esRefrigerado)) {
                continue;
            }

            OrdenEntrega ordenEntrega = oes.generarOrdenEntrega(pedido, id);
            ordenesEntrega.add(ordenEntrega);
            oes.save();

            capacidadOcupada += volumenPedido;

            ps.setPedido(pedido);
            ps.matchAttende(pedido.getId());
        }

        return ordenesEntrega;
    }

    private Boolean isFull(Double max, Double toAdd) {
        return toAdd <= max;
    }
}