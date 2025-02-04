package com.app_rutas.losgs;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.tda.list.LinkedList;
import com.google.gson.Gson;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class LogBuilderDao extends AdapterDao<LogBuilder> {
    private LogBuilder logBuilder;
    private LinkedList<LogBuilder> listAll;

    public LogBuilderDao() {
        super(LogBuilder.class);
    }

    public LogBuilder getLogBuilder() {
        if (this.logBuilder == null) {
            this.logBuilder = new LogBuilder();
        }
        return this.logBuilder;
    }

    public void setLogBuilder(LogBuilder logBuilder) {
        this.logBuilder = logBuilder;
    }

    public LinkedList<LogBuilder> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(LogBuilder.class);
        try {
            this.logBuilder.setId(id);
            this.persist(this.logBuilder);
            Contador.actualizarContador(LogBuilder.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el logBuilder: " + e.getMessage());
        }
    }

    private LinkedList<LogBuilder> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<LogBuilder> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<LogBuilder> logBuilders = new LinkedList<>();
        if (!lista.isEmpty()) {
            LogBuilder[] aux = lista.toArray();
            Integer low = 0;
            Integer high = aux.length - 1;
            Integer mid;
            Integer index = -1;
            String searchValue = value.toString().toLowerCase();
            while (low <= high) {
                mid = (low + high) / 2;

                String midValue = obtenerAttributeValue(aux[mid], attribute).toString().toLowerCase();

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
                return logBuilders;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                logBuilders.add(aux[i]);
                i++;
            }
        }
        return logBuilders;
    }

    public LinkedList<LogBuilder> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public LogBuilder buscarPor(String attribute, Object value) throws Exception {
        LinkedList<LogBuilder> lista = listAll();
        LogBuilder p = null;

        try {
            if (!lista.isEmpty()) {
                LogBuilder[] logBuilders = lista.toArray();
                for (int i = 0; i < logBuilders.length; i++) {
                    if (obtenerAttributeValue(logBuilders[i], attribute).toString().toLowerCase()
                            .equals(value.toString().toLowerCase())) {
                        p = logBuilders[i];
                        break;
                    }
                }
            }
            if (p == null) {
                throw new Exception("No se encontró el logBuilder con " + attribute + ": " + value);
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar el logBuilder: " + e.getMessage());
        }

        return p;
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

    public String[] getLogBuilderAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : LogBuilder.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
    }

    public LinkedList<LogBuilder> order(String attribute, Integer type) throws Exception {
        LinkedList<LogBuilder> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.logBuilder);
    }

    public LogBuilder getLogBuilderById(Integer id) throws Exception {
        return get(id);
    }

    public String getLogBuilderJsonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public LogType getTipo(String tipo) {
        return LogType.valueOf(tipo);
    }

    public LogType[] getTipos() {
        return LogType.values();
    }

    public String getLogBuilderJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

    public Boolean registreLog(LogType type, String username, String description) throws Exception {
        LogBuilder logBuilder = new LogBuilder(type, username, description);
        this.setLogBuilder(logBuilder);
        return this.save();
    }

}