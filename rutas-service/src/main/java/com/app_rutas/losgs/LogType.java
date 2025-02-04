package com.app_rutas.losgs;

public enum LogType {
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR");

    private String type;

    LogType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static LogType fromString(String text) {
        for (LogType b : LogType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
