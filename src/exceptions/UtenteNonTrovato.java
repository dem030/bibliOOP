package exceptions;

public class UtenteNonTrovato extends BibliotecaException{
    public UtenteNonTrovato(String username){
        super("Utente non trovato con username: " + username);
    }

}
