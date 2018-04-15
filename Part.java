package com.pik.xmem;

import java.util.Arrays;

public class Part
{
    protected Block    blk;
    protected long[][] pp;
    protected Object   arr;
    protected int      arrlen;
    protected type     arrtyp;
    private   int[]    xx;
    
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
    }
    
    public void getPart() throws Exception{ blk.part( pp, arr, false );}
    public void setPart() throws Exception{ blk.part( pp, arr, true  );}

    public int arrLoc( int[] ijk ) { // ijk[n] > 0 !!! 
        int loc=ijk[0]-1;
        for(int n=1;n<ijk.length;n++) loc += (ijk[n]-1) * xx[n]; 
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
        String s = blk.toString();
        String p = "\n[";
        for(int i=0;i<pp.length;i++) p+=" "+pp[i][0]+":"+pp[i][1]+",";
        p = p.substring( 0, p.length()-1 )+" ]\n";
        for( int i=0;i<arrlen;i++) p += objStr( i )+" "; 
        return s+p;
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

        
        
        
        tt("");
    }
    static void tt(String x){System.out.println( x );}
//*/        
    
}
