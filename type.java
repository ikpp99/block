package com.pik.xmem;

public enum type 
{
    BYTE   ( 1 ),  // 0
    SHORT  ( 2 ),  // 1
    INT    ( 4 ),  // 2
    LONG   ( 8 ),  // 3
    FLOAT  ( 4 ),  // 4
    DOUBLE ( 8 );  // 5
    
    private type( int nbytes ){ nb=nbytes;}
    
    private int           nb;
    public  int        getNb     (){ return nb ;}
    static public type val( int i ){ return i<values().length? values()[ i ]: null;}
}
