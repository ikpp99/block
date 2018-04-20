package com.pik.xmem;

public class test
{
    static String lon2str( long v ){ String s=lon2str( v, 10 ); return normLen( s, 13 )+"| len="+s.length();}
    static String lon2str( long v, int m ){ 
        String s = (v<0? "":" ")+v;
        int l=s.length(), x=m+1;
        if( l>x ) s = s.substring( 0, x )+"+"+(l-x);
        return s;
    }
    
    static String dbl2str( double v ){ String s=dbl2str( v, 8 ); return normLen( s, 14 )+"| len="+s.length();}
    static String dbl2str( double v, int m ){
        int w = m+6;
        String s = (v<0? "":" ")+String.format("%"+w+"."+m+"g",v).trim().replace(',','.');
        
        int x = s.indexOf("e"), exp=0;
        if( x > 0 ) exp = Integer.parseInt( s.substring( x+1 ));
        
        if( s.substring( 1,3 ).equals("0.")) s = s.replace("0.",".");
        
        if( s.substring( 1,3 ).equals(".0")){
            int ex=-2, p=2;  x=s.length();
            while( ++p < x && s.charAt( p )=='0') ex--;
            if( p<x ){
                s = s.substring( 0, 1 )+s.charAt( p )+"." +s.substring( p+1 );
                exp += ex;
            } else s=" 0";
            x = -1;
        }
        
        if( x < 0 ) x = s.length();
        x = x < w-2? x: w-3;
        s = s.substring( 0, x );
        
        x = s.length(); while( --x > 1 && s.charAt( x )=='0'){}
        s = s.substring( 0, x+1 );
        if( exp !=0 ) s += (exp>0?"+":"")+exp;
        else if( s.charAt( s.length()-1 )=='.') s =s.substring( 0, s.length()-1 );
        
        return s;
    }

    static private String normLen( String s, int w ){
        w -= s.length();
        return w>0? s = s+sp32.substring( 0, w ): s;
    }
    static private final String sp32="                                ";// 32*' '!!!
    
//==============================================================================DBG:
    static void tt(String x){System.out.println( x );}static void tt(){tt("");}

    public static void main( String[] args ) throws Exception {
        long q = Long.MAX_VALUE; tt(" "+q+" = Long.MAX_VALUE");
        for(int i=0;i<20;i++){ tt(""+lon2str( q*(Math.random()<.5?-1:1))); q /=10;} tt();
        
        double d = Double.MAX_VALUE;  tt(" "+d+" = Double.MAX_VALUE");
        for(int i=0;i<55;i++){ tt(""+dbl2str( d*(Math.random()<.5?-1:1))); d *=1e-10;}
        
        d = 0.; tt(""+dbl2str( d ));
        d = -1234.56; tt(""+dbl2str( d ));
        d = 12000.0000e-33; tt(""+dbl2str( d ));
        d = .1234567; tt(""+dbl2str( d ));
        d = 123.4567; tt(""+dbl2str( d ));
        d = 1234567.; tt(""+dbl2str( d ));
    }
}
