
public enum type 
{
    BYTE   ( 1 ),
    SHORT  ( 2 ),
    INT    ( 4 ),
    LONG   ( 8 ),
    FLOAT  ( 4 ),
    DOUBLE ( 8 );
    
    private type( int nbytes ){ nb=nbytes;}
    
    private int    nb; 
    public  int getNb   (){ return nb ;}
}
