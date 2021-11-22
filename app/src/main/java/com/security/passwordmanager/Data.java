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

    public Data setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Data setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getNameWebsite() {
        return nameWebsite;
    }

    public Data setNameWebsite(String nameWebsite) {
        this.nameWebsite = nameWebsite;
        return this;
    }

    public String getNameAccount() {
        return nameAccount;
    }

    public Data setNameAccount(String nameAccount) {
        this.nameAccount = nameAccount;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public Data setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Data setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Data setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Data encrypt(Cryptographer cryptographer) {
        setNameAccount(cryptographer.encrypt(nameAccount));
        setLogin(cryptographer.encrypt(login));
        setPassword(cryptographer.encrypt(password));
        return this;
    }

    public Data decrypt(Cryptographer cryptographer) {
        setNameAccount(cryptographer.decrypt(nameAccount));
        setLogin(cryptographer.decrypt(login));
        setPassword(cryptographer.decrypt(password));
        return this;
    }

    /**
     * compare name of website form this object and from data
     * @param anotherData - another object of data
     * @return value < 0 if this string
     *      *          is lexicographically less than the string argument;
     *      *          and a
     *      *          value > 0 if this string is
     *      *          lexicographically greater than the string argument.
     */
    public int compareTo(Data anotherData) {
        return this.getNameWebsite().compareTo(anotherData.getNameWebsite());
    }
}
