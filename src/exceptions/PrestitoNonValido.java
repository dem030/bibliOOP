package exceptions;

public class PrestitoNonValido extends BibliotecaException{
    public PrestitoNonValido(int prest_id){
        super("Prestito non valido con id: " + prest_id, "PRESTITO_NON_VALIDO");
    }

}
