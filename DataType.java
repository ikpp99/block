import java.util.Arrays;

public interface DataType
{
    int getNb();
    
    default byte[] getElem( byte[] b, int p ){
        int e = p + getNb();
        if( e <= b.length ) return Arrays.copyOfRange( b, p , e );
        return null; 
    }
    
//    default boolean putElem( byte[] b, int p, byte[] v ){
//        int e = p + getNb();
//        if( e <= b.length ){ Arrays.copyOfRange( b, p , e );
//        return null; 
//    }
    
    default byte[] getVal ( byte[] b, int p ){ return null;}
    default void   putVal ( byte[] b, int p, byte[] v ){};
    
    default double getDbl   (){ return -8;}
    default float  getFloat (){ return -4;}
    default long   getLong  (){ return  8;}
    default int    getInt   (){ return  4;}

 public static void tt(String s){System.out.println(s);}
}
