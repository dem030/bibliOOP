package exceptions;

public class MaterialeInPrestito extends BibliotecaException {
    public MaterialeInPrestito(int id_pz) 
    {
        super("Il materiale con ID: " + id_pz + " è attualmente in prestito.");
    }
    

}
