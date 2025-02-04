package com.app_rutas.controller.dao.services;

import java.time.LocalDate;
import java.util.HashMap;

import javax.validation.ValidationException;

import com.app_rutas.controller.dao.TrabajadorDao;
import com.app_rutas.controller.excepcion.ResourceNotFoundException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Trabajador;
import com.app_rutas.models.enums.Sexo;
import com.app_rutas.models.enums.TipoIdentificacion;
import com.app_rutas.utils.PageUtils;

public class TrabajadorServices {

    private TrabajadorDao obj;

    public TrabajadorServices() {
        obj = new TrabajadorDao();
    }

    public Trabajador getTrabajador() {
        return obj.getTrabajador();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Trabajador> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setTrabajador(Trabajador trabajador) {
        obj.setTrabajador(trabajador);
    }

    public Trabajador getById(Integer id) throws Exception {
        return obj.getTrabajadorById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();

    }

    public LinkedList<Trabajador> getTrabajadoresBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Trabajador> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Trabajador obtenerTrabajadorPor(String atributo, Object valor) throws Exception {
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

    public Sexo getSexo(String sexo) {
        return obj.getSexo(sexo);
    }

    public Sexo[] getSexos() {
        return obj.getSexos();
    }

    public String[] getTrabajadorAttributeLists() {
        return obj.getTrabajadorAttributeLists();
    }

    public Boolean exist(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        Boolean unicTrabajador;
        Boolean unicConductor;
        unicTrabajador = this.exist(campo, value);
        ConductorServices cs = new ConductorServices();
        unicConductor = cs.isUnique(campo, value);
        return unicTrabajador && unicConductor;
    }

    public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
        Trabajador trabajador = obj.getTrabajador();
        FieldValidator.validateAndSet(trabajador, map, field, validations);
    }

    public void addTrabajador(HashMap<String, Object> map) throws Exception {
        try{
        this.setTrabajador(new Trabajador());
        this.validateField("nombre", map, "ALPHABETIC", "MIN_LENGTH=3", "MAX_LENGTH=50", "NOT_NULL");
        this.validateField("apellido", map, "ALPHABETIC", "MIN_LENGTH=3", "MAX_LENGTH=50", "NOT_NULL");
        TipoIdentificacion.validateTipoIdentificacion(map.get("tipoIdentificacion").toString());
        this.getTrabajador().setTipoIdentificacion(this.getTipo(map.get("tipoIdentificacion").toString()));
        this.validateField("identificacion", map, "NUMERIC", "MIN_LENGTH=10", "MAX_LENGTH=13", "NOT_NULL",
                "IS_UNIQUE");
        this.validateField("fechaNacimiento", map, "NOT_NULL", "DATE", "MIN_DATE=1900-01-01",
                "MAX_DATE=" + LocalDate.now().minusYears(18).toString());
        this.validateField("direccion", map, "ALPHANUMERIC", "MIN_LENGTH=5", "MAX_LENGTH=100", "NOT_NULL");
        this.validateField("telefono", map, "NUMERIC", "MIN_LENGTH=7", "MAX_LENGTH=10", "NOT_NULL");
        this.validateField("email", map, "VALID_EMAIL", "MIN_LENGTH=5", "MAX_LENGTH=50", "NOT_NULL", "IS_UNIQUE");
        Sexo.validateSexo(map.get("sexo").toString());
        this.getTrabajador().setSexo(Sexo.valueOf(map.get("sexo").toString()));
        this.setTrabajador(getTrabajador());
        this.save();
    } catch (ValidationException e) {
        throw new ValidationException("Error en la validacion de los campos:" + e.getMessage());
        }
    }

    public void updateTrabajador(HashMap<String, Object> map) throws Exception {
        if (map.get("id") == null) {
            throw new ValidationException("El id es requerido");
        }
        Trabajador trabajador = this.getById((Integer) map.get("id"));
        if (trabajador == null) {
            throw new ResourceNotFoundException("No se encontro el trabajador con id: " + map.get("id"));
        }
        this.setTrabajador(trabajador);
        validateIfChanged("nombre", map, trabajador.getNombre(),
                "ALPHABETIC", "MIN_LENGTH=3", "MAX_LENGTH=50", "NOT_NULL");
        validateIfChanged("apellido", map, trabajador.getApellido(),
                "ALPHABETIC", "MIN_LENGTH=3", "MAX_LENGTH=50", "NOT_NULL");
        if (map.get("tipoIdentificacion") != null
                && !getTipo(map.get("tipoIdentificacion").toString())
                        .equals(trabajador.getTipoIdentificacion())) {
            TipoIdentificacion.validateTipoIdentificacion(getTipo(map.get("tipoIdentificacion").toString()).toString());
            getTrabajador().setTipoIdentificacion(getTipo(map.get("tipoIdentificacion").toString()));
        }
        validateIfChanged("identificacion", map, trabajador.getIdentificacion(),
                "NUMERIC", "MIN_LENGTH=10", "MAX_LENGTH=13", "NOT_NULL", "IS_UNIQUE");
        validateIfChanged("fechaNacimiento", map, trabajador.getFechaNacimiento().toString(),
                "NOT_NULL", "DATE", "MIN_DATE=1900-01-01",
                "MAX_DATE=" + LocalDate.now().minusYears(18).toString());
        validateIfChanged("direccion", map, trabajador.getDireccion(),
                "ALPHANUMERIC", "MIN_LENGTH=5", "MAX_LENGTH=100", "NOT_NULL");
        validateIfChanged("telefono", map, trabajador.getTelefono(),
                "NUMERIC", "MIN_LENGTH=7", "MAX_LENGTH=10", "NOT_NULL");
        validateIfChanged("email", map, trabajador.getEmail(),
                "VALID_EMAIL", "MIN_LENGTH=5", "MAX_LENGTH=50", "NOT_NULL", "IS_UNIQUE");
        if (map.get("sexo") != null && !getSexo(map.get("sexo").toString()).equals(trabajador.getSexo())) {
            Sexo.validateSexo(getSexo(map.get("sexo").toString()).toString());
            getTrabajador().setSexo(getSexo(map.get("sexo").toString()));
        }
        this.update();
    }

    private void validateIfChanged(String field, HashMap<String, Object> map, Object newValue, String... validations)
            throws Exception {
        if (map.get(field) != null && !map.get(field).equals(newValue)) {
            this.validateField(field, map, validations);
        }
    }

    public Trabajador[] listInPage(LinkedList<Trabajador> list, Integer page, Integer size) {
        return (Trabajador[]) PageUtils.listInPages(list, page, size);
    }
}