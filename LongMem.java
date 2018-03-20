import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LongMem
{
    public static final int MM = 128;
    
//  protected ArrayList<ByteBuffer> mem;
    protected ArrayList<byte[]> mem;
    
    protected long memLen;
    
    public LongMem( long len ) throws Exception {
        mem = new ArrayList<>();
        long s=0;
        while( s < len ){
            int n = MM;
            if( s+n > len ) n = (int)( len - s );
            
//          mem.add( ByteBuffer.allocateDirect( n ));
            mem.add( new byte[ n ] );
            
            s+=n;
        }
        memLen = len;
    }
    
    public void copyBytes( long pmem, byte[] b, int pb, int len, boolean toMem ) throws Exception 
    {
        long x = pmem + len;
        if(  x > memLen ) throw new Exception("LongMem < "+pmem+" + "+len );

        int b0=buf( pmem ), p0=off( pmem ), bx=buf( x ), ee=MM;
        while( b0 <= bx )
        {
            if( b0==bx ) ee = off( x );
            int nn = ee - p0;
            if( nn >0 )
            {
//              if( toMem ) System.arraycopy( b, pb, mem.get(b0).array(), p0, nn );
//              else        System.arraycopy( mem.get(b0).array(), p0, b, pb, nn );
                if( toMem ) System.arraycopy( b, pb, mem.get(b0), p0, nn );
                else        System.arraycopy( mem.get(b0), p0, b, pb, nn );
            }
            if( ++b0 > bx ) break;
            p0=0; pb+=nn; 
        }
    }
    
    public void putBytes( byte[] b, long pmem ) throws Exception { copyBytes( pmem, b,0, b.length, true  );}
    
    public void getBytes( long pmem, byte[] b ) throws Exception { copyBytes( pmem, b,0, b.length, false );}

    private int buf( long p ){ return (int)( p/MM );}
    private int off( long p ){ return (int)( p - MM*buf( p ));} 
}
