
public class Int3 implements DataType
{
    private static final int nb=3;
    public int getNb(){ return nb;}
    
    public byte[] getVal ( byte[] b, int p ){ DataType.tt("#get INT3# "+nb); return null;}
    public void   putVal ( byte[] b, int p, byte[] v ) { DataType.tt("#put INT3# "+nb);}
}
