package com.security.passwordmanager.data;

import android.content.Context;

import androidx.annotation.IntDef;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.security.passwordmanager.Cryptographer;

import java.io.Serializable;

@Entity
public abstract class Data implements Serializable {

    @IntDef(value = {TYPE_WEBSITE, TYPE_BANK_CARD})
    public @interface DataType {}

    public static final int TYPE_WEBSITE = 0;
    public static final int TYPE_BANK_CARD = 1;

    @PrimaryKey(autoGenerate = true)
    private int id;

    public static Data getInstance(@DataType int type) {
        if (type == TYPE_WEBSITE)
            return new Website();
        else
            return new BankCard();
    }

    public abstract @DataType int getType();

    public abstract Data encrypt(Cryptographer cryptographer);

    public abstract Data decrypt(Cryptographer cryptographer);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String toString(Context context, boolean needHeading);

    public boolean isWebsite() {
        return getType() == TYPE_WEBSITE;
    }

    public boolean isBankCard() {
        return getType() == TYPE_BANK_CARD;
    }

    public abstract boolean equals(Data data);

    public abstract boolean compareTo(Data anotherData);
}
