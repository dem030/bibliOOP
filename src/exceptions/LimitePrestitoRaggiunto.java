package exceptions;

public class LimitePrestitoRaggiunto extends BibliotecaException {
    public LimitePrestitoRaggiunto(int limite){
        super("Limite prestiti raggiunto: " + limite, "LIMITE_PRESTITO_RAGGIUNTO");
    }

}
