package MPSI;
import java.math.BigInteger;

public class Polynomial {
    private BigInteger[] coef;   // coefficients p(x) = sum { coef[i] * x^i }
    private int degree;   // degree of polynomial (-1 for the zero polynomial)
    private BigInteger modulo; // finite field Z_p

    /**
     * Initializes a new polynomial a x^b
     * @param a the leading coefficient
     * @param b the exponent
     * @throws IllegalArgumentException if {@code b} is negative
     */
    public Polynomial(BigInteger a, int b, BigInteger modulo) {
        if (b < 0) {
            throw new IllegalArgumentException("exponent cannot be negative: " + b);
        }
        
        this.modulo = modulo;
        
        coef = new BigInteger[b+1];
        
        for(int i=0; i<b+1; i++)
        	coef[i] = BigInteger.ZERO;
        
        coef[b] = a.mod(modulo);
        reduce();
    }
    

    // pre-compute the degree of the polynomial, in case of leading zero coefficients
    // (that is, the length of the array need not relate to the degree of the polynomial)
    private void reduce() {
        degree = -1;
        for (int i = coef.length - 1; i >= 0; i--) {
            if (!coef[i].equals(BigInteger.ZERO)) {
                degree = i;
                return;
            }
        }
    }

    /**
     * Returns the degree of this polynomial.
     * @return the degree of this polynomial, -1 for the zero polynomial.
     */
    public int degree() {
        return degree;
    }

    public BigInteger[] coefficients() {
    	return coef;
    }
    
    /**
     * Returns the sum of this polynomial and the specified polynomial.
     *
     * @param  that the other polynomial
     * @return the polynomial whose value is {@code (this(x) + that(x))}
     */
    public Polynomial plus(Polynomial that) {
        Polynomial poly = new Polynomial(BigInteger.ZERO, Math.max(this.degree, that.degree), modulo);
        for (int i = 0; i <= this.degree; i++) poly.coef[i] = poly.coef[i].add(this.coef[i]).mod(modulo);
        for (int i = 0; i <= that.degree; i++) poly.coef[i] = poly.coef[i].add(that.coef[i]).mod(modulo);
        poly.reduce();
        return poly;
    }

    /**
     * Returns the result of subtracting the specified polynomial
     * from this polynomial.
     *
     * @param  that the other polynomial
     * @return the polynomial whose value is {@code (this(x) - that(x))}
     */
    public Polynomial minus(Polynomial that) {
        Polynomial poly = new Polynomial(BigInteger.ZERO, Math.max(this.degree, that.degree), modulo);
        for (int i = 0; i <= this.degree; i++) poly.coef[i] = poly.coef[i].add(this.coef[i]).mod(modulo);
        for (int i = 0; i <= that.degree; i++) poly.coef[i] = poly.coef[i].subtract(that.coef[i]).mod(modulo);;
        poly.reduce();
        return poly;
    }

    /**
     * Returns the product of this polynomial and the specified polynomial.
     * Takes time proportional to the product of the degrees.
     * (Faster algorithms are known, e.g., via FFT.)
     *
     * @param  that the other polynomial
     * @return the polynomial whose value is {@code (this(x) * that(x))}
     */
    public Polynomial times(Polynomial that) {
        Polynomial poly = new Polynomial(BigInteger.ZERO, this.degree + that.degree, modulo);
        for (int i = 0; i <= this.degree; i++)
            for (int j = 0; j <= that.degree; j++)
            	poly.coef[i+j] = poly.coef[i+j].add( this.coef[i].multiply(that.coef[j]).mod(modulo) ).mod(modulo);
        poly.reduce();
        return poly;
    }

    /**
     * Returns the result of evaluating this polynomial at the point x.
     *
     * @param  x the point at which to evaluate the polynomial
     * @return the integer whose value is {@code (this(x))}
     */
    public BigInteger evaluate(BigInteger x) {
        BigInteger p = BigInteger.ZERO;
        for (int i = degree; i >= 0; i--)
            p = coef[i].add(x.multiply(p).mod(modulo)).mod(modulo);
        return p;
    }

    /**
     * Return a string representation of this polynomial.
     * @return a string representation of this polynomial in the format
     *         4x^5 - 3x^2 + 11x + 5
     */
    @Override
    public String toString() {
        if      (degree == -1) return "0";
        else if (degree ==  0) return "" + coef[0];
        else if (degree ==  1) return coef[1] + "x + " + coef[0];
        String s = coef[degree] + "x^" + degree;
        for (int i = degree - 1; i >= 0; i--) {
            if      (coef[i].compareTo(BigInteger.ZERO) == 0) continue;
            else if  (coef[i].compareTo(BigInteger.ZERO) == 1) s = s + " + " + (coef[i]);
            else if (coef[i].compareTo(BigInteger.ZERO) == -1) s = s + " - " + (coef[i].negate());
            if      (i == 1) s = s + "x";
            else if (i >  1) s = s + "x^" + i;
        }
        return s;
    }
}

/******************************************************************************
 *  Copyright 2002-2020, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/