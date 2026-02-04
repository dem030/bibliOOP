package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.time.LocalDate;
import models.*;

public class ClientUtenteMain {
    // Attributi
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Utente utenteCorrente;
    private Scanner scanner;
    private boolean connesso;
    
    // Main
    public static void main(String[] args) {
        ClientUtenteMain client = new ClientUtenteMain();
        client.avvia();
    }
    
    // Metodo avvia
    public void avvia() {
        try {
            scanner = new Scanner(System.in);
            
            System.out.println("===================================");
            System.out.println("   BIBLIOTECA - CLIENT UTENTE");
            System.out.println("===================================");
            System.out.println();
            
            // Connessione al server
            connetti("localhost", 8080);
            
            // Loop menu principale (login/registrazione)
            boolean autenticato = false;
            while (!autenticato) {
                System.out.println("1. Login");
                System.out.println("2. Registrazione");
                System.out.println("3. Esci");
                System.out.print("Scelta: ");
                
                int scelta = scanner.nextInt();
                scanner.nextLine(); // consuma newline
                
                switch (scelta) {
                    case 1:
                        autenticato = login();
                        break;
                    case 2:
                        registrazione();
                        break;
                    case 3:
                        disconnetti();
                        return;
                    default:
                        System.out.println("Scelta non valida");
                }
            }
            
            // Loop menu operazioni utente
            while (connesso) {
                mostraMenuUtente();
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
    
    // Login
    private boolean login() {
        try {
            System.out.println();
            System.out.println("=== LOGIN ===");
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
                utenteCorrente = parseUtente(datiUtente);
                
                System.out.println();
                System.out.println("✓ Login effettuato con successo!");
                System.out.println("Benvenuto, " + utenteCorrente.getNome() + "!");
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
    
    // Registrazione
    private void registrazione() {
        try {
            System.out.println();
            System.out.println("=== REGISTRAZIONE ===");
            
            System.out.print("Codice Fiscale (16 caratteri): ");
            String cf = scanner.nextLine().toUpperCase();
            
            System.out.print("Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            
            System.out.print("Cognome: ");
            String cognome = scanner.nextLine();
            
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Telefono: ");
            String telefono = scanner.nextLine();
            
            // Validazione locale
            if (cf.length() != 16) {
                System.out.println("✗ Codice fiscale deve essere di 16 caratteri");
                return;
            }
            
            // Invia comando al server
            String comando = "REGISTRAZIONE:" + cf + ";" + username + ";" + password + ";" + 
                           nome + ";" + cognome + ";" + email + ";" + telefono;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                System.out.println();
                System.out.println("✓ Registrazione completata!");
                System.out.println("Ora puoi effettuare il login");
                System.out.println();
            } else {
                String messaggioErrore = risposta.substring(7);
                System.out.println("✗ Registrazione fallita: " + messaggioErrore);
                System.out.println();
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
    
    // Menu utente
    private void mostraMenuUtente() {
        try {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("         MENU UTENTE");
            System.out.println("==========================================");
            System.out.println("1. Cerca materiale");
            System.out.println("2. Prenota materiale");
            System.out.println("3. Visualizza i miei prestiti");
            System.out.println("4. Restituisci materiale");
            System.out.println("5. Rinnova prestito");
            System.out.println("6. Calcola penale");
            System.out.println("0. Logout");
            System.out.println("==========================================");
            System.out.print("Scelta: ");
            
            int scelta = scanner.nextInt();
            scanner.nextLine(); // consuma newline
            
            switch (scelta) {
                case 1:
                    cercaMateriale();
                    break;
                case 2:
                    prenotaMateriale();
                    break;
                case 3:
                    visualizzaMieiPrestiti();
                    break;
                case 4:
                    restituisciMateriale();
                    break;
                case 5:
                    rinnovaPrestito();
                    break;
                case 6:
                    calcolaPenale();
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
    
    // Cerca materiale
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
                    // Parsa lista materiali (separati da "||")
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
    
    // Prenota materiale
    private void prenotaMateriale() {
        try {
            System.out.println();
            System.out.println("=== PRENOTA MATERIALE ===");
            System.out.print("Inserisci ID del materiale: ");
            int idMateriale = scanner.nextInt();
            scanner.nextLine();
            
            // Invia comando
            String comando = "PRENOTA_MATERIALE:" + idMateriale;
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
    
    // Visualizza miei prestiti
    private void visualizzaMieiPrestiti() {
        try {
            System.out.println();
            System.out.println("=== I MIEI PRESTITI ===");
            
            // Invia comando
            String comando = "VISUALIZZA_PRESTITI";
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                String dati = risposta.substring(3);
                
                if (dati.equals("Nessun prestito trovato")) {
                    System.out.println("Non hai prestiti attivi");
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
    
    // Restituisci materiale
    private void restituisciMateriale() {
        try {
            System.out.println();
            System.out.println("=== RESTITUISCI MATERIALE ===");
            System.out.print("Inserisci ID del prestito: ");
            int idPrestito = scanner.nextInt();
            scanner.nextLine();
            
            // Invia comando
            String comando = "RESTITUISCI:" + idPrestito;
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
    
    // Rinnova prestito
    private void rinnovaPrestito() {
        try {
            System.out.println();
            System.out.println("=== RINNOVA PRESTITO ===");
            System.out.print("Inserisci ID del prestito: ");
            int idPrestito = scanner.nextInt();
            scanner.nextLine();
            
            // Invia comando
            String comando = "RINNOVA:" + idPrestito;
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
    
    // Calcola penale
    private void calcolaPenale() {
        try {
            System.out.println();
            System.out.println("=== CALCOLA PENALE ===");
            System.out.print("Inserisci ID del prestito: ");
            int idPrestito = scanner.nextInt();
            scanner.nextLine();
            
            // Invia comando
            String comando = "CALCOLA_PENALE:" + idPrestito;
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
            System.out.println("Arrivederci!");
            
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
        // Salta ruolo e bloccato per semplicità
        
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
        System.out.println("Materiale: " + parti[4]);
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