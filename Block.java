package com.pik.xmem;

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
                concatLastHoles();
                sum = writeBlock( name );
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

    private void concatLastHoles() throws Exception {
        Object[] keys = freeLocLen.keySet().toArray();
        int xx = keys.length;
        if( xx < 2 ) throw new Exception("NO MEMORY");
        
        Long las = (Long)keys[ xx-1 ];
        Long lll = freeLocLen.get( las );
        
        Long pre = (Long)keys[ xx-2 ];
        long ppp = freeLocLen.get( pre );
        
        long dat = pre+ppp;
        long lda = las-dat; 
        
        mem.copyLeft( pre, dat, lda );
        
        freeLocLen.remove( pre );
        freeLocLen.remove( las );
        freeLocLen.put( pre+lda, ppp+lll );
        
        for( String name: blockNamBlk.keySet()){
            Block blk = blockNamBlk.get( name );
            if( dat <= blk.loc || blk.loc <=las ) blk.loc-=ppp;
        }
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
    
    public String toString(){ return "Block \""+nam+"\": loc="+loc+", "+head;}
    
    static public String blocks() throws Exception {
        String s="### Blocks:"; int i=0;
        for( String name: blockNamBlk.keySet()) s+="\n    "+(++i)+".\t\t"+getBlock( name );
        s+="\n### Holes:"; i=0; long free=0;
        for( Long p: freeLocLen.keySet()){ 
            long h=freeLocLen.get( p ); free+=h;  
            s+="\n    "+(++i)+".\t\t"+p+"\t\t"+freeLocLen.get( p );
        }
        return s+"\n-------------------------------------------------\n\t\tFree:\t\t"+free+"\n";
    }
    
///* DBG: =====================================================================================================
    
    public static void main( String[] args ) throws Exception
    {
        LongMem mem = new LongMem( 512 );
        iniBlocks( mem );
        Block aa1 = new Block( "aa1", type.DOUBLE, new long[]{2,3} ); 
        Block lon2 = new Block( "lon2", type.LONG, new long[]{2,3,2}, 16 ); 
        Block byt3 = new Block( "byt3", type.BYTE, new long[]{1,12,2}, 8 );
        Block int4 = new Block( "int4", type.INT,  new long[]{2,3,3} ); 
        Block sh5 = new Block( type.SHORT, new long[]{4,2} ); 
        Block bb6 = new Block( type.BYTE, new long[]{8,2} );
        mem.copyArr( 496, new byte[]{-1,-2,-3,-4,-5,-6,-7,-8, 1,2,3,4,5,6,7,8},true );
tt( blocks());

        sh5.delete();
        delete("byt3");
//        lon2.delete();
//        int4.delete();
        delete("aa1");
tt( blocks());

        byte[] bb=new byte[16]; mem.copyArr( 496, bb, false );
        String s="[]:";for( byte b: bb)s+=" "+b; tt( s );

        Block int7 = new Block( "int7", type.INT,  new long[]{9,1,2} );
        
        tt(""+getBlock( "_1" )); 
        
        mem.copyArr( 424, bb, false );
        s="[]:";for( byte b: bb)s+=" "+b; tt( s );
        
tt( blocks());
    }    
    static void tt(String x){System.out.println( x );}
//*/    
}
