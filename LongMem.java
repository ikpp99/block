package com.pik.xmem;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LongMem
{
    protected static int MM = 128;  // 1024*1024*512 = 0.5 GB // length of byte[] arrs. 
    protected long memLen;
    protected ArrayList< byte[] > mem;
    
    public LongMem( long len )  throws Exception { this( len, MM );} 
    
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
    }
    
    private void copArr( byte[] bb, int pbb, Object arr, int parr, int len, type typ, boolean put )
    {
        if( typ.equals( type.BYTE ))
        {
            if( put ) System.arraycopy( (byte[]) arr, parr, bb, pbb, len ); 
            else        System.arraycopy( bb, pbb, (byte[]) arr, parr, len );        
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

    
///* DBG:   
    public static void main( String[] args ) throws Exception
    {
        int NN=1111;
        LongMem mem = new LongMem( 12*NN ); 
        double[] dd = new double[ NN ]; for(int i=0;i<NN;i++) dd[i]=i;
        double[] rr = new double[ dd.length ]; 
        
        mem.copyArr( 48, dd, 2, NN-2, true );
        mem.copyArr( 48, rr,         false );
        String s="[]: "; for( double q: rr ) s+=q+", ";  tt( s );
        
        long[] ddd = new long[ NN ]; for(int i=0;i<NN;i++) ddd[i]=i;
        long[] rrr = new long[ ddd.length ]; 
        
        mem.copyArr( 48, ddd, 2, NN-2, true );
        mem.copyArr( 48, rrr,         false );
        s="[]: "; for( long q: rrr ) s+=q+", ";  tt( s );
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
