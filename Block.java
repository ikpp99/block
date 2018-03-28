package com.pik.xmem;

import java.util.HashMap;
import java.util.TreeMap;

public class Block
{
    static protected LongMem mem;

    static protected HashMap< String, Long > blockNamLoc; 
    static protected TreeMap<   Long, Long > freeLocLen;
    
    protected String nam;
    protected Head   head;
    protected long   loc, len;
    
    static public void iniBlockSet( LongMem longMem )
    {
        mem    = longMem;
        freeLocLen = new TreeMap<>();
        freeLocLen.put( 0L, mem.memLen );
        blockNamLoc = new HashMap<>();
    }
    
    public Block( String name, type type, long[] size ) throws Exception { this( name, type, size, 0 );}
    public Block( String name, type type, long[] size, int extLen ) throws Exception 
    {
        head = new Head( type, size, extLen );  len = head.len;
        long sum=0;  boolean done=false;

        for( Long pos: freeLocLen.keySet()){
             Long free = freeLocLen.get( pos ); sum += free; 
             if(  free >= len )
             {
                head.writeHead ( mem , pos ); 
                blockNamLoc.put( name, pos );  nam=name;  loc = pos;
                
                freeLocLen.remove( pos ); 
                free -= len;
                if( free > 0 ) freeLocLen.put( pos+len, free );
                done=true; break;
             }
        }
        if( !done ){
            if( sum < len ) throw new Exception("NO MEMORY");
            throw new Exception("NO Contigues MEMORY");       //TODO squize...
        }
    }

    public void delete() throws Exception  //TODO to be tested
    {
        blockNamLoc.remove( nam ); long nex = loc+len;
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
    
    public void copyExt( byte[] ext, boolean put ) throws Exception {
        if( ext !=null && head.exx >0 ){
            if( put ) head.writeExt( mem, loc, ext );
            else      head.readExt ( mem, loc );
        }
    }
    
}
