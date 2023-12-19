package com.ognjen.main.client;

import com.ognjen.main.server.Server;

import java.io.Serializable;

// Util class used to form the "container" of information to send back to server.

public class DataContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type; // login / register
    private String name;
    private String password;
    private Server server;

    public DataContainer(String type, String name, String password, Server server) {
        this.type = type;
        this.name = name;
        this.password = password;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", server=" + server +
                '}';
    }
}