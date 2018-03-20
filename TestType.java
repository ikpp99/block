import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Arrays;

public class TestType
{
    static void tt(String s){System.out.println(s);}

    public static void main( String[] args ) { new TestType( args );}
    TestType( String[] args ){
        byte[] b = new byte[ 9 ]; 
        byte[] v = new byte[ 9 ]; 
        
        type.DBL .ins.putVal( b,0,v );
        type.INT4.ins.putVal( b,0,v );
        tt("");
        
        type dd = type.DBL, ii = type.INT4;
        dd.ins.putVal( b,0,v );
        ii.ins.putVal( b,0,v );

        
        tt("\nname="+dd.name()+", ordinal="+dd.ordinal()+", dd.nb="+dd.ins.getNb()+", dd="+dd );
        tt(  "name="+ii.name()+", ordinal="+ii.ordinal()+", ii.nb="+ii.ins.getNb()+", ii="+ii );
        
        tt("\nn1="+type.valueOf("DBL").name()+", n2="+type.valueOf("INT4").name());
        
        tt("------------------------------------------------------");
        
        for(int t=0;t<type.values().length;t++) tt(t+":\t"+type.n2type( t ));  // !!!!!!
        
        type typ = type.n2type( 2 );          // ### type
        typ.ins.getVal( b, 1 );
        
        DataType dat = type.n2type( 3 ).ins;  // ## DataType 
        dat.putVal( b, 1, v );
        tt("3-nb: "+dat.getNb());
        tt("cre( typ="+type.INT3.ordinal()+", i,j,k )");  // cre( type.INT3, i, j, k ) !!! ordinal --> f() !!!
        
        int BB=32, DD=BB/8, i;
        byte[] bbb = new byte[ BB ]; double ddd[] = new double[ DD ];
        for(i=0;i<BB;i++) bbb[i]=(byte)(i+1);
        ByteBuffer bu = ByteBuffer.wrap( bbb );
        
        Arrays.fill( ddd, -1. );
        String s="0_ddd[]: "; for(i=0;i<DD;i++) s+=ddd[i]+", "; tt( s );
        
        DoubleBuffer dd8 = bu.asDoubleBuffer();
        
tt("-------------------------------- hasArray="+dd8.hasArray());        
        s="8_ddd[]: "; for(i=0;i<DD;i++) s+=dd8.get()+", "; tt( s );
        
//        System.arraycopy( d8, 0, ddd, 0, DD );
        
        Object qq=null;
        qq = bbb;
        s="### Object<-[]:\n";
        for(byte d: (byte[])qq) s+=d+", ";
        tt( s );
        
        tt("is byte[] = "+(qq instanceof byte[]));
        tt("is int[] = "+(qq instanceof int[]));
        tt("is double[] = "+(qq instanceof double[]));
        tt("ddd is double[] = "+(ddd instanceof double[]));
        
        s="1_ddd[]: "; for(i=0;i<DD;i++) s+=ddd[i]+", "; tt( s );
        
    }
    
    /*
     * Enum type of DATA
     */
    public enum type
    {
        DBL(new Dbl()), INT4(new Int4()), INT3(new Int3()), FLO(new Flo());

        public  final DataType ins;
        private type( DataType inst ){ ins=inst;}
        
        public  static type  n2type( int n ){
            if( n<0 || n>=type.values().length ) return null;
            return type.values()[n]; 
        }
    }
}
