package models;
import java.util.Date;
import utils.*;

public class libro extends Materiale {
    int isbn;
    Date data_pubblicazione;


    public double calcolaPenale(int giorniRitardo, double penaleGiornaliera) {
        return (int)(giorniRitardo * penaleGiornaliera);
    }
    public int getDurataMassimaPrestito() {
        Configurazione conf = new Configurazione(); 
        return conf.getDurataPrestitoLibro();
        
    }
}