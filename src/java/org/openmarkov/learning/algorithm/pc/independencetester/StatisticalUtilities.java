/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.learning.algorithm.pc.independencetester;

public class StatisticalUtilities {

	static public double chiSquare(double st, double dgf){
	    return (gammp(dgf/2.0,st/2.0));
	}
	
	static public double gammp(double a, double x){
		//System.out.println("->> " + x);
	    double gammser,gammcf;
	    if(x<0.0 || a <= 0.0) 
	    	System.out.println("Invalid Parameters in routine gammp in LogFactorial");
	    if(x<(a+1.0)) {
	       gammser = gser(a,x);
	       return gammser;
	    } else {
	       gammcf = gcf(a,x);
	       return (1.0 - gammcf);
	    }
	}
	
	static public double gser(double a, double x){

	    int ITMAX=100;
	    double EPS=3.0e-7;
	    int n;
	    double sum,del,ap,gln,gammser;
	    gln = gammaln(a);
	    if(x <= 0.0){
	       if(x < 0.0) System.out.println("Error: Parameter x is less than 0.0 in routine gser in LogFactorial");
	       gammser = 0.0;
	       return gammser;
	    }else{
	       ap = a;
	       del = sum = 1.0/a;
	       for(n=1; n<= ITMAX; n++){
	          ++ap;
	          del*=x/ap;
	          sum+=del;
	          if(Math.abs(del) < (Math.abs(sum)*EPS)){
	             gammser=sum*Math.exp(-x+a*Math.log(x)-gln);
	             return gammser;
	          }
	       }
	       System.out.println("Parameter a too large, ITMAX too small in routine gser in LogFactorial");
	       gammser=sum*Math.exp(-x+a*Math.log(x)-gln);      
	       return gammser;
	    }
	}


	static public double gcf(double a, double x){

	    int ITMAX=100;
	    double EPS=3.0e-7;
	    double FPMIN=1.0e-30;
	    int i;
	    double an,b,c,d,del,h;
	    double gln;
	    double gammcf;
	    gln = gammaln(a);
	    b=x+1.0-a;
	    c=1.0/FPMIN;
	    d=1.0/b;
	    h=d;
	    for(i=1 ; i<= ITMAX; i++){
	       an=-i*(i-a);
	       b+=2.0;
	       d=an*d+b;
	       if(Math.abs(d) < FPMIN) d=FPMIN;
	       c=b+an/c;
	       if(Math.abs(c) < FPMIN) c=FPMIN;
	       d=1.0/d;
	       del=d*c;
	       h*=del;
	       if(Math.abs(del-1.0) < EPS) break;
	    }
	    if(i > ITMAX) System.out.println("Parameter a is too large, ITMAX too small in routine gcf");
	    gammcf = Math.exp(-x+a*Math.log(x)-gln)*h;
	    return gammcf;
	}
	
	static public double gammaln(double xx){
	    
	    double x,y,tmp,ser;
	    double[] cof = new double[6];
	    cof[0] = 76.18009172947146;
	    cof[1] = -86.50532032941677;
	    cof[2] = 24.01409824083091;
	    cof[3] = -1.231739572450155;
	    cof[4] = 0.1208650973866179e-2;
	    cof[5] = -0.5395239384953e-5;
	    int j;
	    y = xx;
	    x = xx;
	    tmp = x+5.5;
	    tmp-= (x+0.5)*Math.log(tmp);
	    ser = 1.000000000190015;
	    for(j=0 ; j<=5 ; j++) ser+=cof[j]/++y;
	    return (-tmp+Math.log(2.5066282746310005*ser/x));

	}
	
	static public double log2(double x){
		if (x < 1e-6)
            return 0.0;
        else
            return Math.log(x) / Math.log(2);
	}
}
