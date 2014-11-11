/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.scoreAndSearch.metric.util;

/** This class implements some approximations of the logarithm of the factorial.
 * @author joliva
 * @author manuel
 * @author fjdiez
 * @version 1.0
 * @since OpenMarkov 1.0 */
public class MathUtils {
    
    protected static final double LOGPI  =  1.14472988584940017414;

    /**
    * Returns natural logarithm of gamma function.
    *
    * @param x the value
    * @return natural logarithm of gamma function
    */
    public static double lnGamma(double x) {

        double p, q, w, z;

        double A[] = {
              8.11614167470508450300E-4,
              -5.95061904284301438324E-4,
              7.93650340457716943945E-4,
              -2.77777777730099687205E-3,
              8.33333333333331927722E-2
        };
        double B[] = {
              -1.37825152569120859100E3,
              -3.88016315134637840924E4,
              -3.31612992738871184744E5,
              -1.16237097492762307383E6,
              -1.72173700820839662146E6,
              -8.53555664245765465627E5
        };
        double C[] = {
              /* 1.00000000000000000000E0, */
              -3.51815701436523470549E2,
              -1.70642106651881159223E4,
              -2.20528590553854454839E5,
              -1.13933444367982507207E6,
              -2.53252307177582951285E6,
              -2.01889141433532773231E6
        };

        if( x < -34.0 ) {
            q = -x;
            w = lnGamma(q);
            p = Math.floor(q);
            if( p == q )
              throw new ArithmeticException("lnGamma: Overflow");
            z = q - p;
            if( z > 0.5 ) {
            p += 1.0;
            z = p - q;
            }
            z = q * Math.sin( Math.PI * z );
            if( z == 0.0 )
              throw new ArithmeticException("lnGamma: Overflow");
            z = LOGPI - Math.log( z ) - w;
            return z;
        }

        if( x < 13.0 ) {
            z = 1.0;
            while( x >= 3.0 ) {
                x -= 1.0;
                z *= x;
            }
            while( x < 2.0 ) {
                if( x == 0.0 )
                    throw new ArithmeticException("lnGamma: Overflow");
                z /= x;
                x += 1.0;
            }
            if( z < 0.0 )
                z = -z;
            if( x == 2.0 )
                return Math.log(z);
            x -= 2.0;
            p = x * polevl( x, B, 5 ) / p1evl( x, C, 6);
            return( Math.log(z) + p );
        }

        if( x > 2.556348e305 )
            throw new ArithmeticException("lnGamma: Overflow");

        q = ( x - 0.5 ) * Math.log(x) - x + 0.91893853320467274178;

        if( x > 1.0e8 )
            return( q );

        p = 1.0/(x*x);
        if( x >= 1000.0 )
          q += ((7.9365079365079365079365e-4 * p- 2.7777777777777777777778e-3)
                  * p + 0.0833333333333333333333) / x;
        else
          q += polevl( p, A, 4 ) / x;
        return q;
    }

    /**
    * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
    * <pre>
    *                     2          N
    * y  =  C  + C x + C x  +...+ C x
    *        0    1     2          N
    *
    * Coefficients are stored in reverse order:
    *
    * coef[0] = C  , ..., coef[N] = C  .
    *            N                   0
    * </pre>
    * In the interest of speed, there are no checks for out of bounds 
    * arithmetic.
    *
    * @param x argument to the polynomial.
    * @param coef the coefficients of the polynomial.
    * @param N the degree of the polynomial.
    */
    static double polevl( double x, double coef[], int N ) {

        double ans;
        ans = coef[0];

        for(int i=1; i<=N; i++)
            ans = ans*x+coef[i];

        return ans;
    }

    /**
    * Evaluates the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
    * Evaluates polynomial when coefficient of N is 1.0.
    * Otherwise same as <tt>polevl()</tt>.
    * <pre>
    *                     2          N
    * y  =  C  + C x + C x  +...+ C x
    *        0    1     2          N
    *
    * Coefficients are stored in reverse order:
    *
    * coef[0] = C  , ..., coef[N] = C  .
    *            N                   0
    * </pre>
    * The function <tt>p1evl()</tt> assumes that <tt>coef[N] = 1.0</tt> and is
    * omitted from the array.  Its calling arguments are
    * otherwise the same as <tt>polevl()</tt>.
    * <p>
    * In the interest of speed, there are no checks for out of bounds 
    * arithmetic.
    *
    * @param x argument to the polynomial.
    * @param coef the coefficients of the polynomial.
    * @param N the degree of the polynomial.
    */
    static double p1evl( double x, double coef[], int N ) {

        double ans;
        ans = x + coef[0];

        for(int i=1; i<N; i++)
            ans = ans*x+coef[i];

        return ans;
    }
}
