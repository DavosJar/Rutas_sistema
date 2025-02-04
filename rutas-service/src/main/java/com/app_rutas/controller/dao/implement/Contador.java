package com.app_rutas.controller.dao.implement;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;

public class Contador {

    private static final String COUNTER_FILE = "media/counters.json";
    private static HashMap<String, Integer> contadores = new HashMap<>();

    private static String leerContadores() {
        try {
            Scanner scanner = new Scanner(new FileReader(COUNTER_FILE));
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();
            return sb.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    private static void guardarContadores() {
        try {
            FileWriter writer = new FileWriter(COUNTER_FILE);
            String json = "{";
            for (String key : contadores.keySet()) {
                json += "\"" + key + "\": " + contadores.get(key) + ",";
            }
            if (!contadores.isEmpty()) {
                json = json.substring(0, json.length() - 1);
            }
            json += "}";
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int obtenerValorActual(Class<?> clazz) {
        sincronizarContadores();
        String className = clazz.getSimpleName();
        return contadores.getOrDefault(className, 1);
    }

    public static void actualizarContador(Class<?> clazz) {
        String className = clazz.getSimpleName();

        int nuevoValor = contadores.getOrDefault(className, 1);
        contadores.put(className, nuevoValor + 1);

        guardarContadores();
    }

    private static void sincronizarContadores() {
        if (contadores.isEmpty()) {
            String json = leerContadores();
            if (!json.equals("{}")) {
                String jsonWithoutBrackets = json.substring(1, json.length() - 1);
                String[] pairs = jsonWithoutBrackets.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    contadores.put(keyValue[0].replace("\"", "").trim(), Integer.parseInt(keyValue[1].trim()));
                }
            }
        }
    }
}