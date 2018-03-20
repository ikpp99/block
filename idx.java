import java.util.Arrays;

public class idx
{
    public static final int DIM=5, NN=2*DIM;
    private long[] ii;
    
// 1 <= idx[j] <=B_Max_Ind || =-1 - ALL left !!!    
    
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
                    t = t.trim();
                    if( !t.isEmpty()){
                                                        int mm=pss*2, m=0, pt=0; long val=0;
                        while( pt<t.length() && m<2 ){
                                                        char q=t.charAt( pt++ );
                            if('0'<= q && q <='9'){
                                val = val*10 + q - 48;
                            }
                            else if( q==':'){
                                ii[mm+m] = val==0? 1: val;
                                val=0; m++;
                            }
                            else if( q=='*'){
                                if( m==0){ ii[mm+m]=1;m++;}
                                ii[mm+m] = -1; 
                                break;
                            }
                        }
                        if( val !=0 ) ii[mm+m]=val; 
                    }
                }
                pss++;
            }
        }
    }
    
/* DBG:  1 pars ~ 500 ns.   
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
            s="1,2,3,,:88";                      t = new idx( s );
            s="77,:33, 99:*, 2345:99, *, 55: ";  t = new idx( s );;
        }
        t0 = System.nanoTime() - t0;
        tt(""); tt(nn+":  dt = "+t0+" ns,  t2 = "+t0/nn+" ns");
        
    }
    static void tst( String s ){ tt( "### Str|"+s+"|" ); idx t=new idx(s); tar( t );}
    static void tar( idx t ){
        String q="idx[]:"; 
        for( int i=0; i<t.ii.length;i++){ long l=t.ii[i]; q+=(i%2==0?"   ":"")+l+",";}
        tt( q.substring( 0,q.length()-1 ) );
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
