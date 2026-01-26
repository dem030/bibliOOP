package models;
import java.util.Date;
import utils.*;

public class rivista extends Materiale {
    int ed_num;
    Date data_pubblicazione;
    public double calcolaPenale(int giorniRitardo, double penaleGiornaliera) {
        return (int)(giorniRitardo * penaleGiornaliera);
    }
    public int getDurataMassimaPrestito() {
        Configurazione conf = new Configurazione(); 
        return conf.getDurataPrestitoRivista();
    }


}
