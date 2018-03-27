package com.pik.xmem;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class Block
{
    protected String nam;
    protected long len;
    protected int dim, typ, off, extLen;
    protected long[] siz;
    
    public Block( String name ){ nam = name;}
    
    public Block( String name, type type, long[] size, int extByteLen )
    {
        this( name );
        dim = size.length;
        siz = Arrays.copyOf( size, dim );

        typ= type.ordinal();
        len = type.getNb(); for( long d: siz ) len *=d;

        off  = 8*( 2 + dim );
        extLen = extByteLen>0? extByteLen: 0;
        off   += extLen;
        len   += off;
    }

    public Block( String name, type type, long[] size ){ this( name, type, size, 0 );} 
        
    public Block( type type, long[] size ){ this( "def"+(seq++), type, size, 0 );} 
    private static int seq=0;

    public byte[] readExt( LongMem mem, long loc ) throws Exception
    {
        isLoc( loc );
        byte[] ext = null;
        if( extLen >0 ){
            ext = new byte[ extLen ];
            mem.copyArr( loc+8*(2+dim), ext, false ); 
        }
        return ext;
    }
    
    public void writeExt( LongMem mem, long loc, byte[] ext ) throws Exception
    {
        if( ext !=null ){
            isLoc( loc );
            int xx = Math.min( extLen , ext.length );
            if( xx >0 ) mem.copyArr( loc+8*(2+dim), ext, 0, xx, true );
        }
    }
    
    public void readBlockHeader( LongMem mem, long loc ) throws Exception
    {
        isLoc( loc );
        long[] hhl = { 0, 0 };
        mem.copyArr( loc, hhl, false );

        len = hhl[ 0 ];
        off = (int)(  hhl[ 1 ]         & 0xFFFFFFFF );
        typ = (int)(( hhl[ 1 ] >> 32 ) & 0xFFFF );
        dim = (int)(( hhl[ 1 ] >> 48 ) & 0xFFFF );
        siz = new long[ dim ];
        mem.copyArr( loc+16, siz, false );
        
        int d0 = 8*( 2+dim ), lex = off-d0;
        extLen = lex>0? lex: 0;
    }
    
    public void writeBlockHeader( LongMem mem, long loc ) throws Exception
    {
        isLoc( loc );
        int dim2 = 2+dim;                                 //standard siz of header( NO ext )
        long[] hhl = new long[ dim2 ];
        hhl[0] = len;                                     //? (len >> 3) << 16 + 0xFFFE;
        hhl[1] = dim;
        hhl[1] = (((hhl[1] << 16) + typ ) << 32) + off;
        System.arraycopy( siz, 0, hhl, 2, dim );
        
        mem.copyArr( loc, hhl, true );
    }
    
    public String toString()
    {
        String s="Block '"+nam+"': "+type.val( typ )+"[ ";
        try{ for( long ind: siz ) s+=ind+", ";}catch(Exception e){}
        return s.substring( 0, s.length()-2 )+" ], len="+len+", off="+off;
    }
    
    public void isLoc( long loc ) throws Exception{ if( loc%8 !=0 ) throw new Exception("BAD mem Loc8: "+loc);}
    
///* DBG: =====================================================================================================
    
    public static void main( String[] args ) throws Exception
    {
        LongMem mem = new LongMem( 128 );
        
        Block t = new Block("test", type.LONG, new long[]{2,3}, 8 ); 
        t.writeBlockHeader( mem, 0 );
        
        mem.copyArr( 40, new long[]{1,2,3,4,5,6}, true );
        dumpMem( mem );
        
        Block q = new Block( "qqqq" );
        q.readBlockHeader(  mem, 0 );
        tt(""+q );
    }
    static void dumpMem( LongMem mm ){
        String s="### LongMem:"; String dec="dec: ";
        for( byte[] bb: mm.mem ){
            s+="\n"; for(byte b: bb ) s+=" "+(int)b;
            ByteBuffer bbu = ByteBuffer.wrap( bb );
            LongBuffer bbl =bbu.asLongBuffer();
            s+="\nhex: "; 
            int x=bb.length/8;
            for( int i=0;i<x;i++){ s+=" "+Long.toHexString( bbl.get( i )); dec+=" "+bbl.get( i );}
        }
        tt(s); tt(dec); tt("");
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
