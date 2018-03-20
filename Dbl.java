
public class Dbl implements DataType
{
    private int nb=8;
    public int getNb(){ return nb;}
    
    public byte[] getVal ( byte[] b, int p ){ DataType.tt("#get DBL# "+nb); return null;}
    public void   putVal ( byte[] b, int p, byte[] v ) { DataType.tt("#put DBL# "+nb);}
}
