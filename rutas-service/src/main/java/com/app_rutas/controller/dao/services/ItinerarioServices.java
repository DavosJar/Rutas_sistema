package com.app_rutas.controller.dao.services;

import java.util.ArrayList;
import java.util.HashMap;

import com.app_rutas.controller.dao.ItinerarioDao;
import com.app_rutas.controller.excepcion.ResourceNotFoundException;
import com.app_rutas.models.ConductorVehiculo;
import com.app_rutas.models.Itinerario;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.Pedido;
import com.app_rutas.models.Vehiculo;
import com.app_rutas.models.enums.ItinerarioEstadoEnum;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.controller.tda.stack.Stack;

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
                mapa.put("fechaProgramada", lista[i].getFechaProgramada());
                mapa.put("estado", lista[i].getEstado());
                mapa.put("conductorVehiculo",
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
            OrdenEntregaServices oes = new OrdenEntregaServices();
            ArrayList<OrdenEntrega> lista = i.getDetallesEntrega();
            LinkedList<Object> litaHash = new LinkedList<>();

            if (lista != null && !lista.isEmpty()) {
                for (OrdenEntrega oe : lista) {
                    litaHash.add(oes.showOne(oe.getId()));
                }
            } else {
                litaHash = new LinkedList<>();
            }

            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("id", i.getId());
            mapa.put("detallesEntrega", litaHash.toArray());
            mapa.put("fechaGeneracion", i.getFechaGeneracion());
            mapa.put("fechaProgramada", i.getFechaProgramada());
            mapa.put("estado", i.getEstado());
            mapa.put("conductorVehiculo", new ConductorVehiculoServices().showOne(i.getIdConductorVehiculo()));
            System.out.println("Hola mapa" + mapa);
            return mapa;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el itinerario", e);
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
        if (itinerario == null) {
            throw new ResourceNotFoundException("Itinerario no encontrado");
        }

        ConductorVehiculo cv = new ConductorVehiculoServices().getById(itinerario.getIdConductorVehiculo());
        if (cv == null) {
            throw new ResourceNotFoundException("Conductor Vehiculo no encontrado");
        }

        Vehiculo vehiculo = new VehiculoServices().getById(cv.getIdVehiculo());
        if (vehiculo == null) {
            throw new ResourceNotFoundException("Vehiculo no encontrado");
        }

        PedidoServices ps = new PedidoServices();
        LinkedList<Pedido> listaPedidos = ps.order("id", 0);
        if (listaPedidos == null || listaPedidos.isEmpty()) {
            throw new ResourceNotFoundException("No hay pedidos por atender");
        }
        Pedido[] pedidos = listaPedidos.toArray();

        Stack<OrdenEntrega> pilaOrdenes = new Stack<>(pedidos.length);
        OrdenEntregaServices oes = new OrdenEntregaServices();
        Double volumenOcupado = 0.0;
        Double pesoOcupado = 0.0;
        Double capacidadMaxima = vehiculo.getCapacidad();
        Double pesoMaximo = vehiculo.getPesoMaximo();
        boolean esRefrigerado = vehiculo.getRefrigerado() != null && vehiculo.getRefrigerado();

        for (Pedido pedido : pedidos) {
            // Solo procesamos pedidos no atendidos
            if (pedido.getIsAttended()) {
                continue; // Si el pedido ya está atendido, lo omitimos
            }

            Double volumenPedido = pedido.getVolumenTotal();
            Double pesoPedido = pedido.getPesoTotal();
            boolean requiereFrio = pedido.getRequiereFrio();

            if (isFull(capacidadMaxima, volumenOcupado + volumenPedido)
                    || isFull(pesoMaximo, pesoOcupado + pesoPedido)) {
                continue;
            }

            if ((requiereFrio && !esRefrigerado) || (!requiereFrio && esRefrigerado)) {
                continue;
            }

            OrdenEntrega ordenEntrega = oes.generarOrdenEntrega(pedido, id);
            if (ordenEntrega != null) {
                pilaOrdenes.push(ordenEntrega);
                volumenOcupado += volumenPedido;
                pesoOcupado += pesoPedido;

                ps.setPedido(pedido);
                ps.matchAttende(pedido.getId());
            }
        }

        LinkedList<OrdenEntrega> ordenesEntrega = new LinkedList<>();
        while (pilaOrdenes.getSize() > 0) {
            ordenesEntrega.add(pilaOrdenes.pop());
        }

        return ordenesEntrega;
    }

    private Boolean isFull(Double max, Double toAdd) {
        return toAdd > max * 0.95;
    }

    public Boolean cancelarOrden(Integer idOrden) throws Exception {
        OrdenEntregaServices oes = new OrdenEntregaServices();
        oes.cancelarOrden(idOrden);
        return true;
    }

    public Boolean finalizarOrden(Integer idOrden) throws Exception {
        OrdenEntregaServices oes = new OrdenEntregaServices();
        oes.finalizarOrden(idOrden);
        return true;
    }

    public Boolean eliminarOrden(Integer idOrden) throws Exception {
        OrdenEntregaServices oes = new OrdenEntregaServices();
        oes.eliminarOrden(idOrden);
        return true;
    }

    public Boolean finalizarItinerario(Integer idItinerario) throws Exception {
        Itinerario itinerario = obj.getById(idItinerario);
        if (itinerario == null) {
            throw new ResourceNotFoundException("Itinerario no encontrado");
        }

        obj.setItinerario(itinerario);
        obj.getItinerario().setEstado(ItinerarioEstadoEnum.COMPLETADO);

        LinkedList<OrdenEntrega> ordenes = new OrdenEntregaServices().buscar("idItinerario", idItinerario);

        for (OrdenEntrega ordenEntrega : ordenes.toArray()) {
            finalizarOrden(ordenEntrega.getId());
        }

        obj.update();
        return true;
    }

    public Boolean cancelarItinerario(Integer idItinerario) throws Exception {
        Itinerario itinerario = obj.getById(idItinerario);
        if (itinerario == null) {
            throw new ResourceNotFoundException("Itinerario no encontrado");
        }

        obj.setItinerario(itinerario);
        obj.getItinerario().setEstado(ItinerarioEstadoEnum.CANCELADO);
        obj.update();

        LinkedList<OrdenEntrega> ordenes = new OrdenEntregaServices().buscar("idItinerario", idItinerario);

        for (OrdenEntrega ordenEntrega : ordenes.toArray()) {
            cancelarOrden(ordenEntrega.getId());
        }

        return true;
    }

    public Boolean eliminarItinerario(Integer idItinerario) throws Exception {
        if (idItinerario == null) {
            throw new IllegalArgumentException("El ID del itinerario no puede ser nulo.");
        }

        Itinerario itinerario = obj.getById(idItinerario);
        if (itinerario == null) {
            throw new ResourceNotFoundException("Itinerario no encontrado");
        }

        OrdenEntregaServices oes = new OrdenEntregaServices();
        LinkedList<OrdenEntrega> ordenes = oes.buscar("idItinerario", idItinerario);

        PedidoServices ps = new PedidoServices();
        ConductorVehiculoServices cvs = new ConductorVehiculoServices();

        if (cvs.get(itinerario.getIdConductorVehiculo()) == null) {
            throw new ResourceNotFoundException("Conductor Vehiculo no encontrado");
        }
        cvs.setConductorVehiculo(cvs.getById(itinerario.getIdConductorVehiculo()));
        if (ordenes.getSize() == 0) {
            cvs.getConductorVehiculo().setIsWorking(false);
            cvs.update();
            obj.setItinerario(itinerario);
            boolean eliminado = obj.delete();
            if (!eliminado) {
                throw new Exception("No se pudo eliminar el itinerario");
            }
            return true;
        }
        OrdenEntrega[] arrayOrdenes = ordenes.toArray();
        for (OrdenEntrega ordenEntrega : arrayOrdenes) {
            Pedido pedido = ps.getById(ordenEntrega.getIdPedido());
            if (pedido != null) {
                ps.setPedido(pedido);
                ps.getPedido().setIsAttended(false);
                ps.update();
            }
        }
        cvs.getConductorVehiculo().setIsWorking(false);
        cvs.update();

        for (OrdenEntrega ordenEntrega : arrayOrdenes) {
            oes.setOrdenEntrega(ordenEntrega);
            oes.delete();
        }
        obj.setItinerario(itinerario);
        obj.delete();

        return true;
    }
}