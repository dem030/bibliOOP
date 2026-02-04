package models;
import utils.Configurazione;
public class Rivista extends Materiale {
    private int numeroEdizione;
    private int annoPubblicazione;
    
    // Costruttore vuoto
    public Rivista() {
    }
    
    // Getter e Setter per numeroEdizione
    public int getNumeroEdizione() {
        return numeroEdizione;
    }
    
    public void setNumeroEdizione(int numeroEdizione) {
        this.numeroEdizione = numeroEdizione;
    }
    
    // Getter e Setter per annoPubblicazione
    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }
    
    public void setAnnoPubblicazione(int annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }
    
    // Implementazione metodi astratti
    @Override
    public double calcolaPenale(int giorniRitardo, double penaleGiornaliera) {
        return giorniRitardo * penaleGiornaliera;
    }
    
    @Override
    public int getDurataMassimaPrestito() {  
        return Configurazione.getInstance().getDurataPrestitoRivista();
    }
}