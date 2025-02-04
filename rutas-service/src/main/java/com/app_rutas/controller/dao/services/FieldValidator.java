package com.app_rutas.controller.dao.services;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


import com.app_rutas.controller.excepcion.ValueAlreadyExistException;
import com.app_rutas.controller.validations.Validator;

public class FieldValidator {

    private static String SERVICE_URL = "com.app_rutas.controller.dao.services.";

    /*
     * @param target: Objeto que invoca el método.
     * 
     * @param map: Mapa con los campos y valores a validar.
     * 
     * @param field: Campo a validar.
     * 
     * @param validations: Validaciones a aplicar.
     * 
     * @throws Exception: Lanza una excepción si el campo no existe en la clase.
     * Funcionalidad:
     * El metodo validateAndSet se encarga de validar los campos de un objeto y
     * asignarles un valor.
     * El metodo recibe un objeto, un mapa con los campos y valores a validar, el
     * campo a validar y las validaciones a aplicar.
     * El metodo valida si el campo es nulo, si es obligatorio y si es unico.
     * Si el campo es un String, se valida que cumpla con las validaciones
     * especificadas.
     */
    public static void validateAndSet(Object target, HashMap<String, Object> map, String field, String... validations)
            throws Exception {
        Object value = map.get(field);

        if (value == null) {
            for (String validation : validations) {
                if ("NOT_NULL".equals(validation)) {
                    throw new IllegalArgumentException("El campo '" + field + "' es obligatorio.");
                }
            }
        }

        /*
         * La verificacion de unicidad tiene un origen distinto a las validaciones
         * estandar por tanto debe ejecutarse
         * en un bloque de código separado.
         */
        if (contains(validations, "DATE")) {
            for (String validation : validations) {
                Validator.validDateField((String) value, validation);
            }
        } else if (contains(validations, "PASSWORD")) {
            Validator.validPassword(value);

        } else {
            for (String validation : validations) {
                if ("IS_UNIQUE".equals(validation)) {
                    if (!checkUniqueness(target, field, value)) {
                        throw new ValueAlreadyExistException(
                                "El valor del campo '" + field + "' (" + value + ") ya existe.");
                    }
                } else if (value instanceof String) {
                    Validator.validateStringField((String) value, validation);
                } else if (value instanceof Number) {
                    Validator.validNumberField((Number) value, validation);
                }
            }

        }
        try {
            Field fieldObj = findFieldInSuperClass(target.getClass(), field);
            // Asignación del valor al campo
            fieldObj.setAccessible(true);
            if (value instanceof String) {
                fieldObj.set(target, value);
            } else if (value instanceof Number) {
                if (value instanceof Integer) {
                    fieldObj.set(target, (Integer) value);
                } else if (value instanceof Long) {
                    fieldObj.set(target, (Long) value);
                } else if (value instanceof Double) {
                    fieldObj.set(target, (Double) value);
                } else {
                    // Si el tipo no es uno de los soportados, lanzamos una excepción
                    throw new IllegalArgumentException(
                            "Tipo de dato no soportado para el campo: " + fieldObj.getType().getName());
                }
            }
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException("El campo '" + field + "' no existe en la clase.");
        }
    }

    private static Field findFieldInSuperClass(Class<?> clazz, String field) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(field);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("El campo '" + field + "' no existe en la clase ni en sus superclases.");
    }
    /*
     * La verificacion de unicida se encuantra en una funcin dentro de cada service
     * checkUniqueness construye la clase service correspondiente a partir del
     * objeto recibido al
     * llamarla en el servicse ejemplo Persona + Service para el objeto
     * PersonaService
     * El metodo isUnique se encarga de verificar si el valor ya existe en los
     * archivos
     * 
     * @param target: Objeto que invoca el método.
     * 
     * @param field: Campo a validar.
     * 
     * @param value: Valor a validar.
     */

    private static Boolean checkUniqueness(Object target, String field, Object value) throws Exception {
        try {
            // Construccion del nombre correcto del service, toda Clase tiene su
            // ClaseServices
            String targetClassName = target.getClass().getSimpleName();
            String serviceClassName = targetClassName + "Services";
            Class<?> serviceClass = Class.forName(SERVICE_URL + serviceClassName);

            Object service = serviceClass.getDeclaredConstructor().newInstance();
            Method isUniqueMethod = service.getClass().getMethod("isUnique", String.class, Object.class);
            // Invocacion del metodo isUnique, debe estar en presente en cada service donde
            // se requiera verificar unicidad
            boolean isUnique = (boolean) isUniqueMethod.invoke(service, field, value);

            return isUnique;

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ValueAlreadyExistException) {
                e.printStackTrace();
                return false;
            }
            throw new Exception("Error al invocar el método isUnique: " + cause.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private static boolean contains(String[] types, String type) {
        for (String t : types) {
            if (t.equals(type)) {
                return true;
            }
        }
        return false;
    }
}