package com.security.passwordmanager;

public class Pair<X, Y> {

    private X first;
    private Y second;

    public Pair(X first, Y second) {
        setFirst(first);
        setSecond(second);
    }

    public Pair() {
        this(null, null);
    }

    public X first() {
        return first;
    }

    public Y second() {
        return second;
    }

    public void setFirst(X first) {
        this.first = first;
    }

    public void setSecond(Y second) {
        this.second = second;
    }

    public boolean isFirst(X first) {
        return this.first.equals(first);
    }

    public boolean isSecond(Y second) {
        return this.second.equals(second);
    }

    public boolean equals(Pair<X, Y> pair, Integer position) {
        if (position == 0)
            return isFirst(pair.first);
        else if (position == 1)
            return isSecond(pair.second);
        else
            return isFirst(pair.first) && isSecond(pair.second);
    }
}
