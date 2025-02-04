package com.app_rutas.utils;

public class StringFormat {
    public static String camellCaseToNatural(String text) {
        String[] words = text.split("(?=[A-Z])");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word).append(" ");
        }
        return result.toString().trim();
    }

    public static String naturalToCamellCase(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static String snakeCaseToCamellCase(String text) {
        String[] words = text.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    public static String camellCaseToSnakeCase(String text) {
        String[] words = text.split("(?=[A-Z])");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word.toLowerCase()).append("_");
        }
        return result.toString().substring(0, result.length() - 1);
    }

}
