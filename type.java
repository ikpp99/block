package com.pik.xmem;

public enum type 
{
    BYTE   ( 1 ),
    SHORT  ( 2 ),
    INT    ( 4 ),
    LONG   ( 8 ),
    FLOAT  ( 4 ),
    DOUBLE ( 8 );
    
    static private type[] vals={ BYTE, SHORT, INT, LONG, FLOAT, DOUBLE };
    
    private type( int nbytes ){ nb=nbytes;}
    
    private int           nb;
    public  int        getNb   (){ return nb ;}
    static public type val( int i ){ return i<vals.length? vals[ i ]: null;}
}
