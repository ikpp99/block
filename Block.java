public class Block
{
    protected String nam;
    protected long len;
    protected int typ, dim, off;
    protected long[] siz;
    
    public Block( String name, type type, idx idx, long[] ext ){
        nam = name;
        long[] size = idx.getSize();
        len = type.getNb(); for( long d: size ) len *=d;
        
        dim = size.length;
        len += 8*( 2 + dim + ext.length );
        typ = type.ordinal();
    }

    public Block( String name, type type, idx idx ){ this( name, type, idx, null );} 
        
    public Block( type type, idx idx ){ this( "def"+(seq++), type, idx, null );} 
    private static int seq=0;
    
    
    public Block readBlockHeader( LongMem mem, long loc ) {
        return null;
    }
    
    public void writeBlockHeader( LongMem mem, long loc ) {
        
    }
    
///* DBG:   
    public static void main( String[] args )
    {

    }
    static void tt(String x){System.out.println( x );}
//*/    
}
