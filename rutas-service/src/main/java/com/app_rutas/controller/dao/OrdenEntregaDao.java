package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.models.OrdenEntrega;
import com.app_rutas.models.enums.EstadoEnum;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class OrdenEntregaDao extends AdapterDao<OrdenEntrega> {
    private OrdenEntrega ordenEntrega;
    private LinkedList<OrdenEntrega> listAll;

    public OrdenEntregaDao() {
        super(OrdenEntrega.class);
    }

    public OrdenEntrega getOrdenEntrega() {
        if (this.ordenEntrega == null) {
            this.ordenEntrega = new OrdenEntrega();
        }
        return this.ordenEntrega;
    }

    public void setOrdenEntrega(OrdenEntrega ordenEntrega) {
        this.ordenEntrega = ordenEntrega;
    }

    public LinkedList<OrdenEntrega> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(OrdenEntrega.class);
        try {
            this.ordenEntrega.setId(id);
            this.persist(this.ordenEntrega);
            Contador.actualizarContador(OrdenEntrega.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el ordenEntrega: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.ordenEntrega == null || this.ordenEntrega.getId() == null) {
            throw new Exception("No se ha seleccionado un ordenEntrega para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getByIndex("id", this.ordenEntrega.getId());
        if (index == -1) {
            throw new Exception("OrdenEntrega no encontrado.");
        }
        try {
            this.merge(this.ordenEntrega, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el ordenEntrega: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.ordenEntrega == null || this.ordenEntrega.getId() == null) {
            throw new Exception("No se ha seleccionado un ordenEntrega para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getByIndex("id", this.ordenEntrega.getId());
        if (index == -1) {
            throw new Exception("OrdenEntrega no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el ordenEntrega: " + e.getMessage());
        }
    }

    private LinkedList<OrdenEntrega> sequentialSearch(String attribute, Object value) throws Exception {
        LinkedList<OrdenEntrega> lista = this.listAll();
        OrdenEntrega[] aux = lista.toArray();

        LinkedList<OrdenEntrega> ordenes = new LinkedList<>();

        if (aux.length > 0) {
            boolean esNumerico = value instanceof Number;

            for (OrdenEntrega ordenEntrega : aux) {
                Object attributeValue = obtenerAttributeValue(ordenEntrega, attribute);

                if (attributeValue == null) {
                    continue;
                }

                int comparacion;
                if (esNumerico) {
                    Number attributeNumberValue = (Number) attributeValue;
                    Number searchValue = (Number) value;
                    comparacion = Double.compare(attributeNumberValue.doubleValue(), searchValue.doubleValue());
                } else {
                    String attributeStringValue = attributeValue.toString().toLowerCase();
                    String searchStringValue = value.toString().toLowerCase();
                    comparacion = attributeStringValue.compareTo(searchStringValue);
                }

                if (comparacion == 0) {
                    ordenes.add(ordenEntrega);
                }
            }
        }

        return ordenes;
    }

    public LinkedList<OrdenEntrega> buscar(String attribute, Object value) throws Exception {
        return sequentialSearch(attribute, value);
    }

    public OrdenEntrega buscarPor(String attribute, Object value) throws Exception {
        LinkedList<OrdenEntrega> lista = listAll();
        OrdenEntrega p = null;

        if (!lista.isEmpty()) {
            OrdenEntrega[] ordenes = lista.toArray();
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
            OrdenEntrega[] ordenes = this.listAll.toArray();
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

    private Object obtenerAttributeValue(Object object, String attribute)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String normalizedAttribute = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);

        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equals(normalizedAttribute) && method.getParameterCount() == 0) {
                Object value = method.invoke(object);

                return value;
            }
        }

        throw new NoSuchMethodException("No se encontró el método getter para el atributo: " + attribute);
    }

    public String[] getOrdenAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : OrdenEntrega.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
    }

    public LinkedList<OrdenEntrega> order(String attribute, Integer type) throws Exception {
        LinkedList<OrdenEntrega> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.ordenEntrega);
    }

    public OrdenEntrega getById(Integer id) throws Exception {
        return get(id);
    }

    public String getByJasonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public EstadoEnum getEstadoEnum(String estado) {
        return EstadoEnum.valueOf(estado);
    }

    public EstadoEnum[] getEstado() {
        return EstadoEnum.values();
    }

    public String getByJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

    public String codigoU(String input) {
        int base = 0;
        for (char c : input.toCharArray()) {
            base += c;
        }
        int randomNum = (int) (Math.random() * 100000);
        String codigo = String.format("%010d", base * 100000 + randomNum);
        return codigo;
    }
}