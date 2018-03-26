package com.pik.xmem;

import java.util.Arrays;

public class Block
{
    protected String nam;
    protected long len;
    protected int dim, typ, off;
    protected long[] siz;
    protected byte[] ext;
    
    public Block( String name ){ nam = name;}
    
    public Block( String name, type type, long[] size, byte[] extbytes )
    {
        this( name );
        dim = size.length;
        siz = Arrays.copyOf( size, dim );

        typ= type.ordinal();
        len = type.getNb(); for( long d: siz ) len *=d;

        int extlen = 0;
        if( extbytes !=null ){
            extlen = 8*(( extbytes.length+7 )/8);
            ext = Arrays.copyOf( extbytes, extlen );
        }
        else ext=null;
        
        off  = 8*( 2 + dim ) + extlen;
        len += off;
    }

    public Block( String name, type type, long[] size ){ this( name, type, size, null );} 
        
    public Block( type type, long[] size ){ this( "def"+(seq++), type, size, null );} 
    private static int seq=0;
    
    public void readBlockHeader( LongMem mem, long loc ) throws Exception{ if( loc%8 !=0 ) throw new Exception("BAD mem Loc8: "+loc);
    
        long[] hhl = { 0, 0 };
        mem.copyArr( loc, hhl, 0, 2, type.LONG, false );

        len = hhl[ 0 ];
        off = (int)(  hhl[ 1 ]         & 0xFFFFFFFF );
        typ = (int)(( hhl[ 1 ] >> 32 ) & 0xFFFF );
        dim = (int)(( hhl[ 1 ] >> 48 ) & 0xFFFF );
        siz = new long[ dim ];
        mem.copyArr( loc+16, siz, 0, dim, type.LONG, false );
        
        int d0 = 8*( dim+2 ), lex = off-d0;
        if( lex > 0 ){
            ext = new byte[ lex ];
            mem.copyArr( loc+d0, ext, 0, lex, type.BYTE, false ); 
        }
        else ext = null;    
    }
    
    public void writeBlockHeader( LongMem mem, long loc ) throws Exception{ if( loc%8 !=0 ) throw new Exception("BAD mem Loc8: "+loc);
    
        int dim2 = 2+dim;                                 //standard siz of header( NO ext )
        long[] hhl = new long[ dim2 ];
        hhl[0] = len;                                     //? (len >> 3) << 16 + 0xFFFE;
        hhl[1] = dim;
        hhl[1] = ( hhl[1] << 16 + typ ) << 32 + off;
tt("0: "+hhl[0]+", 1: "+Long.hhl[1]);        
        System.arraycopy( siz, 0, hhl, 2, dim );
        
        mem.copyArr( loc, hhl, 0, dim2, type.LONG, true );
        
        if( ext !=null ) mem.copyArr( loc + 8*dim2, ext, 0, ext.length, type.BYTE, true );
    }
    public String toString(){
        String s="Block: len="+len+", off="+off+", typ="+typ+"( "+type.val( typ )+"), dim="+dim+": ";
        try{ for( long ind: siz ) s+=ind+", ";}catch(Exception e){}
        return s;
    }
    
///* DBG:   
    public static void main( String[] args ) throws Exception
    {
        LongMem mem = new LongMem( 128 );
        
        Block t = new Block("test", type.LONG, new long[]{2,3,4,5}, new byte[]{1,2,3,4,5,6,7} );
        
        t.writeBlockHeader( mem, 0 );
        mem.copyArr( 56, new byte[]{-1,-2,-3}, 0, 3, type.BYTE, true );
dumpMem( mem );
        
        Block q = new Block( "qqqq" );
        q.readBlockHeader(  mem, 0 );
        tt( ""+q );
    }
    static void dumpMem( LongMem mm ){
        String s="### LongMem:";
        for( byte[] bb: mm.mem ){
            s+="\n"; for(byte b: bb ) s+=" "+(int)b;
        }
        tt(s);tt("");
    }
    static void tt(String x){System.out.println( x );}
//*/    
}
