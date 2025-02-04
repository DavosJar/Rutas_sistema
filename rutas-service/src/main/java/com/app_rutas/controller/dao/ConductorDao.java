package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Conductor;
import com.app_rutas.models.enums.ConductorTurnoEnum;
import com.app_rutas.models.enums.EstadoConductor;
import com.app_rutas.models.enums.Sexo;
import com.app_rutas.models.enums.TipoIdentificacion;
import com.google.gson.Gson;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class ConductorDao extends AdapterDao<Conductor> {
    private Conductor conductor;
    private LinkedList<Conductor> listAll;

    public ConductorDao() {
        super(Conductor.class);
    }

    public Conductor getConductor() {
        if (this.conductor == null) {
            this.conductor = new Conductor();
        }
        return this.conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public LinkedList<Conductor> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Conductor.class.getSuperclass());
        try {
            this.conductor.setId(id);
            this.persist(this.conductor);
            Contador.actualizarContador(Conductor.class.getSuperclass());
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el conductor: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.conductor == null || this.conductor.getId() == null) {
            throw new Exception("No se ha seleccionado un conductor para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getConductorIndex("id", this.conductor.getId());
        if (index == -1) {
            throw new Exception("Conductor no encontrado.");
        }
        try {
            this.merge(this.conductor, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el conductor: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.conductor == null || this.conductor.getId() == null) {
            throw new Exception("No se ha seleccionado un conductor para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getConductorIndex("id", this.conductor.getId());
        if (index == -1) {
            throw new Exception("Conductor no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el conductor: " + e.getMessage());
        }
    }

    private LinkedList<Conductor> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Conductor> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Conductor> conductors = new LinkedList<>();
        if (!lista.isEmpty()) {
            Conductor[] aux = lista.toArray();
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
                return conductors;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                conductors.add(aux[i]);
                System.out.println("Agregando: " + aux[i].getNombre());
                i++;
            }
        }
        return conductors;
    }

    public LinkedList<Conductor> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Conductor buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Conductor> lista = listAll();
        Conductor p = null;

        if (!lista.isEmpty()) {
            Conductor[] conductors = lista.toArray();
            for (int i = 0; i < conductors.length; i++) {
                if (obtenerAttributeValue(conductors[i], attribute).toString().toLowerCase()
                        .equals(value.toString().toLowerCase())) {
                    p = conductors[i];
                    break;
                }
            }
        }
        return p;
    }

    private Integer getConductorIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Conductor[] conductors = this.listAll.toArray();
            for (int i = 0; i < conductors.length; i++) {
                if (obtenerAttributeValue(conductors[i], attribute).toString().toLowerCase()
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

    public String[] getConductorAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : Conductor.class.getDeclaredMethods()) {
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

        Conductor[] conductores = this.listAll.toArray();

        for (Conductor conductor : conductores) {
            Object attributeValue = obtenerAttributeValue(conductor, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("Ya existe un conductor con el valor: " + value);
            }
        }

        return true;
    }

    public LinkedList<Conductor> order(String attribute, Integer type) throws Exception {
        LinkedList<Conductor> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.conductor);
    }

    public Conductor getConductorById(Integer id) throws Exception {
        return get(id);
    }

    public String getConductorJasonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public TipoIdentificacion getTipo(String tipo) {
        return TipoIdentificacion.valueOf(tipo);
    }

    public TipoIdentificacion[] getTipos() {
        return TipoIdentificacion.values();
    }

    public Sexo getSexo(String sexo) {
        return Sexo.valueOf(sexo);
    }

    public Sexo[] getSexos() {
        return Sexo.values();
    }

    public EstadoConductor getEstado(String estado) {
        return EstadoConductor.valueOf(estado);
    }

    public EstadoConductor[] getEstados() {
        return EstadoConductor.values();
    }

    public ConductorTurnoEnum getTurno(String turno) {
        return ConductorTurnoEnum.valueOf(turno);
    }

    public ConductorTurnoEnum[] getTurnos() {
        return ConductorTurnoEnum.values();
    }

    public String getConductorJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

}