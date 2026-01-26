package exceptions;

public class PrestitoGiaRinnovato extends BibliotecaException {
    public PrestitoGiaRinnovato(int prest_id){
        super("Prestito gia' rinnovato con id: " + prest_id);
    }

}
