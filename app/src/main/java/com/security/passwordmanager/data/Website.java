package com.security.passwordmanager.data;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.security.passwordmanager.Cryptographer;
import com.security.passwordmanager.R;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "WebsiteTable")
public class Website extends Data {

    private String address;
    private String nameWebsite;
    private String nameAccount;
    private String login;
    private String password;
    private String comment;

    @Override
    public int getType() {
        return TYPE_WEBSITE;
    }

    public Website(int id, String address, String nameWebsite,
                   String nameAccount, String login, String password, String comment) {
        setId(id);
        setAddress(address);
        setNameWebsite(nameWebsite);
        setNameAccount(nameAccount);
        setLogin(login);
        setPassword(password);
        setComment(comment);
    }

    @Ignore
    public Website() {
        this(0);
    }

    @Ignore
    public Website(int id) {
        this(id, "", "", "", "", "", "");
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

    @Override
    public Website encrypt(Cryptographer cryptographer) {
        setLogin(cryptographer.encrypt(login));
        setPassword(cryptographer.encrypt(password));
        return this;
    }

    @Override
    public Website decrypt(Cryptographer cryptographer) {
        setLogin(cryptographer.decrypt(login));
        setPassword(cryptographer.decrypt(password));
        return this;
    }

    @NotNull
    @Override
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

    @Override
    public boolean equals(Data data) {
        return getAddress().equals(((Website) data).getAddress());
    }

    @Override
    public boolean compareTo(Data anotherData) {
        String anotherString;

        if (anotherData.isWebsite())
            anotherString = ((Website) anotherData).getNameWebsite();
        else
            anotherString = ((BankCard) anotherData).getBankName();

        return getNameWebsite().compareTo(anotherString) > 0;
    }
}
