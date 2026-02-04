package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestito {  
    private int id;
    private Utente utente;  
    private Materiale materiale;  
    private LocalDate dataPrestito;  
    private LocalDate dataScadenza;
    private LocalDate dataRestituzione;
    private boolean rinnovato;
    private double penale;
    
    
    public Prestito() {
    }
    
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Utente getUtente() {
        return utente;
    }
    
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    
    public Materiale getMateriale() {
        return materiale;
    }
    
    public void setMateriale(Materiale materiale) {
        this.materiale = materiale;
    }
    
    public LocalDate getDataPrestito() {
        return dataPrestito;
    }
    
    public void setDataPrestito(LocalDate dataPrestito) {
        this.dataPrestito = dataPrestito;
    }
    
    public LocalDate getDataScadenza() {
        return dataScadenza;
    }
    
    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }
    
    public LocalDate getDataRestituzione() {
        return dataRestituzione;
    }
    
    public void setDataRestituzione(LocalDate dataRestituzione) {
        this.dataRestituzione = dataRestituzione;
    }
    
    public boolean isRinnovato() {
        return rinnovato;
    }
    
    public void setRinnovato(boolean rinnovato) {
        this.rinnovato = rinnovato;
    }
    
    public double getPenale() {
        return penale;
    }
    
    public void setPenale(double penale) {
        this.penale = penale;
    }
    
    
    
    public boolean isInRitardo() {
        if (dataRestituzione != null) {
            return false;  
        }
        LocalDate oggi = LocalDate.now();
        return oggi.isAfter(dataScadenza);
    }
    
    public int getGiorniRitardo() {
        if (dataRestituzione != null) {
            
            if (dataRestituzione.isAfter(dataScadenza)) {
                return (int) ChronoUnit.DAYS.between(dataScadenza, dataRestituzione);
            }
            return 0;
        } else {
            
            LocalDate oggi = LocalDate.now();
            if (oggi.isAfter(dataScadenza)) {
                return (int) ChronoUnit.DAYS.between(dataScadenza, oggi);
            }
            return 0;
        }
    }
    
    public double calcolaPenale(double penaleGiornaliera) {
        int giorniRitardo = getGiorniRitardo();
        if (giorniRitardo > 0 && materiale != null) {
            return materiale.calcolaPenale(giorniRitardo, penaleGiornaliera);
        }
        return 0.0;
    }
    
    public void rinnova(int giorniAggiuntivi) throws Exception {
        if (rinnovato) {
            throw new Exception("Prestito già rinnovato");
        }
        if (isInRitardo()) {
            throw new Exception("Non puoi rinnovare un prestito in ritardo");
        }
        this.dataScadenza = this.dataScadenza.plusDays(giorniAggiuntivi);
        this.rinnovato = true;
    }
}