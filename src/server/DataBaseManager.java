package server;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import models.*;
import exceptions.*;
import utils.*;

public class DataBaseManager {


    private static DataBaseManager instance;

    private Connection connection;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private DataBaseManager() {
    }

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    // inizializza connessione al DB

    public void inizializza(String url, String username, String password) throws Exception {
        try {
            this.dbUrl = url;
            this.dbUsername = username;
            this.dbPassword = password;

            Class.forName("com.mysql.cj.jdbc.Driver"); 
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            System.out.println(" Connessione database stabilita");
        } catch (SQLException e) {
            System.err.println("Errore connessione DB: " + e.getMessage());
            throw new Exception("Impossibile connettersi al database", e);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trovato");
            throw new Exception("Driver MySQL non trovato", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void chiudiConnessione() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println(" Connessione chiusa");
        }
    }

    // richieste utente

    
    public int registraUtente(Utente utente) throws Exception {
    try {
        if (!utente.validaCodiceFiscale()) {
            throw new CodiceFiscaleNonValido("Codice fiscale non valido");
        }
        
        String query = "INSERT INTO utente (cod_fiscale, username, password, nome, cognome, email, telefono, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, utente.getCodice_fiscale());
        stmt.setString(2, utente.getUsername());
        stmt.setString(3, utente.getPassword());
        stmt.setString(4, utente.getNome());
        stmt.setString(5, utente.getCognome());
        stmt.setString(6, utente.getEmail());
        stmt.setLong(7, Long.parseLong(utente.getTelefono())); // telefono è BIGINT nel DB
        stmt.setString(8, "USER"); // ✅ USER non UTENTE
        
        stmt.executeUpdate();
        
        ResultSet rs = stmt.getGeneratedKeys();
        int idGenerato = 0;
        if (rs.next()) {
            idGenerato = rs.getInt(1);
        }
        
        stmt.close();
        return idGenerato;
        
    } catch (SQLException e) {
        String messaggioErrore = e.getMessage().toLowerCase();
        if (messaggioErrore.contains("duplicate entry") || messaggioErrore.contains("unique")) {
            throw new Exception("Username o codice fiscale già esistente");
        } else {
            throw new Exception("Errore registrazione: " + e.getMessage());
        }
    }
}

    public Utente autenticaUtente(String username, String password) throws Exception {
        String query = "SELECT * FROM utente WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("bloccato")) {
                        throw new OperazioneNonConsentitaException("Utente bloccato");
                    }

                    Utente u = new Utente();
                    u.setId_ut(rs.getInt("id_ut"));
                    u.setCodice_fiscale(rs.getString("cod_fiscale"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setNome(rs.getString("nome"));
                    u.setCognome(rs.getString("cognome"));
                    u.setEmail(rs.getString("email"));
                    u.setTelefono(rs.getString("telefono"));
                    u.setRuoloUtente(Utente.ruoloUtente.valueOf(rs.getString("ruolo")));
                    u.setBloccato(rs.getBoolean("bloccato"));

                    return u;
                } else {
                    throw new UtenteNonTrovato("Credenziali errate");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore autenticazione: " + e.getMessage());
            throw new Exception("Errore database durante autenticazione", e);
        }
    }

    public List<Materiale> cercaMateriale(String query) throws Exception {
    List<Materiale> listaMateriali = new ArrayList<>();
    
    try {
        String sql = "SELECT * FROM materiale WHERE titolo LIKE ? OR autore LIKE ?";
        
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, "%" + query + "%");
        stmt.setString(2, "%" + query + "%");
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String tipo = rs.getString("tipo");
            
            if (tipo.equals("LIBRO")) {
                Libro libro = new Libro();
                libro.setID_pz(rs.getInt("id_pz")); 
                libro.setTitolo(rs.getString("titolo"));
                libro.setAutore(rs.getString("autore"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setAnnoPubblicazione(rs.getInt("anno_pub"));
                libro.setDisponibile(rs.getBoolean("disp"));
                
                listaMateriali.add(libro);
                
            } else if (tipo.equals("RIVISTA")) {
                Rivista rivista = new Rivista();
                rivista.setID_pz(rs.getInt("id_pz")); 
                rivista.setTitolo(rs.getString("titolo"));
                rivista.setAutore(rs.getString("autore"));
                rivista.setNumeroEdizione(rs.getInt("ed_num")); 
                rivista.setAnnoPubblicazione(rs.getInt("anno_pub")); 
                rivista.setDisponibile(rs.getBoolean("disp")); 
                
                listaMateriali.add(rivista);
            }
        }
        
        stmt.close();
        return listaMateriali;
        
    } catch (SQLException e) {
        System.err.println("Errore ricerca: " + e.getMessage());
        throw new Exception("Errore durante la ricerca dei materiali");
    }
}

    
    public synchronized Prestito prenotaMateriale(int idUtente, int idMateriale, Configurazione config) throws Exception {
        try {
            // STEP 1: Verifica disponibilità materiale
            String query1 = "SELECT * FROM materiale WHERE id_pz = ?";
            String tipo;

            try (PreparedStatement stmt1 = connection.prepareStatement(query1)) {
                stmt1.setInt(1, idMateriale);
                try (ResultSet rs1 = stmt1.executeQuery()) {
                    if (!rs1.next()) {
                        throw new MaterialeNonTrovato(idMateriale);
                    }
                    if (!rs1.getBoolean("disp")) {
                        throw new LibroNonDisponibile("Materiale non disponibile");
                    }
                    tipo = rs1.getString("tipo");
                }
            }

            String query2 = "SELECT COUNT(*) as conteggio FROM prestito WHERE ut_id = ? AND rest_data IS NULL";

            try (PreparedStatement stmt2 = connection.prepareStatement(query2)) {
                stmt2.setInt(1, idUtente);
                try (ResultSet rs2 = stmt2.executeQuery()) {
                    if (rs2.next()) {
                        int conteggio = rs2.getInt("conteggio");
                        if (conteggio >= config.getMaxPrestitiPerUtente()) {
                            throw new LimitePrestitoRaggiunto(conteggio);
                        }
                    }
                }
            }
            
            LocalDate dataOggi = LocalDate.now();
            int durataGiorni = "LIBRO".equals(tipo) ? config.getDurataPrestitoLibro() : config.getDurataPrestitoRivista();
            LocalDate dataScadenza = dataOggi.plusDays(durataGiorni);

            String query3 = "INSERT INTO prestito (ut_id, mat_id,prest_data, scad_data) VALUES (?, ?, ?, ?)";
            int idPrestito = 0;

            try (PreparedStatement stmt3 = connection.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS)) {
                stmt3.setInt(1, idUtente);
                stmt3.setInt(2, idMateriale);
                stmt3.setDate(3, Date.valueOf(dataOggi));
                stmt3.setDate(4, Date.valueOf(dataScadenza));
                stmt3.executeUpdate();

                try (ResultSet rs3 = stmt3.getGeneratedKeys()) {
                    if (rs3.next()) {
                        idPrestito = rs3.getInt(1);
                    }
                }
            }

            String query4 = "UPDATE materiale SET disp = FALSE WHERE id_pz = ?";
            try (PreparedStatement stmt4 = connection.prepareStatement(query4)) {
                stmt4.setInt(1, idMateriale);
                stmt4.executeUpdate();
            }

            Prestito prestito = new Prestito();
            prestito.setId(idPrestito);
            prestito.setDataPrestito(dataOggi);
            prestito.setDataScadenza(dataScadenza);
            prestito.setDataRestituzione(null);
            prestito.setRinnovato(false);
            
            prestito.setPenale(0.0);

            Utente utente = getUtenteById(idUtente);
            prestito.setUtente(utente);

            Materiale materiale = getMaterialeById(idMateriale);
            prestito.setMateriale(materiale);

            return prestito;
        } catch (SQLException e) {
            System.err.println("Errore prenotazione: " + e.getMessage());
            throw new Exception("Errore durante la prenotazione", e);
        }
    }

