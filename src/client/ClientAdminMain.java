package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.time.LocalDate;
import models.*;

public class ClientAdminMain {
    // Attributi
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Utente adminCorrente;
    private Scanner scanner;
    private boolean connesso;
    
    // Main
    public static void main(String[] args) {
        ClientAdminMain client = new ClientAdminMain();
        client.avvia();
    }
    
    // Metodo avvia
    public void avvia() {
        try {
            scanner = new Scanner(System.in);
            
            System.out.println("===================================");
            System.out.println("   BIBLIOTECA - CLIENT ADMIN");
            System.out.println("===================================");
            System.out.println();
            
            // Connessione al server
            connetti("localhost", 8080);
            
            // Login amministratore
            boolean autenticato = false;
            while (!autenticato) {
                autenticato = loginAdmin();
                if (!autenticato) {
                    System.out.println("Vuoi riprovare? (s/n): ");
                    String risposta = scanner.nextLine();
                    if (!risposta.equalsIgnoreCase("s")) {
                        disconnetti();
                        return;
                    }
                }
            }
            
            // Loop menu operazioni admin
            while (connesso) {
                mostraMenuAdmin();
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    // Connessione al server
    private void connetti(String host, int porta) throws Exception {
        try {
            System.out.println("Connessione al server " + host + ":" + porta + "...");
            
            socket = new Socket(host, porta);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            
            in = new ObjectInputStream(socket.getInputStream());
            
            connesso = true;
            System.out.println("✓ Connesso al server!");
            System.out.println();
            
        } catch (IOException e) {
            System.err.println("✗ Impossibile connettersi al server: " + e.getMessage());
            System.err.println("Assicurati che il server sia avviato.");
            throw new Exception("Connessione fallita");
        }
    }
    
    // Login amministratore
    private boolean loginAdmin() {
        try {
            System.out.println();
            System.out.println("=== LOGIN AMMINISTRATORE ===");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            // Invia comando al server
            String comando = "LOGIN:" + username + ":" + password;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                // Parsa dati utente
                String datiUtente = risposta.substring(3);
                adminCorrente = parseUtente(datiUtente);
                
                // Verifica che sia ADMIN
                if (!adminCorrente.getRuoloUtente().toString().equals("ADMIN")) {
                    System.out.println("✗ Accesso negato: non sei un amministratore");
                    adminCorrente = null;
                    return false;
                }
                
                System.out.println();
                System.out.println("✓ Login effettuato con successo!");
                System.out.println("Benvenuto, Amministratore " + adminCorrente.getNome() + "!");
                System.out.println();
                
                return true;
                
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Login fallito: " + messaggioErrore);
                System.out.println();
                return false;
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
            return false;
        }
    }
    
    // Menu amministratore
    private void mostraMenuAdmin() {
        try {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("         MENU AMMINISTRATORE");
            System.out.println("==========================================");
            System.out.println("1. Aggiungi materiale");
            System.out.println("2. Rimuovi materiale");
            System.out.println("3. Visualizza tutti i prestiti");
            System.out.println("4. Visualizza prestiti in ritardo");
            System.out.println("5. Blocca/Sblocca utente");
            System.out.println("6. Visualizza statistiche");
            System.out.println("7. Cerca materiale");
            System.out.println("0. Logout");
            System.out.println("==========================================");
            System.out.print("Scelta: ");
            
            int scelta = scanner.nextInt();
            scanner.nextLine(); // consuma newline
            
            switch (scelta) {
                case 1:
                    aggiungiMateriale();
                    break;
                case 2:
                    rimuoviMateriale();
                    break;
                case 3:
                    visualizzaTuttiPrestiti();
                    break;
                case 4:
                    visualizzaPrestitiInRitardo();
                    break;
                case 5:
                    bloccaSbloccaUtente();
                    break;
                case 6:
                    visualizzaStatistiche();
                    break;
                case 7:
                    cercaMateriale();
                    break;
                case 0:
                    logout();
                    break;
                default:
                    System.out.println("Scelta non valida");
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            scanner.nextLine(); // pulisci buffer
        }
    }
    
    // Aggiungi materiale
    private void aggiungiMateriale() {
        try {
            System.out.println();
            System.out.println("=== AGGIUNGI MATERIALE ===");
            System.out.println("Tipo (1=Libro, 2=Rivista): ");
            int tipo = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Titolo: ");
            String titolo = scanner.nextLine();
            
            System.out.print("Autore: ");
            String autore = scanner.nextLine();
            
            String tipoStr;
            String campo3; // isbn o numero edizione
            
            if (tipo == 1) {
                tipoStr = "LIBRO";
                System.out.print("ISBN: ");
                campo3 = scanner.nextLine();
            } else {
                tipoStr = "RIVISTA";
                System.out.print("Numero Edizione: ");
                campo3 = scanner.nextLine();
            }
            
            System.out.print("Anno Pubblicazione: ");
            int anno = scanner.nextInt();
            scanner.nextLine();
            
            // Invia comando
            String comando = "AGGIUNGI_MATERIALE:" + tipoStr + ";" + titolo + ";" + autore + ";" + campo3 + ";" + anno;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String messaggio = risposta.substring(3);
                System.out.println("✓ " + messaggio);
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Rimuovi materiale
    private void rimuoviMateriale() {
        try {
            System.out.println();
            System.out.println("=== RIMUOVI MATERIALE ===");
            System.out.print("Inserisci ID del materiale: ");
            int idMateriale = scanner.nextInt();
            scanner.nextLine();
            
            // Conferma
            System.out.print("Sei sicuro di voler rimuovere questo materiale? (s/n): ");
            String conferma = scanner.nextLine();
            
            if (!conferma.equalsIgnoreCase("s")) {
                System.out.println("Operazione annullata");
                return;
            }
            
            // Invia comando
            String comando = "RIMUOVI_MATERIALE:" + idMateriale;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                System.out.println("✓ Materiale rimosso con successo");
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Visualizza tutti i prestiti
    private void visualizzaTuttiPrestiti() {
        try {
            System.out.println();
            System.out.println("=== TUTTI I PRESTITI ===");
            
            // Invia comando
            String comando = "TUTTI_PRESTITI";
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String dati = risposta.substring(3);
                
                if (dati.isEmpty() || dati.equals("Nessun prestito trovato")) {
                    System.out.println("Nessun prestito presente nel sistema");
                } else {
                    // Parsa lista prestiti
                    String[] prestiti = dati.split("\\|\\|");
                    
                    System.out.println();
                    System.out.println("Prestiti trovati: " + prestiti.length);
                    System.out.println("==========================================");
                    
                    for (int i = 0; i < prestiti.length; i++) {
                        String[] parti = prestiti[i].split(";");
                        stampaPrestito(parti);
                        System.out.println("==========================================");
                    }
                }
                
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Errore: " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Visualizza prestiti in ritardo
    private void visualizzaPrestitiInRitardo() {
        try {
            System.out.println();
            System.out.println("=== PRESTITI IN RITARDO ===");
            
            // Invia comando
            String comando = "PRESTITI_RITARDO";
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String dati = risposta.substring(3);
                
                if (dati.isEmpty() || dati.equals("Nessun prestito in ritardo")) {
                    System.out.println("✓ Nessun prestito in ritardo");
                } else {
                    // Parsa lista prestiti
                    String[] prestiti = dati.split("\\|\\|");
                    
                    System.out.println();
                    System.out.println("⚠️  Prestiti in ritardo: " + prestiti.length);
                    System.out.println("==========================================");
                    
                    for (int i = 0; i < prestiti.length; i++) {
                        String[] parti = prestiti[i].split(";");
                        stampaPrestito(parti);
                        System.out.println("==========================================");
                    }
                }
                
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Errore: " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Blocca/Sblocca utente
    private void bloccaSbloccaUtente() {
        try {
            System.out.println();
            System.out.println("=== BLOCCA/SBLOCCA UTENTE ===");
            System.out.print("Inserisci ID utente: ");
            int idUtente = scanner.nextInt();
            scanner.nextLine();
            
            System.out.println("Azione:");
            System.out.println("1. Blocca");
            System.out.println("2. Sblocca");
            System.out.print("Scelta: ");
            int azione = scanner.nextInt();
            scanner.nextLine();
            
            boolean blocca = (azione == 1);
            
            // Invia comando
            String comando = "BLOCCA_UTENTE:" + idUtente + ":" + blocca;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String messaggio = risposta.substring(3);
                System.out.println("✓ " + messaggio);
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Visualizza statistiche
    private void visualizzaStatistiche() {
        try {
            System.out.println();
            System.out.println("=== STATISTICHE SISTEMA ===");
            
            // Invia comando
            String comando = "STATISTICHE";
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String dati = risposta.substring(3);
                
                // Per ora stampa i dati grezzi
                // Puoi migliorare il parsing in base a come implementi le statistiche nel server
                System.out.println();
                System.out.println("Statistiche:");
                System.out.println(dati);
                
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Errore: " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Cerca materiale (stesso del client utente)
    private void cercaMateriale() {
        try {
            System.out.println();
            System.out.println("=== CERCA MATERIALE ===");
            System.out.print("Inserisci titolo o autore: ");
            String query = scanner.nextLine();
            
            // Invia comando
            String comando = "CERCA_MATERIALE:" + query;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String dati = risposta.substring(3);
                
                if (dati.equals("Nessun risultato trovato")) {
                    System.out.println("Nessun materiale trovato");
                } else {
                    // Parsa lista materiali
                    String[] materiali = dati.split("\\|\\|");
                    
                    System.out.println();
                    System.out.println("Risultati trovati: " + materiali.length);
                    System.out.println("----------------------------------------");
                    
                    for (int i = 0; i < materiali.length; i++) {
                        String[] parti = materiali[i].split(";");
                        stampaMateriale(parti);
                        System.out.println("----------------------------------------");
                    }
                }
                
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Errore: " + messaggioErrore);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Logout
    private void logout() {
        try {
            System.out.println();
            System.out.println("Logout in corso...");
            
            // Invia comando EXIT
            out.writeObject("EXIT");
            out.flush();
            
            disconnetti();
            
            System.out.println("✓ Disconnesso dal server");
            System.out.println("Arrivederci, Amministratore!");
            
        } catch (IOException e) {
            System.err.println("Errore durante logout: " + e.getMessage());
        }
    }
    
    // Disconnetti
    private void disconnetti() {
        try {
            connesso = false;
            
            if (in != null) {
                in.close();
            }
            
            if (out != null) {
                out.close();
            }
            
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            
        } catch (IOException e) {
            System.err.println("Errore chiusura connessione: " + e.getMessage());
        }
    }
    
    // ========== METODI HELPER PARSING ==========
    
    private Utente parseUtente(String dati) {
        // Formato: id;username;nome;cognome;ruolo;bloccato
        String[] parti = dati.split(";");
        
        Utente utente = new Utente();
        utente.setId_ut(Integer.parseInt(parti[0]));
        utente.setUsername(parti[1]);
        utente.setNome(parti[2]);
        utente.setCognome(parti[3]);
        // Salta ruolo per ora (gestito con enum)
        
        return utente;
    }
    
    // ========== METODI HELPER STAMPA ==========
    
    private void stampaMateriale(String[] parti) {
        // Formato: id;tipo;titolo;autore;disponibile;extra1;extra2
        System.out.println("ID: " + parti[0]);
        System.out.println("Tipo: " + parti[1]);
        System.out.println("Titolo: " + parti[2]);
        System.out.println("Autore: " + parti[3]);
        System.out.println("Disponibile: " + (parti[4].equals("true") ? "Sì" : "No"));
        
        if (parti[1].equals("LIBRO")) {
            System.out.println("ISBN: " + parti[5]);
            System.out.println("Anno: " + parti[6]);
        } else {
            System.out.println("Numero Edizione: " + parti[5]);
            System.out.println("Anno: " + parti[6]);
        }
    }
    
    private void stampaPrestito(String[] parti) {
        // Formato: id;idUtente;userUtente;idMat;titoloMat;dataPrest;dataScad;dataRest;penale;ritardo
        System.out.println("ID Prestito: " + parti[0]);
        System.out.println("Utente: " + parti[2] + " (ID: " + parti[1] + ")");
        System.out.println("Materiale: " + parti[4] + " (ID: " + parti[3] + ")");
        System.out.println("Data Prestito: " + parti[5]);
        System.out.println("Data Scadenza: " + parti[6]);
        
        if (!parti[7].equals("null")) {
            System.out.println("Data Restituzione: " + parti[7]);
            System.out.println("Stato: RESTITUITO");
        } else {
            System.out.println("Stato: IN CORSO");
        }
        
        if (Double.parseDouble(parti[8]) > 0) {
            System.out.println("Penale: " + parti[8] + "€");
        }
        
        if (parti[9].equals("true")) {
            System.out.println("⚠️  IN RITARDO!");
        }
    }
}