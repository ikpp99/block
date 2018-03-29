package com.pik.xmem;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class Block
{
    static protected LongMem mem;

    static protected LinkedHashMap< String, Long > blockNamLoc; 
    static protected TreeMap<   Long, Long > freeLocLen;
    static private int Seq = 0; 
    
    protected long    loc;
    protected String  nam;
    protected Head    head;
    
    static public void iniBlocks( LongMem longMem )
    {
        mem    = longMem;
        freeLocLen = new TreeMap<>();
        freeLocLen.put( 0L, mem.memLen );
        blockNamLoc = new LinkedHashMap<>();
    }
    
    public static Block getBlock( String name ) throws Exception
    {
        Block qq=null;
        Long pos = blockNamLoc.get( name );
        if(  pos !=null ){
             Head hh = new Head();
             hh.readHead( mem, pos );
             qq = new Block( pos, name, hh );
        }
        return qq;
    }
    private Block( long loc, String name, Head head ){ this.loc=loc; this.nam=name; this.head=head;}  
    
    public  Block( type type, long[] size ) throws Exception { this("_"+(Seq++) , type, size, 0 );}
    public  Block( String name, type type, long[] size ) throws Exception { this( name, type, size, 0 );}
    public  Block( String name, type type, long[] size, int extLen ) throws Exception 
    {
        head = new Head( type, size, extLen );
        long sum=0;  boolean done=false;

        for( Long pos: freeLocLen.keySet()){
             Long free = freeLocLen.get( pos ); sum += free; 
             if(  free >= head.len )
             {
                if( blockNamLoc.get( name ) !=null ) throw new Exception("Block ALREADY EXISTS, "+name); 
                 
                head.writeHead ( mem , pos ); 
                blockNamLoc.put( name, pos );  nam=name;  loc = pos;
                
                freeLocLen.remove( pos ); 
                free -= head.len;
                if( free > 0 ) freeLocLen.put( pos+head.len, free );
                done=true; break;
             }
        }
        if( !done ){
            if( sum < head.len ) throw new Exception("NO MEMORY");
            throw new Exception("NO Contigues MEMORY "+head.len);       //TODO squize...
        }
    }

    public void delete() throws Exception  //TODO to be tested
    {
        long len = head.len;
        blockNamLoc.remove( nam );  long nex = loc + len;
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
        for( String name: blockNamLoc.keySet()) s+="\n    "+(++i)+".\t\t"+getBlock( name );
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
tt( blocks());

        byte[] bb=new byte[16]; mem.copyArr( 496, bb, false );
        String s="[]:";for( byte b: bb)s+=" "+b; tt( s );

        Block int7 = new Block( "int4", type.INT,  new long[]{9,1,2} );
    }    
    static void tt(String x){System.out.println( x );}
//*/    
}