    public List<Prestito> getPrestiti(int idUtente) throws Exception {
        List<Prestito> listaPrestiti = new ArrayList<>();

        String query = "SELECT p.*, m.*, u.* FROM prestito p " + "JOIN materiale m ON p.mat_id = m.id_pz " + "JOIN utente u ON p.ut_id = u.id_ut " + "WHERE p.ut_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Materiale materiale;
                    String tipo = rs.getString("tipo");

                    if ("LIBRO".equals(tipo)) {
                        Libro libro = new Libro();
                        libro.setID_pz(rs.getInt("id_pz"));
                        libro.setTitolo(rs.getString("titolo"));
                        libro.setAutore(rs.getString("autore"));
                        libro.setIsbn(rs.getString("isbn"));
                        libro.setAnnoPubblicazione(rs.getInt("anno_pub"));
                        libro.setDisponibile(rs.getBoolean("disp"));
                        materiale = libro;
                    } else {
                        Rivista rivista = new Rivista();
                        rivista.setID_pz(rs.getInt("id_pz"));
                        rivista.setTitolo(rs.getString("titolo"));
                        rivista.setAutore(rs.getString("autore"));
                        rivista.setNumeroEdizione(rs.getInt("ed_num"));
                        rivista.setAnnoPubblicazione(rs.getInt("anno_pub"));
                        rivista.setDisponibile(rs.getBoolean("disp"));
                        materiale = rivista;
                    }

                    Utente utente = new Utente();
                    utente.setId_ut(rs.getInt("id_ut"));
                    utente.setUsername(rs.getString("username"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));

                    Prestito prestito = new Prestito();
                    prestito.setId(rs.getInt("prest_id"));
                    prestito.setUtente(utente);
                    prestito.setMateriale(materiale);

                    Date dataPrest = rs.getDate("prest_data");
                    if (dataPrest != null) {
                        prestito.setDataPrestito(dataPrest.toLocalDate());
                    }

                    Date dataScad = rs.getDate("scad_data");
                    if (dataScad != null) {
                        prestito.setDataScadenza(dataScad.toLocalDate());
                    }

                    Date dataRest = rs.getDate("rest_data");
                    if (dataRest != null) {
                        prestito.setDataRestituzione(dataRest.toLocalDate());
                    }

                    prestito.setRinnovato(rs.getBoolean("rinnovato"));
                    prestito.setPenale(rs.getDouble("penale"));

                    listaPrestiti.add(prestito);
                }
            }

            return listaPrestiti;
        } catch (SQLException e) {
            System.err.println("Errore recupero prestiti: " + e.getMessage());
            throw new Exception("Errore durante il recupero dei prestiti", e);
        }
    }

    public double restituisciMateriale(int idPrestito, Configurazione config) throws Exception {
        try {
            String query1 = "SELECT p.*, m.tipo FROM prestito p " + "JOIN materiale m ON p.mat_id = m.id_pz " + "WHERE p.prest_id = ?";

            LocalDate dataScadenza;
            int idMateriale;
            String tipo;

            try (PreparedStatement stmt1 = connection.prepareStatement(query1)) {
                stmt1.setInt(1, idPrestito);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (!rs.next()) {
                        throw new PrestitoNonValido(idPrestito);
                    }

                    if (rs.getDate("rest_data") != null) {
                        throw new PrestitoNonValido(rs.getDate("rest_data"));
                    }

                    Date dataScadDb = rs.getDate("scad_data");
                    if (dataScadDb == null) {
                        throw new PrestitoNonValido(idPrestito);
                    }
                    dataScadenza = dataScadDb.toLocalDate();
                    idMateriale = rs.getInt("mat_id");
                    tipo = rs.getString("tipo");
                }
            }
            // se ritardo, calcolo penale
            LocalDate dataOggi = LocalDate.now();
            long giorniRitardo = java.time.temporal.ChronoUnit.DAYS.between(dataScadenza, dataOggi);
            double penale = 0.0;

            if (giorniRitardo > 0) {
                double penaleGiornaliera = "LIBRO".equals(tipo)
                        ? config.getPenaleGiornalieraLibro()
                        : config.getPenaleGiornalieraRivista();
                penale = giorniRitardo * penaleGiornaliera;
            }

            String query2 = "UPDATE prestito SET rest_data = ?, penale = ? WHERE prest_id = ?";
            try (PreparedStatement stmt2 = connection.prepareStatement(query2)) {
                stmt2.setDate(1, Date.valueOf(dataOggi));
                stmt2.setDouble(2, penale);
                stmt2.setInt(3, idPrestito);
                stmt2.executeUpdate();
            }

            String query3 = "UPDATE materiale SET disp = TRUE WHERE id_pz = ?";
            try (PreparedStatement stmt3 = connection.prepareStatement(query3)) {
                stmt3.setInt(1, idMateriale);
                stmt3.executeUpdate();
            }

            return penale;
        } catch (SQLException e) {
            System.err.println("Errore restituzione: " + e.getMessage());
            throw new Exception("Errore durante la restituzione", e);
        }
    }

    // Rinnova prestito
    public LocalDate rinnovaPrestito(int idPrestito, Configurazione config) throws Exception {
        try {
            String query1 = "SELECT p.*, m.tipo FROM prestito p " +"JOIN materiale m ON p.mat_id = m.id_pz " + "WHERE p.prest_id = ?";
            LocalDate dataScadenza;
            String tipo;
            try (PreparedStatement stmt1 = connection.prepareStatement(query1)) {
                stmt1.setInt(1, idPrestito);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (!rs.next()) {
                        throw new PrestitoNonValido(idPrestito);
                    }

                    if (rs.getBoolean("rinnovato")) {
                        throw new PrestitoGiaRinnovato(idPrestito);
                    }

                    Date dataScadDb = rs.getDate("scad_data");
                    if (dataScadDb == null) {
                        throw new PrestitoNonValido(idPrestito);
                    }
                    dataScadenza = dataScadDb.toLocalDate();
                    tipo = rs.getString("tipo");
                }
            }

            // una volta verificato prestito, controllo ritardo
            LocalDate dataOggi = LocalDate.now();
            if (dataOggi.isAfter(dataScadenza)) {
                throw new PrestitoInRitardo(idPrestito);
            }

            // calcolo rinnovo
            int durataGiorni = "LIBRO".equals(tipo)
                    ? config.getDurataPrestitoLibro()
                    : config.getDurataPrestitoRivista();
            LocalDate nuovaScadenza = dataScadenza.plusDays(durataGiorni);

            // aggiornamento
            String query3 = "UPDATE prestito SET scad_data = ?, rinnovato = TRUE WHERE prest_id = ?";
            try (PreparedStatement stmt3 = connection.prepareStatement(query3)) {
                stmt3.setDate(1, Date.valueOf(nuovaScadenza));
                stmt3.setInt(2, idPrestito);
                stmt3.executeUpdate();
            }

            return nuovaScadenza;
        } catch (SQLException e) {
            System.err.println("Errore rinnovo: " + e.getMessage());
            throw new Exception("Errore durante il rinnovo", e);
        }
    }

    // richieste admin

    
    public int aggiungiMateriale(Materiale materiale) throws Exception {
        String query = "INSERT INTO materiale " + "(tipo, titolo, autore, isbn, anno_pub, ed_num) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            String tipo = (materiale instanceof Libro) ? "LIBRO" : "RIVISTA";

            stmt.setString(1, tipo);
            stmt.setString(2, materiale.getTitolo());
            stmt.setString(3, materiale.getAutore());

            if (materiale instanceof Libro) {
                Libro libro = (Libro) materiale;
                stmt.setString(4, libro.getIsbn());
                stmt.setInt(5, libro.getAnnoPubblicazione());
                stmt.setNull(6, Types.INTEGER);
            } else {
                Rivista rivista = (Rivista) materiale;
                stmt.setNull(4, Types.VARCHAR);
                stmt.setInt(5, rivista.getAnnoPubblicazione());
                stmt.setInt(6, rivista.getNumeroEdizione());
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                int idGenerato = 0;
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                }
                return idGenerato;
            }
        } catch (SQLException e) {
            System.err.println("Errore aggiunta materiale: " + e.getMessage());
            e.printStackTrace();  // DEBUG: mostra stack trace completo
            throw new Exception("Errore durante l'aggiunta del materiale", e);
        }
    }

    public boolean rimuoviMateriale(int idMateriale) throws Exception {
        try {
            String query1 = "SELECT disp FROM materiale WHERE id_pz = ?";
            boolean disponibile;
            try (PreparedStatement stmt1 = connection.prepareStatement(query1)) {
                stmt1.setInt(1, idMateriale);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (!rs.next()) {
                        throw new MaterialeNonTrovato(idMateriale);
                    }
                    disponibile = rs.getBoolean("disp");
                }
            }
            if (!disponibile) {
                throw new MaterialeInPrestito(idMateriale);
            }
            String query2 = "DELETE FROM materiale WHERE id_pz = ?";
            try (PreparedStatement stmt2 = connection.prepareStatement(query2)) {
                stmt2.setInt(1, idMateriale);
                stmt2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Errore rimozione materiale: " + e.getMessage());
            throw new Exception("Errore durante la rimozione del materiale", e);
        }
    }
    public List<Prestito> getTuttiPrestiti() throws Exception {
        List<Prestito> listaPrestiti = new ArrayList<>();

        String query = "SELECT p.*, m.*, u.* FROM prestito p " + "JOIN materiale m ON p.mat_id = m.id_pz " + "JOIN utente u ON p.ut_id = u.id_ut";

        try (PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Materiale materiale;
                String tipo = rs.getString("tipo");

                if ("LIBRO".equals(tipo)) {
                    Libro libro = new Libro();
                    libro.setID_pz(rs.getInt("id_pz"));
                    libro.setTitolo(rs.getString("titolo"));
                    libro.setAutore(rs.getString("autore"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setAnnoPubblicazione(rs.getInt("anno_pub"));
                    libro.setDisponibile(rs.getBoolean("disp"));
                    materiale = libro;
                } else {
                    Rivista rivista = new Rivista();
                    rivista.setID_pz(rs.getInt("id_pz"));
                    rivista.setTitolo(rs.getString("titolo"));
                    rivista.setAutore(rs.getString("autore"));
                    rivista.setNumeroEdizione(rs.getInt("ed_num"));
                    rivista.setAnnoPubblicazione(rs.getInt("anno_pub"));
                    rivista.setDisponibile(rs.getBoolean("disp"));
                    materiale = rivista;
                }
                Utente utente = new Utente();
                utente.setId_ut(rs.getInt("id_ut"));
                utente.setUsername(rs.getString("username"));
                Prestito prestito = new Prestito();
                prestito.setId(rs.getInt("prest_id"));
                prestito.setUtente(utente);
                prestito.setMateriale(materiale);
                Date dataPrest = rs.getDate("prest_data");
                if (dataPrest != null) {
                    prestito.setDataPrestito(dataPrest.toLocalDate());
                }
                Date dataScad = rs.getDate("scad_data");
                if (dataScad != null) {
                    prestito.setDataScadenza(dataScad.toLocalDate());
                }
                Date dataRest = rs.getDate("rest_data");
                if (dataRest != null) {
                    prestito.setDataRestituzione(dataRest.toLocalDate());
                }
                prestito.setPenale(rs.getDouble("penale"));
                listaPrestiti.add(prestito);
            }

            return listaPrestiti;
        } catch (SQLException e) {
            System.err.println("Errore: " + e.getMessage());
            throw new Exception("Errore durante il recupero dei prestiti", e);
        }
    }

    public List<Prestito> getPrestitiInRitardo() throws Exception {
        List<Prestito> listaPrestiti = new ArrayList<>();

        String query = "SELECT p.*, m.*, u.* FROM prestito p " + "JOIN materiale m ON p.mat_id = m.id_pz " + "JOIN utente u ON p.ut_id = u.id_ut " + "WHERE p.scad_data < CURDATE() AND p.rest_data IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Materiale materiale;
                String tipo = rs.getString("tipo");

                if ("LIBRO".equals(tipo)) {
                    Libro libro = new Libro();
                    libro.setID_pz(rs.getInt("id_pz"));
                    libro.setTitolo(rs.getString("titolo"));
                    libro.setAutore(rs.getString("autore"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setAnnoPubblicazione(rs.getInt("anno_pub"));
                    libro.setDisponibile(rs.getBoolean("disp"));
                    materiale = libro;
                } else {
                    Rivista rivista = new Rivista();
                    rivista.setID_pz(rs.getInt("id_pz"));
                    rivista.setTitolo(rs.getString("titolo"));
                    rivista.setAutore(rs.getString("autore"));
                    rivista.setNumeroEdizione(rs.getInt("ed_num"));
                    rivista.setAnnoPubblicazione(rs.getInt("anno_pub"));
                    rivista.setDisponibile(rs.getBoolean("disp"));
                    materiale = rivista;
                }
                Utente utente = new Utente();
                utente.setId_ut(rs.getInt("id_ut"));
                utente.setUsername(rs.getString("username"));
                Prestito prestito = new Prestito();
                prestito.setId(rs.getInt("prest_id"));
                prestito.setUtente(utente);
                prestito.setMateriale(materiale);
                Date dataPrest = rs.getDate("prest_data");
                if (dataPrest != null) {
                    prestito.setDataPrestito(dataPrest.toLocalDate());
                }
                Date dataScad = rs.getDate("scad_data");
                if (dataScad != null) {
                    prestito.setDataScadenza(dataScad.toLocalDate());
                }
                prestito.setPenale(rs.getDouble("penale"));
                listaPrestiti.add(prestito);
            }
            return listaPrestiti;
        } catch (SQLException e) {
            System.err.println("Errore: " + e.getMessage());
            throw new Exception("Errore durante il recupero dei prestiti in ritardo", e);
        }
    }

    
    public void bloccaUtente(int idUtente, boolean blocca) throws Exception {
        try {
            String query1 = "SELECT username, ruolo FROM utente WHERE id_ut = ?";
            String ruolo;
            String username;

            try (PreparedStatement stmt1 = connection.prepareStatement(query1)) {
                stmt1.setInt(1, idUtente);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (!rs.next()) {
                        throw new UtenteNonTrovato("ID: " + idUtente);
                    }
                    ruolo = rs.getString("ruolo");
                    username = rs.getString("username");
                }
            }

            if ("ADMIN".equals(ruolo)) {
                throw new OperazioneNonConsentitaException("Non puoi bloccare un amministratore");
            }

            // una volta verificato se admin, set bloccato
            String query2 = "UPDATE utente SET bloccato = ? WHERE id_ut = ?";
            try (PreparedStatement stmt2 = connection.prepareStatement(query2)) {
                stmt2.setBoolean(1, blocca);
                stmt2.setInt(2, idUtente);
                stmt2.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Errore blocco utente: " + e.getMessage());
            throw new Exception("Errore durante il blocco/sblocco utente", e);
        }
    }

    // metodi recupero dati
    private Utente getUtenteById(int idUtente) throws Exception {
        String query = "SELECT * FROM utente WHERE id_ut = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idUtente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utente utente = new Utente();
                    utente.setId_ut(rs.getInt("id_ut"));
                    utente.setCodice_fiscale(rs.getString("cod_fiscale"));
                    utente.setUsername(rs.getString("username"));
                    utente.setNome(rs.getString("nome"));
                    utente.setCognome(rs.getString("cognome"));
                    utente.setEmail(rs.getString("email"));
                    utente.setTelefono(rs.getString("telefono"));
                    utente.setRuoloUtente(Utente.ruoloUtente.valueOf(rs.getString("ruolo")));
                    utente.setBloccato(rs.getBoolean("bloccato"));
                    return utente;
                } else {
                    throw new UtenteNonTrovato("ID: " + idUtente);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Errore durante il recupero dell'utente", e);
        }
    }

    private Materiale getMaterialeById(int idMateriale) throws Exception {
        String query = "SELECT * FROM materiale WHERE id_pz = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idMateriale);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");

                    if ("LIBRO".equals(tipo)) {
                        Libro libro = new Libro();
                        libro.setID_pz(idMateriale);
                        libro.setTitolo(rs.getString("titolo"));
                        libro.setAutore(rs.getString("autore"));
                        libro.setIsbn(rs.getString("isbn"));
                        libro.setAnnoPubblicazione(rs.getInt("anno_pub"));
                        libro.setDisponibile(rs.getBoolean("disp"));
                        return libro;
                    } else if ("RIVISTA".equals(tipo)) {
                        Rivista rivista = new Rivista();
                        rivista.setID_pz(rs.getInt("id_pz"));
                        rivista.setTitolo(rs.getString("titolo"));
                        rivista.setAutore(rs.getString("autore"));
                        rivista.setNumeroEdizione(rs.getInt("ed_num"));
                        rivista.setAnnoPubblicazione(rs.getInt("anno_pub"));
                        rivista.setDisponibile(rs.getBoolean("disp"));
                        return rivista;
                    } else {
                        throw new MaterialeNonTrovato(idMateriale);
                    }
                } else {
                    throw new MaterialeNonTrovato(idMateriale);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Errore durante il recupero del materiale", e);
        }
    }
}
