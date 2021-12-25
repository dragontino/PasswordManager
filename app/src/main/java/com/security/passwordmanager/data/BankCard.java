package com.security.passwordmanager.data;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.security.passwordmanager.Cryptographer;
import com.security.passwordmanager.R;

@Entity(tableName = "BankTable")
public class BankCard extends Data {

    private String bankName;
    private String cardNumber;
    private String cardHolder;
    private String validity;
    private int cvv;
    private int pin;

    @Override
    public int getType() {
        return TYPE_BANK_CARD;
    }

    @Ignore
    public BankCard() {
        this(0);
    }

    @Ignore
    public BankCard(int id) {
        this(id, "", "", "", "", 0, 0);
    }

    public BankCard(int id, String bankName, String cardNumber, String cardHolder, String validity, int cvv, int pin) {
        setId(id);
        setBankName(bankName);
        setCardNumber(cardNumber);
        setCardHolder(cardHolder);
        setValidity(validity);
        setCvv(cvv);
        setPin(pin);
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public int getCvv() {
        return cvv;
    }

    public String getStringCvv() {
        return String.valueOf(getCvv());
    }

    public void setCvv(String cvv) {
        this.cvv = Integer.parseInt(cvv);
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getPin() {
        return pin;
    }

    public String getStringPin() {
        return String.valueOf(getPin());
    }

    public void setPin(String pin) {
        this.pin = Integer.parseInt(pin);
    }

    @Override
    public BankCard encrypt(Cryptographer cryptographer) {
        setCardNumber(cryptographer.encrypt(cardNumber));
        setCardHolder(cryptographer.encrypt(cardHolder));
        setValidity(cryptographer.encrypt(validity));
        setCvv(cryptographer.encrypt(getStringCvv()));
        setPin(cryptographer.encrypt(getStringPin()));
        return this;
    }

    @Override
    public BankCard decrypt(Cryptographer cryptographer) {
        setCardNumber(cryptographer.decrypt(cardNumber));
        setCardHolder(cryptographer.decrypt(cardHolder));
        setValidity(cryptographer.decrypt(validity));
        setCvv(cryptographer.decrypt(getStringCvv()));
        setPin(cryptographer.decrypt(getStringPin()));
        return this;
    }

    @Override
    public String toString(Context context, boolean needHeading) {
        final StringBuilder sb = new StringBuilder();
        if (needHeading) {
            sb.append(context.getString(R.string.bank_name)).append(": ")
                    .append(bankName).append("\n");
        }

        sb.append(context.getString(R.string.card_number)).append(": ")
                .append(cardNumber).append("\n")
                .append(context.getString(R.string.card_holder)).append(": ")
                .append(cardHolder).append("\n")
                .append(context.getString(R.string.validity_period)).append(": ")
                .append(validity).append("\n")
                .append(context.getString(R.string.card_cvv)).append(": ")
                .append(cvv).append("\n")
                .append(context.getString(R.string.pin_code)).append(": ")
                .append(pin).append("\n");

        return sb.toString();
    }

    @Override
    public boolean equals(Data data) {
        return getBankName().equals(((BankCard) data).getBankName());
    }

    //сравнение 2 объектов BankCard по bankName
    //или объекта BankCard с Website
    @Override
    public boolean compareTo(Data anotherData) {
        String anotherString;

        if (anotherData.isBankCard())
            anotherString = ((BankCard) anotherData).getBankName();
        else
            anotherString = ((Website) anotherData).getNameWebsite();

        return this.getBankName().compareTo(anotherString) > 0;
    }
}
