package exceptions;

public class MaterialeNonTrovato extends BibliotecaException {
    public MaterialeNonTrovato(int id_pz) {
        super("Materiale non trovato con ID: " + id_pz);
    }

}

