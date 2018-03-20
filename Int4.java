
public class Int4 implements DataType
{
    private static final int nb=4;
    public int getNb(){ return nb;}
    
    public byte[] getVal ( byte[] b, int p ){ DataType.tt("#get INT4# "+nb); return null;}
    public void   putVal ( byte[] b, int p, byte[] v ) { DataType.tt("#put INT4# "+nb);}
}
