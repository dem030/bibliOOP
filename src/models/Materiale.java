package models;
public abstract class Materiale {
     private int id_pz;
    private String titolo;
    private String autore;
    private boolean disponibile;
    public int getId_pz() {
        return id_pz;
    }
    public void setID_pz(int id_pz) {
        this.id_pz= id_pz;
    }
    public String getTitolo() {
        return titolo;
    }
    public void setTitolo(String titolo){
        this.titolo = titolo;
    }
    public String getAutore(){
        return autore;
    }
    public void setAutore(String autore){
        this.autore = autore;
    }
    public boolean isDisponibile() {
        return disponibile;
    }
    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }
    public abstract double calcolaPenale(int giorniRitardo, double penaleGiornaliera);
    public abstract int getDurataMassimaPrestito();
}


