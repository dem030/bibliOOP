package exceptions;

import java.util.Date;

public class BibliotecaException extends Exception {

    private String messaggio;
    private Date dataOra;
    private String tipoOperazione;

    public BibliotecaException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
        this.dataOra = new Date();
    }

    public BibliotecaException(String messaggio, String tipoOperazione) {
        super(messaggio);
        this.messaggio = messaggio;
        this.tipoOperazione = tipoOperazione;
        this.dataOra = new Date();
    }

    public String getMessaggio() {
        return messaggio;
    }

    public Date getDataOra() {
        return dataOra;
    }

    public String getTipoOperazione() {
        return tipoOperazione;
    }

    @Override
    public String toString() {
        if (tipoOperazione != null) {
            return "[" + dataOra + "] " + tipoOperazione + " - " + messaggio;
        }
        return "[" + dataOra + "] " + messaggio;
    }
}
