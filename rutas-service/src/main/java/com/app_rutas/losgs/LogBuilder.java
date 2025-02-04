package com.app_rutas.losgs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogBuilder {
    private Integer id;
    private String description;
    private LogType type;
    private String dateTimestamp;
    private String username;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogBuilder() {
    }

    public LogBuilder(LogType type, String username, String description) {
        this.description = description;
        this.type = type;
        this.dateTimestamp = LocalDateTime.now().format(formatter);
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public String getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(String dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

    public String getUserId() {
        return username;
    }

    public void setUserId(String username) {
        this.username = username;
    }

}
