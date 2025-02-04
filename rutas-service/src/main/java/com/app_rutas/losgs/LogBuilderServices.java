package com.app_rutas.losgs;

import com.app_rutas.controller.tda.list.LinkedList;

public class LogBuilderServices {

    private LogBuilderDao obj;

    public LogBuilderServices() {
        obj = new LogBuilderDao();
    }

    public LogBuilder getLogBuilder() {
        return obj.getLogBuilder();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public LinkedList<LogBuilder> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setLogBuilder(LogBuilder logBuilder) {
        obj.setLogBuilder(logBuilder);
    }

    public LogBuilder getById(Integer id) throws Exception {
        return obj.getLogBuilderById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();

    }

    public LinkedList<LogBuilder> getLogBuilderesBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<LogBuilder> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public LogBuilder obtenerLogBuilderPor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public LogType getTipo(String tipo) {
        return obj.getTipo(tipo);
    }

    public LogType[] getTipos() {
        return obj.getTipos();
    }

    public String[] getLogBuilderAttributeLists() {
        return obj.getLogBuilderAttributeLists();
    }

    public Boolean registreLog(LogType type, String username, String description) throws Exception {
        return obj.registreLog(type, username, description);
    }
}