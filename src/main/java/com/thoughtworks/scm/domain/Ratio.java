package com.thoughtworks.scm.domain;

public class Ratio {
    public final long numerator;
    public final long denominator;

    public Ratio(long numerator, long denominator) {

        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ratio ratio = (Ratio) o;

        return denominator == ratio.denominator && numerator == ratio.numerator;
    }

    @Override
    public int hashCode() {
        int result = (int) (numerator ^ (numerator >>> 32));
        result = 31 * result + (int) (denominator ^ (denominator >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }
}
