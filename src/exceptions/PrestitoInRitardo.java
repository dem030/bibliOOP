package exceptions;

public class PrestitoInRitardo extends BibliotecaException{
    public PrestitoInRitardo(int prest_id){
        super("Prestito in ritardo con id: " + prest_id);
    }
}
