package server;
import java.sql.*;
import exceptions.*;
import models.Libro;
import models.Materiale;
import models.libro;
import models.utente;
import java.util.ArrayList;
import java.util.List;
public class DataBaseManager {
    static private DataBaseManager istance = null;
    private Connection conn;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    String query;
    private DataBaseManager() {
    }
    public static DataBaseManager getIstance() {
        if (istance == null) {
            istance = new DataBaseManager();
        }
        return istance;
    }
    public void inizializza(String dburl, String username, String password) throws Exception{
        try {
            this.dbUrl= dburl;
            this.dbUsername= username;
            this.dbPassword= password;
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dburl, username, password);

        } catch (SQLException e) {
            System.err.println("Errore di connessione al database: " + e.getMessage());
            throw new Exception();
        }catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trovato: " + e.getMessage());
            throw new Exception();
        }
    }
    public Connection getConnection() {
        return conn;
    }
    public int registraUtente(utente u) throws Exception{
        try {
            if (u.validaCodiceFiscale()) {
                throw new CodiceFiscaleInvalidoException();
            }
                query = "INSERT INTO utenti (codice_fiscale, username, password, nome, cognome, email, ruoloUtente, Bloccato) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, u.getCodice_fiscale());
                stmt.setString(2, u.getUsername());
                stmt.setString(3, u.getPassword());
                stmt.setString(4, u.getNome());
                stmt.setString(5, u.getCognome());
                stmt.setString(6, u.getEmail());
                stmt.setObject(7, u.getRuoloUtente());
                stmt.setBoolean(8, u.isBloccato());
                stmt.executeUpdate();
                ResultSet rst = stmt.getGeneratedKeys();
                if (rst.next()) {
                    int idGenerato = rst.getInt(1);
                    return idGenerato;
                }
            
            stmt.close();
   } catch (SQLException e) {
    if (e.getMessage().contains("Duplicate entry")){
        throw new Exception("Username o codice fiscale già presente nel sistema");
        }else{
            throw new Exception("Errore durante la registrazione dell'utente: " + e.getMessage());
            }
    
        }
    }
    public utente autenticaUtente(String username, String password) throws Exception{
        try {
            query = "SELECT * FROM utenti WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) {
                if (rst.getBoolean("bloccato")) {
                    throw new OperazioneNonConsentitaException("utente bloccato");
                }
                utente u = new utente();
                u.setId_ut(rst.getInt("id_ut"));
                u.setCodice_fiscale(rst.getString("codice_fiscale"));
                u.setUsername(rst.getString("username"));
                u.setPassword(rst.getString("password"));
                u.setNome(rst.getString("nome"));
                u.setCognome(rst.getString("cognome"));
                u.setEmail(rst.getString("email"));
                u.setRuoloUtente(utente.ruoloUtente.valueOf(rst.getString("ruoloUtente")));
                u.setBloccato(rst.getBoolean("bloccato"));
                stmt.close();
                return u;
            }else{
                stmt.close();
                throw new UtenteNonTrovato("credenziali errate");
            }
        }catch(SQLException e){
            System.err.println("Errore durante l'autenticazione dell'utente: " + e.getMessage());
            throw new Exception();
        }
    }


    public List<Materiale> cercaMateriale(String query) throws Exception {
    List<Materiale> listaMateriali = new ArrayList<>();
    
    try {
        String sql = "SELECT * FROM materiali WHERE titolo LIKE ? OR autore LIKE ?";
        
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, "%" + query + "%");
        stmt.setString(2, "%" + query + "%");
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String tipo = rs.getString("tipo");
            
            if (tipo.equals("LIBRO")) {
                Materiale libroMateriale = new Libro();
                Libro libro = (Libro) libroMateriale;
                libroMateriale.setID_pz(rs.getInt("id"));
                libroMateriale.setTitolo(rs.getString("titolo"));
                libroMateriale.setAutore(rs.getString("autore"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setAnnoPubblicazione(rs.getInt("anno_pubblicazione"));
                libroMateriale.setDisponibile(rs.getBoolean("disponibile"));
                listaMateriali.add(libro);
                listaMateriali.add(libroMateriale);
            } else if (tipo.equals("RIVISTA")) {
                Materiale rivistaMateriale = new Rivista();
                Rivista rivista = (Rivista) rivistaMateriale;
                rivistaMateriale.setID_pz(rs.getInt("id"));
                rivistaMateriale.setTitolo(rs.getString("titolo"));
                rivistaMateriale.setAutore(rs.getString("autore"));
                rivista.setNumeroEdizione(rs.getInt("numero_edizione"));
                rivista.setAnnoPubblicazione(rs.getInt("anno_pubblicazione"));
                rivistaMateriale.setDisponibile(rs.getBoolean("disponibile"));
                listaMateriali.add(rivista);
                listaMateriali.add(rivistaMateriale);
            }
        }
        
        stmt.close();
        return listaMateriali;
        
    } catch (SQLException e) {
        System.err.println("Errore ricerca: " + e.getMessage());
        throw new Exception("Errore durante la ricerca dei materiali");
    }
}


}