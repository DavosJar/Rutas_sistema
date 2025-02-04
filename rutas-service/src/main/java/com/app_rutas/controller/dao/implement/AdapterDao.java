package com.app_rutas.controller.dao.implement;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.app_rutas.controller.tda.list.LinkedList;
import com.google.gson.Gson;

//clase AdapterDao
@SuppressWarnings({ "rawtypes", "unchecked", "ConvertToTryWithResources" })
public class AdapterDao<T> implements InterfazDao<T> {

    private Class<T> clazz;
    private Gson g = new Gson();

    // Ruta de los archivos
    public static String URL = "media/";

    public AdapterDao(Class clazz) {
        this.clazz = clazz;
        this.g = new Gson();
    }

    @Override

    public void persist(T object) throws Exception {
        LinkedList<T> list = listAll();
        list.add(object);
        String info = g.toJson(list.toArray());
        saveFile(info);
    }

    @Override

    public void merge(T object, Integer index) throws Exception {
        LinkedList<T> list = listAll();
        if (index < 0 || index >= list.getSize()) {
            throw new IllegalArgumentException("Índice fuera de los límites de la lista.");
        }

        list.set(index, object);

        String info = g.toJson(list.toArray());
        saveFile(info);

    }

    @Override
    public LinkedList listAll() {
        LinkedList list = new LinkedList<>();
        try {
            String data = readFile();
            T[] matrix = (T[]) g.fromJson(data, java.lang.reflect.Array.newInstance(clazz, 0).getClass());
            list.toList(matrix);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public T get(Integer id) throws Exception {
        LinkedList<T> list = listAll();

        if (list.isEmpty()) {
            return null;
        }

        T[] matrix = list.toArray();

        Integer minId = getIdent(matrix[0]);
        Integer maxId = getIdent(matrix[matrix.length - 1]);
        if (id < minId || id > maxId) {
            return null;
        }

        int inicio = 0;
        int fin = matrix.length - 1;

        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            T candidato = matrix[medio];
            Integer ident = getIdent(candidato);

            if (ident.equals(id)) {
                return candidato;
            } else if (ident < id) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }

        return null;
    }

    private Integer getIdent(T obj) throws Exception {
        try {
            Method method = null;
            for (Method m : clazz.getMethods()) {
                if (m.getName().equalsIgnoreCase("getId")) {
                    method = m;
                    break;
                }
            }
            if (method == null) {
                for (Method m : clazz.getSuperclass().getMethods()) {
                    if (m.getName().equalsIgnoreCase("getId")) {
                        method = m;
                        break;
                    }
                }
            }
            if (method != null) {
                return (Integer) method.invoke(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    @Override
    public void delete(Integer index) throws Exception {
        LinkedList list = listAll();
        list.delete(index);
        String data = g.toJson(list.toArray());
        saveFile(data);
    }

    private String readFile() throws Exception {
        // Usando UTF-8 para leer el archivo
        Scanner in = new Scanner(new FileReader(URL + clazz.getSimpleName() + ".json", StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        while (in.hasNextLine()) {
            sb.append(in.nextLine());
        }
        in.close();
        return sb.toString();
    }

    private void saveFile(String data) throws Exception {
        // Usando UTF-8 para guardar el archivo
        FileWriter f = new FileWriter(URL + clazz.getSimpleName() + ".json", StandardCharsets.UTF_8);
        f.write(data);
        f.flush();
        f.close();
    }

}
