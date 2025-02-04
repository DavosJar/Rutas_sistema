package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.ContenidoDetalle;
import com.app_rutas.models.enums.ContenidoEnum;
import com.google.gson.Gson;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class ContenidoDetalleDao extends AdapterDao<ContenidoDetalle> {
    private ContenidoDetalle contenidoDetalle;
    private LinkedList<ContenidoDetalle> listAll;

    public ContenidoDetalleDao() {
        super(ContenidoDetalle.class);
    }

    public ContenidoDetalle getContenidoDetalle() {
        if (this.contenidoDetalle == null) {
            this.contenidoDetalle = new ContenidoDetalle();
        }
        return this.contenidoDetalle;
    }

    public void setContenidoDetalle(ContenidoDetalle contenidoDetalle) {
        this.contenidoDetalle = contenidoDetalle;
    }

    public LinkedList<ContenidoDetalle> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(ContenidoDetalle.class);
        try {
            this.contenidoDetalle.setId(id);
            this.persist(this.contenidoDetalle);
            Contador.actualizarContador(ContenidoDetalle.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el contenidoDetalle: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.contenidoDetalle == null || this.contenidoDetalle.getId() == null) {
            throw new Exception("No se ha seleccionado un contenidoDetalle para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getContenidoDetalleIndex("id", this.contenidoDetalle.getId());
        if (index == -1) {
            throw new Exception("ContenidoDetalle no encontrado.");
        }
        try {
            this.merge(this.contenidoDetalle, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el contenidoDetalle: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.contenidoDetalle == null || this.contenidoDetalle.getId() == null) {
            throw new Exception("No se ha seleccionado un contenidoDetalle para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getContenidoDetalleIndex("id", this.contenidoDetalle.getId());
        if (index == -1) {
            throw new Exception("ContenidoDetalle no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el contenidoDetalle: " + e.getMessage());
        }
    }

    private LinkedList<ContenidoDetalle> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<ContenidoDetalle> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<ContenidoDetalle> trabajadors = new LinkedList<>();
        if (!lista.isEmpty()) {
            ContenidoDetalle[] aux = lista.toArray();
            Integer low = 0;
            Integer high = aux.length - 1;
            Integer mid;
            Integer index = -1;
            String searchValue = value.toString().toLowerCase();
            while (low <= high) {
                mid = (low + high) / 2;

                String midValue = obtenerAttributeValue(aux[mid], attribute).toString().toLowerCase();
                System.out.println("Comparando: " + midValue + " con " + searchValue);

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
                return trabajadors;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                trabajadors.add(aux[i]);
                i++;
            }
        }
        return trabajadors;
    }

    public LinkedList<ContenidoDetalle> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public ContenidoDetalle buscarPor(String attribute, Object value) throws Exception {
        LinkedList<ContenidoDetalle> lista = listAll();
        ContenidoDetalle p = null;

        try {
            if (!lista.isEmpty()) {
                ContenidoDetalle[] trabajadors = lista.toArray();
                for (int i = 0; i < trabajadors.length; i++) {
                    if (obtenerAttributeValue(trabajadors[i], attribute).toString().toLowerCase()
                            .equals(value.toString().toLowerCase())) {
                        p = trabajadors[i];
                        break;
                    }
                }
            }
            if (p == null) {
                throw new Exception("No se encontró el contenidoDetalle con " + attribute + ": " + value);
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar el contenidoDetalle: " + e.getMessage());
        }

        return p;
    }

    private Integer getContenidoDetalleIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            ContenidoDetalle[] trabajadors = this.listAll.toArray();
            for (int i = 0; i < trabajadors.length; i++) {
                if (obtenerAttributeValue(trabajadors[i], attribute).toString().toLowerCase()
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

    public String[] getContenidoDetalleAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : ContenidoDetalle.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
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

        ContenidoDetalle[] contenidoDetalles = this.listAll.toArray();

        for (ContenidoDetalle cd : contenidoDetalles) {
            Object attributeValue = obtenerAttributeValue(cd, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El valor ya existe.");
            }
        }

        return true;
    }

    public LinkedList<ContenidoDetalle> order(String attribute, Integer type) throws Exception {
        LinkedList<ContenidoDetalle> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.contenidoDetalle);
    }

    public ContenidoDetalle getContenidoDetalleById(Integer id) throws Exception {
        return get(id);
    }

    public String getContenidoDetalleJsonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public String getContenidoDetalleJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

    public ContenidoEnum[] getContenidoTypes() {
        return ContenidoEnum.values();
    }

    public ContenidoEnum getContenidoType(String type) {
        return ContenidoEnum.valueOf(type);
    }

}