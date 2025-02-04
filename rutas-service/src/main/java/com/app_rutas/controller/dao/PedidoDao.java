package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Pedido;
import com.app_rutas.models.enums.ContenidoEnum;
import com.google.gson.Gson;

import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class PedidoDao extends AdapterDao<Pedido> {
    private Pedido pedido;
    private LinkedList<Pedido> listAll;

    public PedidoDao() {
        super(Pedido.class);
    }

    public Pedido getPedido() {
        if (this.pedido == null) {
            this.pedido = new Pedido();
        }
        return this.pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LinkedList<Pedido> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Pedido.class);
        try {
            this.pedido.setId(id);
            this.persist(this.pedido);
            Contador.actualizarContador(Pedido.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el pedido: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.pedido == null || this.pedido.getId() == null) {
            throw new Exception("No se ha seleccionado un pedido para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getByIndex("id", this.pedido.getId());
        if (index == -1) {
            throw new Exception("Pedido no encontrado.");
        }
        try {
            this.merge(this.pedido, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el pedido: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.pedido == null || this.pedido.getId() == null) {
            throw new Exception("No se ha seleccionado un pedido para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getByIndex("id", this.pedido.getId());
        if (index == -1) {
            throw new Exception("Pedido no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el pedido: " + e.getMessage());
        }
    }

    private LinkedList<Pedido> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Pedido> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Pedido> ordenes = new LinkedList<>();
        if (!lista.isEmpty()) {
            Pedido[] aux = lista.toArray();
            Integer low = 0;
            Integer high = aux.length - 1;
            Integer mid;
            Integer index = -1;
            String searchValue = value.toString().toLowerCase();
            while (low <= high) {
                mid = (low + high) / 2;

                String midValue = obtenerAttributeValue(aux[mid], attribute).toString().toLowerCase();
                // System.out.println("Comparando: " + midValue + " con " + searchValue);

                if (midValue.startsWith(searchValue)) {
                    if (mid == 0 || !obtenerAttributeValue(aux[mid - 1], attribute).toString().toLowerCase()
                            .startsWith(searchValue)) {
                        index = mid;
                        break;
                    } else {
                        high = mid - 1;
                    }
                } else if (midValue.compareToIgnoreCase(searchValue) < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            if (index.equals(-1)) {
                return ordenes;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                ordenes.add(aux[i]);
                i++;
            }
        }
        return ordenes;
    }

    public LinkedList<Pedido> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Pedido buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Pedido> lista = listAll();
        Pedido p = null;

        if (!lista.isEmpty()) {
            Pedido[] ordenes = lista.toArray();
            for (int i = 0; i < ordenes.length; i++) {
                if (obtenerAttributeValue(ordenes[i], attribute).toString().toLowerCase()
                        .equals(value.toString().toLowerCase())) {
                    p = ordenes[i];
                    break;
                }
            }
        }
        return p;
    }

    private Integer getByIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Pedido[] ordenes = this.listAll.toArray();
            for (int i = 0; i < ordenes.length; i++) {
                if (obtenerAttributeValue(ordenes[i], attribute).toString().toLowerCase()
                        .equals(value.toString().toLowerCase())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private Object obtenerAttributeValue(Object object, String attribute) throws Exception {
        String normalizedAttribute = "get" + attribute.substring(0, 1).toUpperCase()
                + attribute.substring(1).toLowerCase();
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(normalizedAttribute) && method.getParameterCount() == 0) {
                return method.invoke(object);
            }
        }

        throw new NoSuchMethodException("No se encontor el atributo: " + attribute);
    }

    public String[] getOrdenAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : Pedido.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
    }

    public LinkedList<Pedido> order(String attribute, Integer type) throws Exception {
        LinkedList<Pedido> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        if (campo == null || value == null) {
            throw new IllegalArgumentException("El atributo y el valor no pueden ser nulos.");
        }

        if (this.listAll == null) {
            this.listAll = listAll();
        }

        if (this.listAll.isEmpty()) {
            return true;
        }

        Pedido[] pedidos = this.listAll.toArray();

        for (Pedido p : pedidos) {
            Object attributeValue = obtenerAttributeValue(p, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El valor ya existe.");
            }
        }

        return true;
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.pedido);
    }

    public Pedido getById(Integer id) throws Exception {
        return get(id);
    }

    public String getByJasonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public ContenidoEnum getContenidoEnum(String estado) {
        return ContenidoEnum.valueOf(estado);
    }

    public ContenidoEnum[] getContenido() {
        return ContenidoEnum.values();
    }

    public String getByJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }
}