package com.security.passwordmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;

import androidx.annotation.Nullable;

public class Cryptographer {

    private final Context mContext;

    private static final char EXTRA_LETTER = (char) 198;
    private static final int SIZE = 13;
    private static final char [][] keyTable = new char[SIZE][SIZE];

    public Cryptographer(Context context) {
        this.mContext = context;
        fillData();
    }


    public String encrypt(String defaultString) {
        return crypt(defaultString, 1);
    }


    public String decrypt(String defaultString) {
        return crypt(defaultString, -1);
    }


    private String crypt(String defaultString, int next) {
        StringBuilder cryptString = new StringBuilder(defaultString);

        int elem = 0;
        while (elem < cryptString.length()) {

            char current = cryptString.charAt(elem);
            char future;
            if (elem + 1 >= cryptString.length())
                future = ' ';
            else
                future = cryptString.charAt(elem + 1);

            if (next == 1) {
                if (cryptString.length() % 2 != 0 && elem == cryptString.length() - 1)
                    cryptString.append(EXTRA_LETTER);
                else if (current == cryptString.charAt(elem + 1))
                    cryptString.insert(elem + 1, EXTRA_LETTER);

                future = cryptString.charAt(elem + 1);
            }

            Pair<Integer, Integer> first = getIndex(
                    String.valueOf(current));
            Pair<Integer, Integer> second = getIndex(
                    String.valueOf(future));

            if (first == null || second == null)
                return "";

            if (first.equals(second, 0)) {
                first.setSecond(mod(
                        first.second() + next,
                        SIZE
                ));
                second.setSecond(mod(
                        second.second() + next,
                        SIZE
                ));
            }
            else if (first.equals(second, 1)) {
                first.setFirst(mod(
                        first.first() + next,
                        SIZE
                ));
                second.setFirst(mod(
                        second.first() + next,
                        SIZE));
            }
            else {
                int buff = first.second();
                first.setSecond(second.second());
                second.setSecond(buff);
            }

            cryptString.setCharAt(elem, keyTable[first.first()][first.second()]);
            cryptString.setCharAt(elem + 1, keyTable[second.first()][second.second()]);

            elem += 2;
        }

        if (next == -1) {
            elem = 0;
            while (elem < cryptString.length()) {

                if (cryptString.charAt(elem) == EXTRA_LETTER)
                    cryptString.deleteCharAt(elem);

                else elem ++;
            }
        }

        return cryptString.toString();
    }


    @SuppressLint("HardwareIds")
    private void fillData() {
        String key = checkedKey(
                Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID));
        int code = 32;

        for (int index = 0; index < SIZE * SIZE; index++) {

            int i = index / SIZE, j = index % SIZE;

            if (index < key.length())
                keyTable[i][j] = key.charAt(index);

            else {
                while (key.contains(String.valueOf((char) code)))
                    code = getNextCode(code);

                keyTable[i][j] = (char) code;
                code = getNextCode(code);
            }
        }
    }

    private int getNextCode(int currentCode) {
        switch (currentCode) {
            case 126:
                return 163;
            case 163:
                return 8470;
            case 8470:
                return 167;
            case 167:
                return 177;
            case 177:
                return 214;
            case 214:
                return 223;
            case 223:
                return 230;
            case 230:
                return 1025;
            case 1025:
                return 1040;
            case 1103:
                return 1105;
            case 1105:
                return EXTRA_LETTER;
            default:
                return currentCode + 1;
        }
    }

    @Nullable
    private Pair<Integer, Integer> getIndex(String elem) {
        for (int i = 0; i < SIZE; i++) {
            int j = String.valueOf(keyTable[i]).indexOf(elem);
            if (j > -1)
                return new Pair<>(i, j);
        }
        return null;
    }

    private String checkedKey(String key) {
        StringBuilder builder = new StringBuilder(key);
        int index = 0;

        while (index < builder.length()) {
            String elem = String.valueOf(builder.charAt(index));
            if (builder.substring(0, index).contains(elem)) {
                builder.deleteCharAt(index);
                continue;
            }
            index++;
        }

        return builder.toString();
    }


    //возвращает остаток от деления a на b
    public static int mod(int a, int b) {
        int result = a % b;
        return result < 0 ? result + b : result;
    }
}
