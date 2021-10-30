package com.security.passwordmanager;

import java.util.UUID;

public class Data {

    private UUID id;
    private String address;
    private String name;
    private String login;
    private String password;
    private String comment;

    public Data(UUID id, String url, String name, String login, String password, String comment) {
        setId(id);
        setAddress(url);
        setName(name);
        setLogin(login);
        setPassword(password);
        setComment(comment);
    }

    public Data() {
        this(UUID.randomUUID());
    }

    public Data(UUID id) {
        this(id, "", "", "", "", "");
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean equals(Data data) {
        return address.equals(data.address) || name.equals(data.name);
    }
}
