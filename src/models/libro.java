package models;
import utils.Configurazione;
public class Libro extends Materiale {
    private String isbn;
    private int annoPubblicazione;
    
    // Costruttore vuoto
    public Libro() {
    }
    
    // Getter e Setter per isbn
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
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
        return Configurazione.getInstance().getDurataPrestitoLibro();
    }
}