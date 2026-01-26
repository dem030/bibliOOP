package exceptions;

public class PrenotazioneNonValida extends BibliotecaException{
    public PrenotazioneNonValida(String messaggio) {
        super(messaggio, "Prenotazione Non Valida");
    }

}
