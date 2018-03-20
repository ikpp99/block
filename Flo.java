
public class Flo implements DataType
{
    private static final int nb=4;
    public int getNb(){ return nb;}
    
    public byte[] getVal ( byte[] b, int p ){ DataType.tt("#get FLO# "+nb); return null;}
    public void   putVal ( byte[] b, int p, byte[] v ) { DataType.tt("#put FLO# "+nb);}
}
