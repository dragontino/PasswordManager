package com.security.passwordmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;

import androidx.annotation.Nullable;

public class Cryptographer {

    private static final int START = 32;
    private static final int END = 177;

    private final Context mContext;

//    private int N, publicExp, privateExp;

    private static final char EXTRA_LETTER = (char) 198;
    private static final int SIZE = 10;
    private static final char [][] keyTable = new char[SIZE][SIZE];

    public Cryptographer(Context context) {
        this.mContext = context;
        fillData();
    }


    @Nullable
    public String encrypt(String defaultString) {
        return crypt(defaultString, 1);
    }


    @Nullable
    public String decrypt(String defaultString) {
        return crypt(defaultString, -1);
    }



    @Nullable
    private String crypt(String defaultString, int next) {
        StringBuilder cryptString = new StringBuilder(defaultString);

        int elem = 0;
        while (elem < cryptString.length()) {

            if (next == 1) {
                if (cryptString.charAt(elem) < START || cryptString.charAt(elem) > END)
                    return null;

                if (cryptString.length() % 2 != 0 && elem == cryptString.length() - 1)
                    cryptString.append(EXTRA_LETTER);
                else if (cryptString.charAt(elem) == cryptString.charAt(elem + 1))
                    cryptString.insert(elem + 1, EXTRA_LETTER);
            }

            Pair<Integer, Integer> first = getIndex(
                    String.valueOf(cryptString.charAt(elem)));
            Pair<Integer, Integer> second = getIndex(
                    String.valueOf(cryptString.charAt(elem + 1)));

            if (first == null || second == null)
                return null;

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
                    code = getNewCode(code);

                keyTable[i][j] = (char) code;
                code = getNewCode(code);
            }
        }
    }

    private int getNewCode(int currentCode) {
        switch (currentCode) {
            case 126:
                return 163;
            case 163:
                return 165;
            case 165:
                return 167;
            case 167:
                return 177;
            case 177:
                return EXTRA_LETTER;
            default:
                return currentCode + 1;
        }
    }

    @Nullable
    private Pair<Integer, Integer> getIndex(String elem) {
        for (int i = 0; i < SIZE; i++) {
            int j = String.valueOf(keyTable[i]).indexOf(elem);
//            Log.d("Crypt", String.valueOf(keyTable[i]));
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


//    @SuppressLint("HardwareIds")
//    private void calculateExponents() {
//        String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
//        int a = parseInt(androidId), b = a;
//
//        while (isComposite(a))
//            a--;
//        while (isComposite(b))
//            b++;
//
//        N = a * b;
//        publicExp = 2;
//        privateExp = 1;
//        int phi = (a - 1) * (b - 1);
//
//        while (gcd(phi, publicExp) != 1 && publicExp < phi)
//            publicExp++;
//
//        while ((publicExp * privateExp) % phi != 1)
//            privateExp++;
//    }

//    private int parseInt(String string) {
//        int result = 0;
//
//        for (int i = 0; i < string.length(); i++)
//            result += string.charAt(i);
//
//        return result;
//    }
//
//    private int gcd(int a, int b) {
//        int result = Math.max(a, b);
//
//        if (Math.min(a, b) <= 0)
//            return result;
//
//        return gcd(b, a % b);
//    }
//
//    private boolean isComposite(int number) {
//        if (number % 2 == 0)
//            return number != 2;
//
//        for (int div = 3; div <= Math.sqrt(number); div += 2) {
//            if (number % div == 0)
//                return true;
//        }
//        return false;
//    }
}
