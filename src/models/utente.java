package models;

public class Utente{
    private int id_ut;
    private String codice_fiscale;
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;
    public enum ruoloUtente {USER, ADMIN};
    private ruoloUtente ruoloUtente;
    private boolean Bloccato;
    public int getId_ut() {
        return id_ut;
    }
    public String getCodice_fiscale() {
        return codice_fiscale;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getNome() {
        return nome;
    }
    public String getCognome() {
        return cognome;
    }
    public String getEmail() {
        return email;
    }
    public String getTelefono() {
        return telefono;
    }
    public boolean isBloccato() {
        return Bloccato;
    }
    public ruoloUtente getRuoloUtente() {
        return ruoloUtente;
    }
    public void setId_ut(int id_ut) {
        this.id_ut = id_ut;
    }
    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBloccato(boolean bloccato) {
        Bloccato = bloccato;
    }
    public void setRuoloUtente(ruoloUtente ruoloUtente) {
        this.ruoloUtente = ruoloUtente;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public boolean validaCodiceFiscale() {
        if (this.codice_fiscale == null) {
            return false;
        }else if (this.codice_fiscale.length() != 16) {
            return false;
        }
        for (char c : this.codice_fiscale.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean validaEmail() {
        if (this.email == null || this.email.isEmpty()) {
            return false;
        }
        // Controlla formato base: qualcosa@qualcosa.qualcosa
        int atIndex = this.email.indexOf('@');
        if (atIndex <= 0) {
            return false;
        }
        int dotIndex = this.email.lastIndexOf('.');
        if (dotIndex <= atIndex + 1 || dotIndex == this.email.length() - 1) {
            return false;
        }
        return true;
    }

    public boolean validaTelefono() {
        if (this.telefono == null || this.telefono.isEmpty()) {
            return false;
        }
        // Rimuovi spazi e trattini
        String tel = this.telefono.replaceAll("[\\s-]", "");
        // Può iniziare con +
        if (tel.startsWith("+")) {
            tel = tel.substring(1);
        }
        // Deve contenere solo cifre e avere lunghezza 9-15
        if (tel.length() < 9 || tel.length() > 15) {
            return false;
        }
        for (char c : tel.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}