package utils;

public class Configurazione {
    private String hostServer;
    private int portaServer;
    private int maxThread;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private int durataPrestitoLibro;
    private int durataPrestitoRivista;
    private int maxPrestitiPerUtente;
    private double penaleGiornalieraLibro;
    private double penaleGiornalieraRivista;
    
    
    public Configurazione() {
    }
    
    public String getHostServer() {
        return hostServer;
    }
    
    public void setHostServer(String hostServer) {
        this.hostServer = hostServer;
    }
    
    public int getPortaServer() {
        return portaServer;
    }
    
    public void setPortaServer(int portaServer) {
        this.portaServer = portaServer;
    }
    public int getMaxThread() {
        return maxThread;
    }
    
    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }
    
    public String getDbUrl() {
        return dbUrl;
    }
    
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    
    public String getDbUsername() {
        return dbUsername;
    }
    
    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }
    
    
    public String getDbPassword() {
        return dbPassword;
    }
    
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    
    
    public int getDurataPrestitoLibro() {
        return durataPrestitoLibro;
    }
    
    public void setDurataPrestitoLibro(int durataPrestitoLibro) {
        this.durataPrestitoLibro = durataPrestitoLibro;
    }
    
    
    public int getDurataPrestitoRivista() {
        return durataPrestitoRivista;
    }
    
    public void setDurataPrestitoRivista(int durataPrestitoRivista) {
        this.durataPrestitoRivista = durataPrestitoRivista;
    }
    
    
    public int getMaxPrestitiPerUtente() {
        return maxPrestitiPerUtente;
    }
    
    public void setMaxPrestitiPerUtente(int maxPrestitiPerUtente) {
        this.maxPrestitiPerUtente = maxPrestitiPerUtente;
    }
    
    
    public double getPenaleGiornalieraLibro() {
        return penaleGiornalieraLibro;
    }
    
    public void setPenaleGiornalieraLibro(double penaleGiornalieraLibro) {
        this.penaleGiornalieraLibro = penaleGiornalieraLibro;
    }
    
    
    public double getPenaleGiornalieraRivista() {
        return penaleGiornalieraRivista;
    }
    
    public void setPenaleGiornalieraRivista(double penaleGiornalieraRivista) {
        this.penaleGiornalieraRivista = penaleGiornalieraRivista;
    }
}