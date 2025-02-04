package com.app_rutas.controller.dao;

import com.app_rutas.controller.dao.implement.AdapterDao;
import com.app_rutas.controller.dao.implement.Contador;
import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.tda.list.LinkedList;
import com.app_rutas.models.Cuenta;
import com.app_rutas.models.JwtUtil;
import com.app_rutas.models.enums.Rol;
import com.app_rutas.models.enums.TipoIdentificacion;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.Gson;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class CuentaDao extends AdapterDao<Cuenta> {
    private Cuenta cuenta;
    private LinkedList<Cuenta> listAll;

    public CuentaDao() {
        super(Cuenta.class);
    }

    public Cuenta getCuenta() {
        if (this.cuenta == null) {
            this.cuenta = new Cuenta();
        }
        return this.cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public LinkedList<Cuenta> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Cuenta.class);
        try {
            this.cuenta.setId(id);
            this.persist(this.cuenta);
            Contador.actualizarContador(Cuenta.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el cuenta: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.cuenta == null || this.cuenta.getId() == null) {
            throw new Exception("No se ha seleccionado un cuenta para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getCuentaIndex("id", this.cuenta.getId());
        if (index == -1) {
            throw new Exception("Cuenta no encontrado.");
        }
        try {
            this.merge(this.cuenta, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el cuenta: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.cuenta == null || this.cuenta.getId() == null) {
            throw new Exception("No se ha seleccionado un cuenta para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getCuentaIndex("id", this.cuenta.getId());
        if (index == -1) {
            throw new Exception("Cuenta no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el cuenta: " + e.getMessage());
        }
    }

    private LinkedList<Cuenta> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Cuenta> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Cuenta> cuentas = new LinkedList<>();
        if (!lista.isEmpty()) {
            Cuenta[] aux = lista.toArray();
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
                return cuentas;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                cuentas.add(aux[i]);
                i++;
            }
        }
        return cuentas;
    }

    public LinkedList<Cuenta> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Cuenta buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Cuenta> lista = listAll();
        Cuenta p = null;

        try {
            if (!lista.isEmpty()) {
                Cuenta[] cuentas = lista.toArray();
                for (int i = 0; i < cuentas.length; i++) {
                    if (obtenerAttributeValue(cuentas[i], attribute).toString().toLowerCase()
                            .equals(value.toString().toLowerCase())) {
                        p = cuentas[i];
                        break;
                    }
                }
            }
            if (p == null) {
                throw new Exception("No se encontró el cuenta con " + attribute + ": " + value);
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar el cuenta: " + e.getMessage());
        }

        return p;
    }

    private Integer getCuentaIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Cuenta[] cuentas = this.listAll.toArray();
            for (int i = 0; i < cuentas.length; i++) {
                if (obtenerAttributeValue(cuentas[i], attribute).toString().toLowerCase()
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

    public String[] getCuentaAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : Cuenta.class.getDeclaredMethods()) {
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

        Cuenta[] Cuentaes = this.listAll.toArray();

        for (Cuenta cuenta : Cuentaes) {
            Object attributeValue = obtenerAttributeValue(cuenta, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El valor ya existe.");
            }
        }

        return true;
    }

    public LinkedList<Cuenta> order(String attribute, Integer type) throws Exception {
        LinkedList<Cuenta> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.cuenta);
    }

    public Cuenta getCuentaById(Integer id) throws Exception {
        return get(id);
    }

    public String getCuentaJsonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public TipoIdentificacion getTipo(String tipo) {
        return TipoIdentificacion.valueOf(tipo);
    }

    public TipoIdentificacion[] getTipos() {
        return TipoIdentificacion.values();
    }

    public String getCuentaJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

    public Rol getRol(String rol) {
        return Rol.valueOf(rol);
    }

    public Rol[] getRolList() {
        return Rol.values();
    }

    // Método para autenticar un usuario
    public String authenticate(String username, String password) throws Exception {
        LinkedList<Cuenta> cuentas = buscar("username", username);

        if (cuentas.isEmpty()) {
            throw new Exception("Usuario no encontrado.");
        }

        Cuenta cuenta = cuentas.get(0);

        // Verifica la contraseña
        if (!cuenta.getPassword().equals(password)) {
            throw new Exception("Contraseña incorrecta.");
        }

        // Genera el token usando JwtUtil
        String token = JwtUtil.generateToken(cuenta.getUsername(), cuenta.getRol());
        return token;
    }

    public void validarToken(String authHeader) throws JWTVerificationException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JWTVerificationException("Token no proporcionado o inválido.");
        }
        String token = authHeader.substring("Bearer ".length());
        JwtUtil.validateToken(token);
    }
}