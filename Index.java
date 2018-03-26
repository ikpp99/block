package com.pik.xmem;

import java.util.Arrays;
import java.util.HashMap;

public class Index
{
    public static final int DIM=5;
    private long[][] ii;
    
    public Index(){ 
        ii = new long[ DIM ][2];
        for(int i=0;i<DIM;i++) ii[i][0]= ii[i][1]=1;
    }

    public Index( long[][] index ){
        this();
        if( index !=null ) {
            int x = Math.min( DIM, index.length );
            for(int i=0;i<x;i++){ ii[i][0]=index[i][0]; ii[i][1]=index[i][1];} 
        }
    }
    
    public Index( String s ){ // ",i,j[:jj],:jj,,", *="ALL from this"
        this();
        if( s !=null && !s.isEmpty() ){
            String[] ss = s.split(","); int pss=0;
            for( String t: ss ){
                if( pss < DIM ){
                    if( !t.isEmpty()){                  int m=0, pt=0; long val=0;
                                                        StringBuilder var=new StringBuilder();
                        while( pt<t.length() && m<2 ){
                                                        char q=t.charAt( pt++ );
                            if('0'<= q && q <='9'){
                                val = val*10 + q - 48;
                            }
                            else if( q==':'){
                                if( var.length()>0 ) {
                                    Long v = vars.get( var.toString());
                                    if(  v !=null ) val = v;
                                    var = new StringBuilder();
                                }
                                ii[pss][m] = val==0? 1: val;
                                val=0; m++;
                            }
                            else if( q=='*'){
                                if( m==0){ ii[pss][m]=1;m++;}
                                ii[pss][m] = -1; 
                                break;
                            }
                            else if('A'<= q && q <='Z' || 'a'<= q && q <='z'){ var.append( q );}
                        }
                        
                        if( val !=0 ){ ii[pss][m]=val;}
                        else if( var.length()>0 ) {
                            Long v = vars.get( var.toString());
                            if(  v !=null ) ii[pss][m]=v;
                        }
                    }
                }
                pss++;
            }
        }
    }
    
    public long[] getSize() {
        long[] dd = new long[ DIM ];  int i,j=0;
        for(i=0;i<DIM;i++){ dd[j++] = Math.max( ii[i][0], ii[i][1] );}
        while( --j>0 ) if( dd[j]>1 ) break;
        return Arrays.copyOf( dd, j+1 );
    }
    
    static public void var( String eq ) {
        int i=eq.indexOf('=');
        if( i>0 ) var( eq.substring( 0,i ).trim(), Long.parseLong( eq.substring( i+1 ).trim()));
    }
    static public void var( String name, long value ) {
        if( vars==null ) vars = new HashMap<>();
        vars.put( name, value );
    }
    static private HashMap<String,Long> vars;
    
///*==================================================================== DBG:  1 pars ~ 500 ns.
    public static void main( String[] args )
    {
        Index t=new Index( new long[][]{ {23,-1}, {34,55}, {1,-1} }); tar( t );
        String s;
        s=null;                              tst( s );
        s="";                                tst( s );
        s="1,2,3,,:88";                      tst( s );
        s="77,:33, 99:*, 2345:99, *, 55: ";  tst( s );
        
        long t0=System.nanoTime(); int nn=1000000;
        for( int n=0;n<nn;n++){
            s=" 1 , 2 , 3 ,, : 88 ";                          t = new Index( s );
            s=" 77 , : 33 , 99 : * , 2345 : 99 , * , 55 : ";  t = new Index( s );;
        }
        t0 = System.nanoTime() - t0;
        tt(""); tt(nn+":  dt = "+t0+" ns,  t2 = "+t0/nn+" ns");
        
        s="2,3,4,5,6"; Index q=new Index( s ); tar( q ); tt( s+" ### "+lar2s( new Index(s).getSize()));
        s="4";               q=new Index( s );           tt( s+" ### "+lar2s( new Index(s).getSize()));
        s="5,6";             q=new Index( s );           tt( s+" ### "+lar2s( new Index(s).getSize()));
        s="4,5,6";           q=new Index( s );           tt( s+" ### "+lar2s( new Index(s).getSize()));
        s="1,1,1,5";         q=new Index( s );           tt( s+" ### "+lar2s( new Index(s).getSize()));
        
        var("MM", 77777 );
        var("NN = 9999999 " );              tt("");
        s="77, :MM, NN:*, MM:NN, *, 55: ";  tst( s );
    }

    static void tst( String s ){ tt( "### Str|"+s+"|" ); Index t=new Index(s); tar( t );}
    static void tar( Index t ){
        String q="index[][]:"; 
        for( int i=0; i<t.ii.length;i++) q+=" {"+t.ii[i][0]+","+t.ii[i][1]+"},";
        tt( q.substring( 0,q.length()-1 ) );
    }
    static String lar2s( long[] lar) { String s="long[]:  "; for( long q: lar) s+=q+", "; return s;}
    static void tt(String x){System.out.println( x );}
//*/    
}
