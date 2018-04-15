package com.pik.xmem;

import java.util.Arrays;
import java.util.HashMap;

public class Index
{
    protected long[][] ii;

    public Index( long[][] index ){
        if( index !=null ) {
            int x = index.length;
            ii = new long[ x ][2];
            for(int i=0;i<x;i++){ ii[i][0]=index[i][0]; ii[i][1]=index[i][1];} 
        }
    }
    
    public Index( String s ){ // ",i,j[:jj],:jj,,", *="ALL from this"
        int j;        final int DIM=7;
        long[][] tt = new long[ DIM ][2];
        for(j=0;j< DIM ;j++) tt[j][0]=tt[j][1]=1;
        
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
                                tt[pss][m] = val==0? 1: val;
                                val=0; m++;
                            }
                            else if( q=='*'){
                                if( m==0){ tt[pss][m]=1;m++;}
                                tt[pss][m] = -1; 
                                break;
                            }
                            else if('A'<= q && q <='Z' || 'a'<= q && q <='z'){ var.append( q );}
                        }
                        
                        if( val !=0 ){ tt[pss][m]=val;}
                        else if( var.length()>0 ) {
                            Long v = vars.get( var.toString());
                            if(  v !=null ) tt[pss][m]=v;
                        }
                    }
                }
                pss++;
            }
        }
        int x = DIM;
        while( x-->0 && tt[x][0]==1 && tt[x][1]==1 ){}
        ii = new long[++x][2]; 
        for(j=0;j<x;j++){ ii[j][0]=tt[j][0]; ii[j][1]=tt[j][1]; } 
    }
    
    public long[] getSize() {
        long[] dd = new long[ ii.length ];  int i,j=0;
        for(i=0;i<dd.length;i++){ dd[j++] = Math.max( ii[i][0], ii[i][1] );}
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
    
    static public long[][] realIndex( long[][] ind, Head head ) throws Exception {
        int x = ind.length;
        long[][] jj = Arrays.copyOf( ind, x );
        
        for(int i=0; i<x; i++){
            if( jj[i][0] == -1 ){ jj[i][1] = -1; jj[i][0] = 1; }
            if( jj[i][1] == -1 ){ jj[i][1] = head.siz[i] - jj[i][0]+1;}

            if( jj[i][0]<1 || jj[i][0] > head.siz[i]
            ||  jj[i][1]<1 || jj[i][0] + jj[i][1] -1 > head.siz[i] ) throw new Exception("BAD Index");
            
        }
        return jj;
    }
    
    public String toString(){ return idx2str( this.ii );}
    
    public static String idx2str( long[][] pp ){
        String s="[";
        for(int i=0;i<pp.length;i++) s+=" "+pp[i][0]+":"+pp[i][1]+",";
        s = s.substring( 0, s.length()-1 )+" ]";
        return s;
    }
    
///*==================================================================== DBG:  1 pars ~ 500 ns.
    public static void main( String[] args ) throws Exception
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
        
        s="2,3,4,5,6"; Index q=new Index( s ); tar( q ); tt( s+" ### "+larr2s( new Index(s).getSize()));
        s="4";               q=new Index( s );           tt( s+" ### "+larr2s( new Index(s).getSize()));
        s="5,6";             q=new Index( s );           tt( s+" ### "+larr2s( new Index(s).getSize()));
        s="4,5,6";           q=new Index( s );           tt( s+" ### "+larr2s( new Index(s).getSize()));
        s="1,1,1,5";         q=new Index( s );           tt( s+" ### "+larr2s( new Index(s).getSize()));
        
        var("MM", 77777 );
        var("NN = 9999999 " );              tt("");
        s="77, :MM, NN:*,, MM:NN, *, 55: ";  tst( s );
        
        Head hh = new Head( type.INT, new long[]{300,200,100} );  tt(""); tt(""+hh);
        long[][] relInd = realIndex( new long[][]{{9,5},{9,-1},{-1,1}}, hh ); tii( relInd ); tt("");
        
        relInd = realIndex( new long[][]{{-1,1}}, hh ); tii( relInd );
    }

    static void tst( String s ){ tt( "### Str|"+s+"|" ); Index t=new Index(s); tar( t );}
    static void tar( Index t ){ tii( t.ii );}
    static void tii( long[][] jj ){
        String q="index[][]:"; 
        for( int i=0; i<jj.length;i++) q+=" {"+jj[i][0]+","+jj[i][1]+"},";
        tt( q.substring( 0,q.length()-1 ) );
    }
    static String larr2s( long[] lar) { String s="long[]:  "; for( long q: lar) s+=q+", "; return s;}
    static void tt(String x){System.out.println( x );}
//*/    
}
