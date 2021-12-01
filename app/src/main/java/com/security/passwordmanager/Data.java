package com.security.passwordmanager;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public String toString(Context context, boolean needHeading) {
        final StringBuilder sb = new StringBuilder();
        if (needHeading) {
            sb.append(context.getString(R.string.password_label)).append(": ")
            .append(nameWebsite).append("\n")
            .append(context.getString(R.string.url_address)).append(": ")
            .append(address).append("\n");
        }
        sb.append(context.getString(R.string.account)).append(": ")
                .append(nameAccount).append("\n")
                .append(context.getString(R.string.login)).append(": ")
                .append(login).append("\n")
                .append(context.getString(R.string.password)).append(": ")
                .append(password).append("\n");

        if (comment.length() != 0)
            sb.append(context.getString(R.string.comment)).append(": ")
                    .append(comment).append("\n");

        return sb.toString();
    }
}
