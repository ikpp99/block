import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LongMem
{
    protected static int MM = 64;  // 1024*1024*512 = 0.5 GB // length of byte[] arrs. 
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
    
    private void copArr( byte[] bb, int pbb, Object arr, int parr, int len, type typ, boolean toMem )
    {
        if( typ.equals( type.BYTE ))
        {
            if( toMem ) System.arraycopy( (byte[]) arr, parr, bb, pbb, len ); 
            else        System.arraycopy( bb, pbb, (byte[]) arr, parr, len );        
        }
        else {
                                 ByteBuffer buf = ByteBuffer.wrap( bb, pbb, len * typ.getNb() );
            if( toMem ){
                switch( typ ){
                    case SHORT : buf.asShortBuffer() .put( ( short[]) arr, parr, len ); break;
                    case INT   : buf.asIntBuffer()   .put( (   int[]) arr, parr, len ); break;
                    case LONG  : buf.asLongBuffer()  .put( (  long[]) arr, parr, len ); break;
                    case FLOAT : buf.asFloatBuffer() .put( ( float[]) arr, parr, len ); break;
                    case DOUBLE: buf.asDoubleBuffer().put( (double[]) arr, parr, len ); break;
                }
            } else {
                switch( typ ){
                    case SHORT : buf.asShortBuffer() .get( ( short[]) arr, parr, len ); break;
                    case INT   : buf.asIntBuffer()   .get( (   int[]) arr, parr, len ); break;
                    case LONG  : buf.asLongBuffer()  .get( (  long[]) arr, parr, len ); break;
                    case FLOAT : buf.asFloatBuffer() .get( ( float[]) arr, parr, len ); break;
                    case DOUBLE: buf.asDoubleBuffer().get( (double[]) arr, parr, len ); break;
                }
            }
        }    
    }
    
    public void copyArr( long pmem, Object arr, int parr, int len, type typ, boolean toMem ) throws Exception
    {
        int  nb = typ.getNb();
        long arlen = len*nb , x = pmem+arlen;
        if(  x > memLen ) throw new Exception("LongMem < "+pmem+"+"+arlen+" = "+x );

        int b0=buf( pmem ), p0=off( pmem ), bx=buf( x ), ee=MM;
        while( b0 <= bx )
        {
            if( b0==bx ) ee = off( x );
            int nn = ee - p0, nar=nn/nb; //numb. of bytes,
            if( nn >0 ) copArr( mem.get(b0), p0, arr, parr, nar, typ, toMem );
            if( ++b0 > bx ) break;
            p0=0; parr += nar; 
        }
    }
    private int buf( long p ){ return (int)( p/MM );}
    private int off( long p ){ return (int)( p - MM*buf( p ));} 

    
///* DBG:   
    public static void main( String[] args ) throws Exception
    {
        LongMem mem = new LongMem( 256 ); 
        double[] dd = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        double[] rr = new double[ dd.length ]; 
        
        mem.copyArr( 48, dd, 2, 18, type.DOUBLE, true );
        mem.copyArr( 48, rr, 0, 18, type.DOUBLE, false );
        String s="[]: "; for( double q: rr ) s+=q+", ";  tt( s );
        
        long[] ddd = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        long[] rrr = new long[ ddd.length ]; 
        
        mem.copyArr( 48, ddd, 2, 18, type.LONG, true );
        mem.copyArr( 48, rrr, 0, 18, type.LONG, false );
        s="[]: "; for( long q: rrr ) s+=q+", ";  tt( s );
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
