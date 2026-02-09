package models;
import utils.Configurazione;
public class Libro extends Materiale {
    private String isbn;
    private int annoPubblicazione;
    
    
    public Libro() {
    }
    
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    
    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }
    
    public void setAnnoPubblicazione(int annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }
    
    @Override
    public double calcolaPenale(int giorniRitardo, double penaleGiornaliera) {
        return giorniRitardo * penaleGiornaliera;
    }
    
    @Override
    public int getDurataMassimaPrestito() {
        return Configurazione.getInstance().getDurataPrestitoLibro();
    }
}