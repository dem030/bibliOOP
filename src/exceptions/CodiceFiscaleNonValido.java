package exceptions;

public class CodiceFiscaleNonValido extends  BibliotecaException {
    public CodiceFiscaleNonValido(String codice_fiscale){
        super("Il codice fiscale " + codice_fiscale + " non è valido.");
    }
}
