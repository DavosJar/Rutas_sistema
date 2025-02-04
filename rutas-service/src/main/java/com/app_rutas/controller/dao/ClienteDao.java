package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Cliente;
import com.google.gson.Gson;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class ClienteDao extends AdapterDao<Cliente> {
    private Cliente cliente;
    private LinkedList<Cliente> listAll;

    public ClienteDao() {
        super(Cliente.class);
    }

    public Cliente getCliente() {
        if (this.cliente == null) {
            this.cliente = new Cliente();
        }
        return this.cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LinkedList<Cliente> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Cliente.class.getSuperclass());
        try {
            this.cliente.setId(id);
            this.persist(this.cliente);
            Contador.actualizarContador(Cliente.class.getSuperclass());
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el cliente: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.cliente == null || this.cliente.getId() == null) {
            throw new Exception("No se ha seleccionado un cliente para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getClienteIndex("id", this.cliente.getId());
        if (index == -1) {
            throw new Exception("Cliente no encontrado.");
        }
        try {
            this.merge(this.cliente, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el cliente: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.cliente == null || this.cliente.getId() == null) {
            throw new Exception("No se ha seleccionado un cliente para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getClienteIndex("id", this.cliente.getId());
        if (index == -1) {
            throw new Exception("Cliente no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el cliente: " + e.getMessage());
        }
    }

    private LinkedList<Cliente> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Cliente> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Cliente> clientes = new LinkedList<>();
        if (!lista.isEmpty()) {
            Cliente[] aux = lista.toArray();
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
                return clientes;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                clientes.add(aux[i]);
                i++;
            }
        }
        return clientes;
    }

    public LinkedList<Cliente> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Cliente buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Cliente> lista = listAll();
        Cliente p = null;

        if (!lista.isEmpty()) {
            Cliente[] clientes = lista.toArray();
            for (int i = 0; i < clientes.length; i++) {
                if (obtenerAttributeValue(clientes[i], attribute).toString().toLowerCase()
                        .equals(value.toString().toLowerCase())) {
                    p = clientes[i];
                    break;
                }
            }
        }
        return p;
    }

    private Integer getClienteIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Cliente[] clientes = this.listAll.toArray();
            for (int i = 0; i < clientes.length; i++) {
                if (obtenerAttributeValue(clientes[i], attribute).toString().toLowerCase()
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

    public String[] getClienteAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : Cliente.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
    }

    public LinkedList<Cliente> order(String attribute, Integer type) throws Exception {
        LinkedList<Cliente> lista = listAll();
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

        Cliente[] clientes = this.listAll.toArray();

        for (Cliente trabajador : clientes) {
            Object attributeValue = obtenerAttributeValue(trabajador, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El valor ya existe.");
            }
        }

        return true;
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.cliente);
    }

    public Cliente getClienteById(Integer id) throws Exception {
        return get(id);
    }

    public String getClienteJasonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public String getClienteJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

}