package com.security.passwordmanager;

import java.io.Serializable;
import java.util.UUID;

public class Data implements Serializable {

    private UUID id;
    private String address;
    private String nameWebsite;
    private String nameAccount;
    private String login;
    private String password;
    private String comment;

    public Data(UUID id, String url, String nameWebsite, String nameAccount, String login, String password, String comment) {
        setId(id);
        setAddress(url);
        setNameWebsite(nameWebsite);
        setNameAccount(nameAccount);
        setLogin(login);
        setPassword(password);
        setComment(comment);
    }

    public Data() {
        this(UUID.randomUUID());
    }

    public Data(UUID id) {
        this(id, "", "", "", "", "", "");
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

    public String getNameWebsite() {
        return nameWebsite;
    }

    public void setNameWebsite(String nameWebsite) {
        this.nameWebsite = nameWebsite;
    }

    public String getNameAccount() {
        return nameAccount;
    }

    public void setNameAccount(String nameAccount) {
        this.nameAccount = nameAccount;
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
        return address.equals(data.address) || nameWebsite.equals(data.nameWebsite);
    }
}
