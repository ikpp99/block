package com.pik.xmem;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class Block
{
    static protected LongMem mem;
//    static protected volatile boolean Busy=false; 

    static protected LinkedHashMap < String, Block > blockNamBlk; 
    static protected TreeMap       < Long  , Long  > freeLocLen;
    static private int Seq = 0; 
    
    protected long    loc;
    protected String  nam;
    protected Head    head;
    
    static public void iniBlocks( LongMem longMem )
    {
        mem = longMem;
        freeLocLen = new TreeMap<>();
        freeLocLen.put( 0L, mem.memLen );
        blockNamBlk = new LinkedHashMap<>();
    }
    
    public static Block getBlock( String name ) throws Exception { return blockNamBlk.get( name );}

    public  Block( type type, long[] size ) throws Exception { this("_"+(Seq++) , type, size, 0 );}
    public  Block( String name, type type, long[] size ) throws Exception { this( name, type, size, 0 );}
    public  Block( String name, type type, long[] size, int extLen ) throws Exception 
    {
        head = new Head( type, size, extLen );
        long sum = writeBlock( name );
        if( sum >0 ){
            if( sum < head.len ) throw new Exception("NO MEMORY");
//          if(1==1)throw new Exception("NO Continuous MEMORY "+head.len);

            while( sum >0 ){
                if( head.len <= concatLastHoles() ) sum = writeBlock( name );
            }
        }
    }

    private long writeBlock( String name )  throws Exception
    {
        long sum=0;
        for( Long pos:   freeLocLen.keySet() ){
             Long free = freeLocLen.get( pos ); sum += free; 
             if(  free >= head.len )
             {
                if( blockNamBlk.get( name ) !=null ) throw new Exception("Block ALREADY EXISTS, "+name); 
                
                head.writeHead ( mem , pos );  nam=name;  loc = pos; 
                blockNamBlk.put( name, this );
                
                freeLocLen.remove( pos ); 
                free -= head.len;
                if( free > 0 ) freeLocLen.put( pos+head.len, free );
                return 0;
             }
        }
        return sum;
    }

    private long concatLastHoles() throws Exception {
        Object[] keys = freeLocLen.keySet().toArray();
        int xx = keys.length;
        if( xx < 2 ) throw new Exception("NO MEMORY");
        
        Long las = (Long)keys[ xx-1 ];
        Long lll = freeLocLen.get( las );
        
        Long pre = (Long)keys[ xx-2 ];
        long ppp = freeLocLen.get( pre );
        
        long dat = pre+ppp;
        long lda = las-dat;
        long fre = ppp+lll;
        
        mem.copyLeft( pre, dat, lda );
        
        freeLocLen.remove( pre );
        freeLocLen.remove( las );
        freeLocLen.put( pre+lda, fre );
        
        for( String name: blockNamBlk.keySet()){
            Block blk = blockNamBlk.get( name );
            if( dat <= blk.loc && blk.loc <=las ) blk.loc-=ppp;
        }
        return fre;
    }

    public void delete() throws Exception
    {
        long len = head.len;
        blockNamBlk.remove( nam );  long nex = loc + len;
        for( Long free: freeLocLen.keySet()){
            if( nex == free ){
                len += freeLocLen.get( free ); 
                freeLocLen.remove( free );
                break;
            }
        }
        boolean mustPut=true;  // ( loc,len )
        
        for( Long free: freeLocLen.keySet()){
             long lfree = freeLocLen.get( free );
             if( free + lfree == loc ){
                 freeLocLen.remove( free );
                 freeLocLen.put( free, lfree + len );
                 mustPut = false;
                 break;
             }
        }
        if( mustPut ) freeLocLen.put( loc, len );
    }

    static public void delete( String name ) throws Exception { getBlock( name ).delete();}
    
    public void copyExt( byte[] ext, boolean put ) throws Exception {
        if( ext !=null && head.exx >0 ){
            int xx = Math.min( ext.length, head.exx );
            if( xx >0 ) mem.copyArr( loc+8*(2+head.dim), ext, put ); 
        }
    }
    
    public String toString(){ return "Block \""+nam+"\": "+head+", loc="+loc+", dat="+(loc+head.off);}
    
    static public String blocks() throws Exception {
        String s="\n### Blocks:"; int i=0;
        for( String name: blockNamBlk.keySet()) s+="\n    "+(++i)+".\t\t"+getBlock( name );
        s+="\n### Holes:"; i=0; long free=0;
        for( Long p: freeLocLen.keySet()){ 
            long h=freeLocLen.get( p ); free+=h;  
            s+="\n    "+(++i)+".\t\t"+p+"\t\t"+freeLocLen.get( p );
        }
        return s+"\n-------------------------------------------------\n\t\tFree:\t\t"+free;
    }
    
    public void copy( Object arr, boolean put) throws Exception { mem.copyArr( loc+head.off, arr, put );}
    
    static public int arrLen( long[] size ){ int xx=1; for( long l: size ) xx *= l; return xx;}
    
    public String ttBlock()  throws Exception {
        String s ="### "+this +"\n";
        Object arr = crePart( head.siz );  int xx=arrLen( head.siz );
        type typ = type.val( head.typ );
        
        mem.copyArr( loc+head.off, arr, false );
        for(int i=0;i<xx;i++){
            switch( typ ){
                case BYTE:   s+=" "+((byte  [])arr)[i]; break;
                case SHORT:  s+=" "+((short [])arr)[i]; break;
                case INT:    s+=" "+((int   [])arr)[i]; break;
                case LONG:   s+=" "+((long  [])arr)[i]; break;
                case FLOAT:  s+=" "+((float [])arr)[i]; break;
                case DOUBLE: s+=" "+((double[])arr)[i];
            }
        }
        return s;
    }

    public long[] getPartSize( Index idx ){
        long[][] reInd = Index.realIndex( idx.ii, head );
        long[] partSize = new long[ reInd.length ];
        for( int i=0;i<partSize.length;i++) partSize[i] = reInd[i][1];
        return partSize;
    }
    
    public Object crePart( Index idx ){ return crePart( getPartSize( idx ));}
    
    public Object crePart( long[] partSize ){
        int xx = arrLen( partSize );
        Object arr=null;
        switch( type.val( head.typ )){
            case BYTE:   arr = new byte  [ xx ]; break;
            case SHORT:  arr = new short [ xx ]; break;
            case INT:    arr = new int   [ xx ]; break;
            case LONG:   arr = new long  [ xx ]; break;
            case FLOAT:  arr = new float [ xx ]; break;
            case DOUBLE: arr = new double[ xx ];
        }
        return arr;
    }
    
    public void part( Index idx, Object arr, boolean put ) throws Exception 
    {
        long[][] reInd = Index.realIndex( idx.ii, head );
        long dat = loc + head.off;
        
        switch( reInd.length ){
            case 1:  copy1( dat, head.siz, reInd, arr, put ); break;
            case 2:  copy2( dat, head.siz, reInd, arr, put ); break;
            case 3:  copy3( dat, head.siz, reInd, arr, put ); break;
        }
    }

    private void copy1( long dat, long[] size, long[][] ind, Object arr, boolean put ) throws Exception
    {
        long pmem  = dat + head.nb * ( ind[0][0] - 1 );
        mem.copyArr( pmem, arr, 0, (int)ind[0][1], put );
    }
    
    private void copy2( long dat, long[] size, long[][] ind, Object arr, boolean put ) throws Exception
    {
        long ja=ind[1][0], jb=ja + ind[1][1], i=ind[0][0]-1;  int ii=(int)size[0],  parr=0; 
        for( long j=ja; j<jb; j++ ){
             long pmem  = dat + head.nb * ( ii*(j-1) + i );
tt("........ parr="+parr+", ii="+ii+", pmem="+pmem );             
             mem.copyArr( pmem, arr, parr, ii, put );
             parr += ii; 
        }
    }
    
    private void copy3( long dat, long[] size, long[][] ind, Object arr, boolean put ) throws Exception
    {
        long ka=ind[2][0], kb=ka + ind[2][1];                 int jj=(int)( size[0]*size[1] ); 
        long ja=ind[1][0], jb=ja + ind[1][1], i=ind[0][0]-1;  int ii=(int)  size[0],   parr=0; 
        for( long k=ka; k<kb; k++ ){
             long dk = dat + (k-1)*jj;
             for( long j=ja; j<jb; j++ ){
                 long pmem  = dk + ii*( j-1 ) + i;
                 mem.copyArr( pmem, arr, parr, ii, put );
                 parr += ii; 
            }
        }
    }
    
///* DBG: =====================================================================================================
                                                                static final boolean PUT=true, GET=false;
    public static void main( String[] args ) throws Exception
    {
        LongMem mem = new LongMem( 512+1300 );
        iniBlocks( mem );
        Block aa1 = new Block( "aa1", type.DOUBLE, new long[]{2,3} ); 
        Block lon2 = new Block( "lon2", type.LONG, new long[]{2,3,2}, 16 );lon2.copy(new long[]{-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11,-12}, PUT ); 
        Block byt3 = new Block( "byt3", type.BYTE, new long[]{1,12,2}, 8 );
        Block int4 = new Block( "int4", type.INT,  new long[]{2,3,3} ); int4.copy( new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}, PUT ); 
        Block sh5 = new Block( type.SHORT, new long[]{4,2} ); 
        Block bb6 = new Block( type.BYTE, new long[]{8,2} );  bb6.copy( new byte[]{-1,-2,-3,-4,-5,-6,-7,-8, 1,2,3,4,5,6,7,8}, PUT );
tt("\n_______ 0: "+ bb6.ttBlock());
tt( blocks());

        sh5.delete();
        delete("byt3");
//        lon2.delete();
//        int4.delete();
        delete("aa1");
tt( blocks());

tt("\n_______ 1: "+ bb6.ttBlock());
tt( int4.ttBlock());
tt( lon2.ttBlock());

        Block int7 = new Block( "int7", type.INT,  new long[]{9,2,2} );
        
tt("\n_______ 2: "+ bb6.ttBlock());
tt( int4.ttBlock());
tt( lon2.ttBlock());
        
        Block ijk = new Block("ijk", type.INT, new long[]{ 5,7,9 } );
        int[] dd = new int[ 5*7*9 ]; int p=0;
        for(int k=1;k<=9;k++)
            for(int j=1;j<=7;j++)
                for(int i=1;i<=5;i++) dd[p++] = 100*i + 10*j + k;
        ijk.copy( dd, PUT );
        tt( ijk.ttBlock());
//        Index idx = new Index("2:3,2:5,2:7");
//        Index idx = new Index("2:3");
        Index idx = new Index("2:3,2:5");
Index.tar( idx );        
        int[] part = (int[])ijk.crePart( idx );  String s="part[];";for( int q: part )s+=" "+q; tt( s );

        
        ijk.part( idx, part, GET );  s="part[];";for( int q: part )s+=" "+q; tt( s );
    }
    
    static void tt(String x){System.out.println( x );}
//*/    
}
