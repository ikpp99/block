import java.util.Arrays;
import java.util.HashMap;

public class idx
{
    public static final int DIM=5, NN=2*DIM;
    private long[] ii;
    
    static private HashMap<String,Long> vars = new HashMap<>();
    
    public idx(){ 
        ii = new long[ NN ]; 
        Arrays.fill( ii, 1 );
    }

    public idx( long[] index ){
        this();
        if( index !=null ) System.arraycopy( index, 0, ii, 0, (int)Math.min( NN, index.length ) );
    }
    
    public idx( String s ){ // ",i,j[:jj],:jj,,", *="ALL from this"
        this();
        if( s !=null && !s.isEmpty() ){
            String[] ss = s.split(","); int pss=0;
            for( String t: ss ){
                if( pss < DIM ){
                    if( !t.isEmpty()){                  int mm=pss*2, m=0, pt=0; long val=0;
                                                        StringBuilder var=new StringBuilder();
                        while( pt<t.length() && m<2 ){
                                                        char q=t.charAt( pt++ );
                            if('0'<= q && q <='9'){
                                val = val*10 + q - 48;
                            }
                            else if( q==':'){
                                if( var.length()>0 ) {
                                    Long v = vars.get( var.toString());
                                    if(  v !=null ) val = v;
                                    var = new StringBuilder();
                                }
                                ii[mm+m] = val==0? 1: val;
                                val=0; m++;
                            }
                            else if( q=='*'){
                                if( m==0){ ii[mm+m]=1;m++;}
                                ii[mm+m] = -1; 
                                break;
                            }
                            else if('A'<= q && q <='Z' || 'a'<= q && q <='z'){ var.append( q );}
                        }
                        
                        if( val !=0 ){ ii[mm+m]=val;}
                        else if( var.length()>0 ) {
                            Long v = vars.get( var.toString());
                            if(  v !=null ) ii[mm+m]=v;
                        }
                    }
                }
                pss++;
            }
        }
    }
    
    public long[] getSize() {
        long[] dd = new long[ DIM ];  int i,j=0;
        for(i=0;i<NN;i+=2){ dd[j++] = Math.max( ii[i], ii[i+1] );}
        while( --j>0 ) if( dd[j]>1 ) break;
        return Arrays.copyOf( dd, j+1 );
    }
    
    static public void var( String name, long value ) { vars.put( name, value );}
    
///*==================================================================== DBG:  1 pars ~ 500 ns.
    public static void main( String[] args )
    {
        idx t=new idx( new long[]{  23,-1,   34,55,   1,-1  }); tar( t );
        String s;
        s=null;                              tst( s );
        s="";                                tst( s );
        s="1,2,3,,:88";                      tst( s );
        s="77,:33, 99:*, 2345:99, *, 55: ";  tst( s );
        
        long t0=System.nanoTime(); int nn=1000000;
        for( int n=0;n<nn;n++){
            s=" 1 , 2 , 3 ,, : 88 ";                      t = new idx( s );
            s=" 77 , : 33 , 99 : * , 2345 : 99 , * , 55 : ";  t = new idx( s );;
        }
        t0 = System.nanoTime() - t0;
        tt(""); tt(nn+":  dt = "+t0+" ns,  t2 = "+t0/nn+" ns");
        
        s="2,3,4,5,6"; idx q=new idx( s ); tt( lar2s(q.ii)); tt( s+" ### "+lar2s( new idx(s).getSize()));
        s="4"; q=new idx( s ); tt(  s+" ### "+lar2s( new idx(s).getSize()));
        s="5,6"; q=new idx( s ); tt(  s+" ### "+lar2s( new idx(s).getSize()));
        s="4,5,6"; q=new idx( s ); tt(  s+" ### "+lar2s( new idx(s).getSize()));
        s="1,1,1,5"; q=new idx( s ); tt(  s+" ### "+lar2s( new idx(s).getSize()));
        
        var("MM", 77777 );
        var("NN", 9999999 ); tt("");
        s="77, :MM, NN:*, MM:NN, *, 55: ";  tst( s );
    }

    static void tst( String s ){ tt( "### Str|"+s+"|" ); idx t=new idx(s); tar( t );}
    static void tar( idx t ){
        String q="idx[]:"; 
        for( int i=0; i<t.ii.length;i++){ long l=t.ii[i]; q+=(i%2==0?"   ":"")+l+",";}
        tt( q.substring( 0,q.length()-1 ) );
    }
    static String lar2s( long[] lar) { String s="long[]:  "; for( long q: lar) s+=q+", "; return s;}
    static void tt(String x){System.out.println( x );}
//*/    
}
