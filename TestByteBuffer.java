import java.nio.ByteBuffer;
import java.util.Enumeration;

public class TestByteBuffer
{
    static void tt(String s){System.out.println(s);}

    public static void main( String[] args ) { new TestByteBuffer( args );}
    TestByteBuffer( String[] args ){
        
        Integer mmm = 1;
        if( args.length > 0 ){ mmm = new Integer( args[0] );}
        int XX = mmm*1024*1024*1024;
        
        byte[] b = new byte[ XX ];
        ByteBuffer bd = ByteBuffer.wrap( b );
        int  j, pd=0;
        for( j=0;j<XX;j+=8){ pd++; bd.putDouble( j, (double)pd );}
        tt("Last Dbl = "+bd.getDouble( XX-8 ));
        
/*        
        long[] arr = new long[ XX ];
        for( int i=0;i<XX;i++) arr[i]=i;
        int xx = XX-1;
        tt( mmm+":\t"+xx +"-value = "+arr[ xx ]+",    M: "+(xx/(1024*1024)*8));
*/        
tt("############################");        
        
        int N=80;
        byte[] bb = new byte[ N ];
        for(int i=0; i<bb.length;i++) bb[i] = (byte)i;
        ttBarr( bb,"Original" );
//-------------------------------
        ByteBuffer buf = ByteBuffer.wrap( bb, 4, 66 );
        tt("arrayOffset()="+buf.arrayOffset());
        buf.put( bb, 0, 11 );
        ttBarr( buf.array(), "Buff");
        
        buf.putInt( 3,125).putInt( 7,126 ).putChar( 11,'A' ).putDouble(13,-12345.).putInt(21, -1 ).putLong( 25,0 );
        
        int i=3;
        tt( ""+buf.getInt( i )+", "+buf.getInt(i+=4)+", "+buf.getChar(i+=4)+", "+buf.getDouble(i+=2)
        +", "+buf.getInt(i+=8)+", "+buf.getLong(i+=4) );
        
        ttBarr( bb,"Original" );
        tt("-----------------------------------------end.");
    }
    
    void ttBarr( byte[] b, String t ){
        String s="byte[] "+t+" :\n";
        for( byte q: b ) s+=" "+q;
//        for(int i=0;i<b.length;i++) s+=" "+b[i];
        tt( s );
    }
}
