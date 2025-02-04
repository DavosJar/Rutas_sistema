package com.app_rutas.controller.dao.services;

import java.util.HashMap;

import com.app_rutas.controller.dao.ConductorDao;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Conductor;
import com.app_rutas.models.enums.ConductorTurnoEnum;
import com.app_rutas.models.enums.EstadoConductor;
import com.app_rutas.models.enums.Sexo;
import com.app_rutas.models.enums.TipoIdentificacion;

public class ConductorServices {

    @SuppressWarnings("FieldMayBeFinal")
    private ConductorDao obj;

    public ConductorServices() {
        obj = new ConductorDao();
    }

    public Conductor getConductor() {
        return obj.getConductor();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Conductor> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setConductor(Conductor Pprsona) {
        obj.setConductor(Pprsona);
    }

    public Conductor getConductorById(Integer id) throws Exception {
        return obj.getConductorById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public LinkedList<Conductor> getConductoresBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Conductor> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Conductor obtenerConductorPor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public TipoIdentificacion getTipo(String tipo) {
        return obj.getTipo(tipo);
    }

    public TipoIdentificacion[] getTipos() {
        return obj.getTipos();
    }

    public Conductor get(Integer index) throws Exception {
        return obj.get(index);
    }

    public Sexo getSexo(String sexo) {
        return obj.getSexo(sexo);
    }

    public Sexo[] getSexos() {
        return obj.getSexos();
    }

    public EstadoConductor getEstado(String estado) {
        return obj.getEstado(estado);
    }

    public EstadoConductor[] getEstados() {
        return obj.getEstados();
    }

    public ConductorTurnoEnum getTurno(String turno) {
        return obj.getTurno(turno);
    }

    public ConductorTurnoEnum[] getTurnos() {
        return obj.getTurnos();
    }

    public String[] getAttributeList() {
        return obj.getConductorAttributeLists();
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        Boolean unicoTrabajador, unicoConductor;
        if (campo.equals("licenciaConducir")) {
            unicoConductor = obj.isUnique(campo, value);
            unicoTrabajador = true;
        } else {
            unicoTrabajador = new TrabajadorServices().exist(campo, value);
            unicoConductor = obj.isUnique(campo, value);
        }
        return unicoTrabajador && unicoConductor;
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Conductor persona = this.getConductor();
        FieldValidator.validateAndSet(persona, map, field, validations);
    }

}