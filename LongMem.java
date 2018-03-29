package com.pik.xmem;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class LongMem
{
    static protected  int MM = 64;  // 1024*1024*512 = 0.5 GB // length of byte[] arrs. 
    protected long memLen;
    protected ArrayList< byte[] > mem;
    
    protected HashMap< String, Long > blockNamLoc; 
    protected TreeMap<   Long, Long > freeLocLen;
    
    public LongMem( long len ) throws Exception { this( len, MM );} 
    
    protected LongMem( long len, int bufMM ) throws Exception 
    {
        MM = bufMM;
        mem = new ArrayList<>();
        long s=0;
        while( s < len ){
            int n = MM;
            if( s+n > len ) n = (int)( len - s );
            
            mem.add( new byte[ n ] );
            s+=n;
        }
        memLen = len;
        freeLocLen = new TreeMap<>();
        freeLocLen.put( 0L, len );
    }
    
    private void copArr( byte[] bb, int pbb, Object arr, int parr, int len, type typ, boolean put )
    {
        if( typ.equals( type.BYTE ))
        {
            if( put ) System.arraycopy( (byte[]) arr, parr, bb, pbb, len ); 
            else      System.arraycopy( bb, pbb, (byte[]) arr, parr, len );        
        }
        else {
                                 ByteBuffer buf = ByteBuffer.wrap( bb, pbb, len * typ.getNb() );
            if( put ){
                switch( typ ){
                    case SHORT : buf.asShortBuffer() .put( ( short[]) arr, parr, len ); break;
                    case INT   : buf.asIntBuffer()   .put( (   int[]) arr, parr, len ); break;
                    case LONG  : buf.asLongBuffer()  .put( (  long[]) arr, parr, len ); break;
                    case FLOAT : buf.asFloatBuffer() .put( ( float[]) arr, parr, len ); break;
                    case DOUBLE: buf.asDoubleBuffer().put( (double[]) arr, parr, len );
                    default:
                }
            } else {
                switch( typ ){
                    case SHORT : buf.asShortBuffer() .get( ( short[]) arr, parr, len ); break;
                    case INT   : buf.asIntBuffer()   .get( (   int[]) arr, parr, len ); break;
                    case LONG  : buf.asLongBuffer()  .get( (  long[]) arr, parr, len ); break;
                    case FLOAT : buf.asFloatBuffer() .get( ( float[]) arr, parr, len ); break;
                    case DOUBLE: buf.asDoubleBuffer().get( (double[]) arr, parr, len );
                    default:
                }
            }
        }    
    }

    public void copyArr( long pmem, Object arr, boolean put ) throws Exception { copyArr( pmem, arr, -1, -1, put );}

    public void copyArr( long pmem, Object arr, int parr, int len, boolean put ) throws Exception
    {
                                       type typ = null;    
        if(      arr instanceof   byte[] ){ typ = type.BYTE;  if(parr<0) len = ((  byte[]) arr ).length;}
        else if( arr instanceof   long[] ){ typ = type.LONG;  if(parr<0) len = ((  long[]) arr ).length;}
        else if( arr instanceof    int[] ){ typ = type.INT;   if(parr<0) len = ((   int[]) arr ).length;}
        else if( arr instanceof double[] ){ typ = type.DOUBLE;if(parr<0) len = ((double[]) arr ).length;}
        else if( arr instanceof  float[] ){ typ = type.FLOAT; if(parr<0) len = (( float[]) arr ).length;}
        else if( arr instanceof  short[] ){ typ = type.SHORT; if(parr<0) len = (( short[]) arr ).length;}
                                                              if(parr<0) parr=0;
        int  nb = typ.getNb();
        long arlen = len*nb , x = pmem+arlen;
        if(  x > memLen ) throw new Exception("LongMem < "+pmem+"+"+arlen+" = "+x );

        int b0=buf( pmem ), p0=off( pmem ), bx=buf( x ), ee=MM;
        while( b0 <= bx )
        {
            if( b0==bx ) ee = off( x );
            int nn = ee - p0, nar=nn/nb; //numb. of bytes,
            if( nn >0 ) copArr( mem.get(b0), p0, arr, parr, nar, typ, put );
            if( ++b0 > bx ) break;
            p0=0; parr += nar; 
        }
    }
    private int buf( long p ){ return (int)( p/MM );}
    private int off( long p ){ return (int)( p - MM*buf( p ));} 

    public void copyLeft( long d, long s, long n )      // Left: d < s !!!
    {    
        if( d < s ){ 
            int sss = buf( s ), ssX = buf( s+n ), s0 = off( s ), sZ = off( s+n ), sx=MM; 
            int ddd = buf( d ), ddX = buf( d+n ), d0 = off( d ), dZ = off( d+n ), dx=MM;
            long xx=0;

            while(  xx < n ){
                if( sss==ssX ) sx=sZ;
                if( ddd==ddX ) dx=dZ;  // dd:(d0,dx) <- ss:(s0,sx)  

                int nn = Math.min( dx-d0 , sx-s0 );
                System.arraycopy( mem.get( sss ), s0, mem.get( ddd ), d0, nn ); 

                s0 += nn; if( s0==MM ){ sss++; s0=0; sx=MM;}
                d0 += nn; if( d0==MM ){ ddd++; d0=0; dx=MM;}
                xx += nn;
            }
        }
    }
    
///* DBG:
                                                                static final boolean PUT=true, GET=false;
    public static void main( String[] args ) throws Exception
    {
        int NN=1111; 
        LongMem LMem = new LongMem( 12*NN );
        
        long[] ddd = new long[ NN ]; for(int i=0;i<NN;i++) ddd[i]=i;
        long[] rrr = new long[ ddd.length ]; 

        long aa=48, bb=120, cc=8;
        LMem.copyArr( aa, ddd, 2, NN-2, PUT );
        LMem.copyArr( aa, rrr,          GET );
        String s="[]: "; for( long q: rrr ) s+=q+", ";  tt( s );

        double[] dd = new double[ NN ]; for(int i=0;i<NN;i++) dd[i]=i;
        double[] rr = new double[ dd.length ]; 
        
        LMem.copyArr( bb, dd, 2, NN-2, PUT );
        LMem.copyArr( bb, rr,          GET );
        s="[]: "; for( double r: rr ) s+=r+", ";  tt( s );
        
        LMem.copyLeft( cc, bb, NN*8 );
        LMem.copyArr(  cc, rr, GET  );
        s="[]: "; for( double r: rr ) s+=r+", ";  tt( s );
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
