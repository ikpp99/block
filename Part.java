package com.pik.xmem;

import java.util.Arrays;

public class Part
{
    protected Block    blk;
    protected long[][] pp;     // idx
    protected Object   arr;
    protected int      arrlen;
    protected type     arrtyp;
    private   int[]    xx;     // size of part
    
    public Part( Block block, Index idx ) throws Exception { this( block, idx.ii );}
    
    public Part( Block block, long[][] indx ) throws Exception 
    {
        blk=block; pp = Index.realIndex( indx, blk.head );
        xx = new int[ pp.length ]; Arrays.fill( xx, 1 ); 
        arrlen=1;
        for( int i=0; i<pp.length; i++) {
            if( pp[i][0] < 1 || pp[i][1] < 1 || pp[i][0]+pp[i][1]-1 > blk.head.siz[i] )
                throw new Exception("BAD index: "+pp[i][0]+" : "+pp[i][1]);
            arrlen *= pp[i][1];
            if( i>0 ) xx[i] = xx[i-1] * (int)pp[i-1][1]; 
        }
        arrtyp = type.val( blk.head.typ );
        arr = type.creArr( arrtyp, arrlen );
        
//String q="\n____________________xx[]: "+" "+Index.idx2str( pp )+"\n";
//for(int i=0;i<xx.length;i++) q+=" "+xx[i]; tt(q+"\n");        
    }
    
    public void getPart() throws Exception{ blk.part( pp, arr, false );}
    public void setPart() throws Exception{ blk.part( pp, arr, true  );}

    public int arrLoc( int[] ijk ) { // ijk[n] > 0 !!! 
        int loc=ijk[0]-1;
String q="arrLoc ijk[]:"+ijk[0];         
        for(int n=1;n<ijk.length;n++) {
            loc += (ijk[n]-1) * xx[n];
            q+=" "+ijk[n];
        }
tt(q+", loc="+loc);
        return loc;
    }
//------------------------------------------------------------------------------
    
    public Object get( int i               ){ return get( new int[]{ i     });} 
    public Object get( int i, int j        ){ return get( new int[]{ i,j   });} 
    public Object get( int i, int j, int k ){ return get( new int[]{ i,j,k });} 

    public Object get( int[] ijk ){ return getObjLoc( arrLoc( ijk ));}

    private Object getObjLoc( int loc ){
        switch( arrtyp ) {
            case DOUBLE:  return Double .valueOf( ((double[])arr)[ loc ] );
            case INT   :  return Integer.valueOf( ((int   [])arr)[ loc ] );
            case LONG  :  return Long   .valueOf( ((long  [])arr)[ loc ] );
            case FLOAT :  return Float  .valueOf( ((float [])arr)[ loc ] );
            case SHORT :  return Short  .valueOf( ((short [])arr)[ loc ] );
            case BYTE  :  return Byte   .valueOf( ((byte  [])arr)[ loc ] );
        }
        return null;
    }
//------------------------------------------------------------------------------

    public void put( Object v, int i               ){ put( v, new int[]{ i     });} 
    public void put( Object v, int i, int j        ){ put( v, new int[]{ i,j   });} 
    public void put( Object v, int i, int j, int k ){ put( v, new int[]{ i,j,k });} 
    
    public void put( Object v, int[] ijk ){
        int loc = arrLoc( ijk );
        switch( arrtyp ) {
            case DOUBLE:  ((double[])arr)[ loc ] = (double) v ; break;
            case INT   :  ((int   [])arr)[ loc ] = (int)    v ; break;
            case LONG  :  ((long  [])arr)[ loc ] = (long)   v ; break;
            case FLOAT :  ((float [])arr)[ loc ] = (float)  v ; break;
            case SHORT :  ((short [])arr)[ loc ] = (short)  v ; break;
            case BYTE  :  ((byte  [])arr)[ loc ] = (byte)   v ;
        }
    }
    
    public String toString(){
        String s = blk.toString(); p="";
        int vx = pp.length;
        vv = Arrays.copyOf( pp, vx ); 
        ss = Arrays.copyOf( pp, vx ); 
        par2str( vx-1 );
        return s+p;
    }
    private long[][] vv, ss;  String p;
    private void par2str( int pv ) {
        if( pv > 1) {
            long sav = vv[pv][0], end = sav+vv[pv][1];
            while( vv[pv][0] < end) {
                par2str( pv-1 );
                vv[pv][0]++;
            }
            vv[pv][0] = sav;
        }
        else {  // pv=1
            p+="\n" + partIndex() + partData() ;
            
        }
    }
    private String partData() {
        String s=Index.idx2str( vv )+" ## ";
        int vx=(int)vv.length;
        int[] ijk = new int[ vx ];
        for(int i=0;i<vx;i++) ijk[i] = (int)( vv[i][0] - ss[i][0]+1);
        
        for(int i=(int)vv[0][0]; i<(int)(vv[0][0]+vv[0][1]); i++ ){ s+="\n";
            for(int j=(int)vv[1][0]; j<(int)(vv[1][0]+vv[1][1]); j++ ){
                
                ijk[0]=i - (int)ss[0][0] + 1; 
                ijk[1]=j - (int)ss[1][0] + 1;
                
                
tt("i="+ijk[0]+",j="+ijk[1]+", k="+ijk[2]+(ijk.length>3? ", l="+ijk[3]:"")+" pos="+arrLoc( ijk ));                
                s+=" "+get( ijk );
            }
        }
        return s;
    }
    private String partIndex() {
        String s = Index.idx2str( vv );
        if( vv.length > 2 ) {
            int i = s.indexOf(',');
            i = s.indexOf(',',i+1);
            s = s.substring( 0, i );
            for(i=2;i<vv.length;i++) s+=", "+vv[i][0]; 
            s+=" ]";
        }
        return s;
    }
    
    private String objStr( int n ) {
        Object obj = getObjLoc( n );
        switch( arrtyp ) {
            case DOUBLE:  return ""+(double) obj;
            case INT   :  return ""+(int   ) obj;
            case LONG  :  return ""+(long  ) obj;
            case FLOAT :  return ""+(float ) obj;
            case SHORT :  return ""+(short ) obj;
            case BYTE  :  return ""+(byte  ) obj;
        }
        return null;
    }
    
///* DBG: =====================================================================================================
    
    public static void main( String[] args ) throws Exception {
        LongMem mem = new LongMem( 512+1300 + 5*7*9*8 + 30 );  Block.iniBlocks( mem );
        
        Block iii = new Block("iii", type.INT, new long[]{ 5,7,9 } );
        Part rrr = new Part( iii,new Index("*,*,*"));
        for(int k=1;k<=9;k++)
            for(int j=1;j<=7;j++)
                for(int i=1;i<=5;i++) rrr.put( Integer.valueOf( 100*i + 10*j + k ), new int[]{i,j,k});
        rrr.setPart(); tt(""+rrr);
        Block.blocks();
        Block qq = new Block("qq",type.INT, new int[]{3,4,5,6}); tt(""+ qq );
        
        
        
        Index ip = new Index("1:3,2:3,2:4,3:4");
        Part  q = new Part( qq, ip );
        tt(""+q);
        
        tt("\n2,3,4\t"+ rrr.get(2,3,4));
        tt("4,5,6\t"+ rrr.get(4,5,6));
    }
    static void tt(String x){System.out.println( x );}
    //*/        
}
