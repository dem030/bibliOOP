package models;
import java.util.Date;
public class prestito {
    private int prest_id;
    private int utente_id;
    private int materiale_id;
    public Date data_prestito;
    public Date data_restituzione;
    public boolean restituito;
    
    public int getPrest_id() {
        return prest_id;
    }
    public int getUtente_id() {
        return utente_id;
    }
    public int getMateriale_id() {
        return materiale_id;
    }
    public int setPrest_id(int prest_id) {
        return this.prest_id = prest_id;
    }
    public int setUtente_id(int utente_id) {
        return this.utente_id = utente_id;
    }
    public int setMateriale_id(int materiale_id) {
        return this.materiale_id = materiale_id;
    }
    public double calcolaPenale(int giorniRitardo, double penaleGiornaliera) {
        return (int)(giorniRitardo * penaleGiornaliera);
    }
    public boolean isInRitardo() {
        Date today = new Date();
        if (today.after(data_restituzione) && !restituito) {
            return true;
        } else {
            return false;
        }
    }
    public double getGiorniRitardo() {
         return (new Date().getTime() - data_restituzione.getTime()) / (1000 * 60 * 60 * 24);
    }

    public boolean rinnova(){
        if(!isInRitardo()){
            // estendi la data di restituzione in base al tipo di materiale

            return true;
        }
        return false;
    }
    
}
