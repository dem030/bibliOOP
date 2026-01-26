package exceptions;

public class OperazioneNonConsentitaException extends BibliotecaException {

    public OperazioneNonConsentitaException(String operazione) {
        super("Operazione non consentita: " + operazione);
    }

    
    public OperazioneNonConsentitaException(String operazione, String ruolo) {
        super(
            "Operazione non consentita per il ruolo '" + ruolo + "': " + operazione,
            "OPERAZIONE_NON_CONSENTITA"
        );
    }
}
