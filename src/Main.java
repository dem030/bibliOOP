import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import utils.*;
import server.*;

public class Main {
    
    private static ServerSocket serverSocket;
    private static ExecutorService threadPool;
    private static Configurazione config;
    private static boolean serverAvviato = false;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println("==========================================");
            System.out.println("   SISTEMA GESTIONE BIBLIOTECA");
            System.out.println("==========================================");
            System.out.println();
            
            // STEP 1: Avvio automatico del server in background
            avviaServerBackground();
            
            // Attendi un attimo che il server si avvii
            Thread.sleep(1000);
            
            // STEP 2: Menu principale
            boolean continua = true;
            while (continua) {
                System.out.println();
                System.out.println("==========================================");
                System.out.println("         MENU PRINCIPALE");
                System.out.println("==========================================");
                System.out.println("1. Accedi come Utente");
                System.out.println("2. Accedi come Amministratore");
                System.out.println("3. Registrati (nuovo utente)");
                System.out.println("0. Esci");
                System.out.println("==========================================");
                System.out.print("Scelta: ");
                
                int scelta = scanner.nextInt();
                scanner.nextLine();
                
                switch (scelta) {
                    case 1:
                        accediUtente();
                        break;
                    case 2:
                        accediAdmin();
                        break;
                    case 3:
                        registrazioneNuovoUtente();
                        break;
                    case 0:
                        System.out.println();
                        System.out.println("Chiusura sistema...");
                        continua = false;
                        break;
                    default:
                        System.out.println("Scelta non valida!");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
        } finally {
            chiudiServer();
            scanner.close();
        }
    }
    
    // Avvia server in background
    private static void avviaServerBackground() {
        Thread serverThread = new Thread(() -> {
            try {
                System.out.println("Avvio server in background...");
                
                // Leggi configurazione
                config = XMLConfigParser.leggiConfigurazione("config/config.xml");
                System.out.println(" Configurazione caricata");
                
                // Inizializza database
                DataBaseManager dbManager = DataBaseManager.getInstance();
                dbManager.inizializza(
                    config.getDbUrl(),
                    config.getDbUsername(),
                    config.getDbPassword()
                );
                System.out.println(" Database connesso");
                
                // Crea ThreadPool
                threadPool = Executors.newFixedThreadPool(config.getMaxThread());
                System.out.println(" ThreadPool creato");
                
                // Avvia ServerSocket
                serverSocket = new ServerSocket(config.getPortaServer());
                serverAvviato = true;
                System.out.println(" Server avviato sulla porta " + config.getPortaServer());
                System.out.println();
                
                // Loop accettazione client (in background)
                while (serverAvviato) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        
                        // Crea handler (lo implementeremo dopo)
                        ClientHandler handler = new ClientHandler(clientSocket, config);
                        threadPool.submit(handler);
                        
                    } catch (IOException e) {
                        if (serverAvviato) {
                            System.err.println("[Server] Errore accettazione client: " + e.getMessage());
                        }
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Errore avvio server: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        serverThread.setDaemon(true); // Thread demone, si chiude con il main
        serverThread.start();
    }
    
    // Accedi come utente
    private static void accediUtente() {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println();
            System.out.println("=== LOGIN UTENTE ===");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            // Connessione al server locale
            Socket socket = new Socket("localhost", config.getPortaServer());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Invia comando login
            String comando = "LOGIN:" + username + ":" + password;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                System.out.println(" Login effettuato!");
                System.out.println();
                
                // Avvia sessione utente
                sessioneUtente(socket, out, in, username);
                
            } else {
                String errore = risposta.substring(7);
                System.out.println(" Login fallito: " + errore);
                socket.close();
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
    
    // Sessione utente
    private static void sessioneUtente(Socket socket, ObjectOutputStream out, ObjectInputStream in, String username) {
        Scanner scanner = new Scanner(System.in);
        boolean inSessione = true;
        
        try {
            while (inSessione) {
                System.out.println();
                System.out.println("========================================");
                System.out.println("  MENU UTENTE - " + username);
                System.out.println("========================================");
                System.out.println("1. Cerca materiale");
                System.out.println("2. Prenota materiale");
                System.out.println("3. Visualizza i miei prestiti");
                System.out.println("4. Restituisci materiale");
                System.out.println("5. Rinnova prestito");
                System.out.println("0. Logout");
                System.out.println("========================================");
                System.out.print("Scelta: ");
                
                int scelta = scanner.nextInt();
                scanner.nextLine();
                
                String comando = "";
                
                switch (scelta) {
                    case 1: // Cerca
                        System.out.print("Inserisci titolo o autore: ");
                        String query = scanner.nextLine();
                        comando = "CERCA_MATERIALE:" + query;
                        break;
                        
                    case 2: // Prenota
                        System.out.print("Inserisci ID materiale: ");
                        int idMat = scanner.nextInt();
                        scanner.nextLine();
                        comando = "PRENOTA_MATERIALE:" + idMat;
                        break;
                        
                    case 3: // Visualizza prestiti
                        comando = "VISUALIZZA_PRESTITI";
                        break;
                        
                    case 4: // Restituisci
                        System.out.print("Inserisci ID prestito: ");
                        int idPrest = scanner.nextInt();
                        scanner.nextLine();
                        comando = "RESTITUISCI:" + idPrest;
                        break;
                        
                    case 5: // Rinnova
                        System.out.print("Inserisci ID prestito: ");
                        int idRinn = scanner.nextInt();
                        scanner.nextLine();
                        comando = "RINNOVA:" + idRinn;
                        break;
                        
                    case 0: // Logout
                        comando = "EXIT";
                        inSessione = false;
                        break;
                        
                    default:
                        System.out.println("Scelta non valida");
                        continue;
                }
                
                // Invia comando
                out.writeObject(comando);
                out.flush();
                
                if (!comando.equals("EXIT")) {
                    // Ricevi e mostra risposta
                    String risposta = (String) in.readObject();
                    mostraRisposta(risposta);
                }
            }
            
            socket.close();
            System.out.println(" Logout effettuato");
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
    
    // Accedi come admin
    private static void accediAdmin() {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println();
            System.out.println("=== LOGIN AMMINISTRATORE ===");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            // Connessione al server locale
            Socket socket = new Socket("localhost", config.getPortaServer());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Invia comando login
            String comando = "LOGIN:" + username + ":" + password;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                // Verifica che sia admin
                String dati = risposta.substring(3);
                String[] parti = dati.split(";");
                String ruolo = parti[4];
                
                if (!ruolo.equals("ADMIN")) {
                    System.out.println(" Accesso negato: non sei un amministratore");
                    socket.close();
                    return;
                }
                
                System.out.println(" Login amministratore effettuato!");
                System.out.println();
                
                // Avvia sessione admin
                sessioneAdmin(socket, out, in, username);
                
            } else {
                String errore = risposta.substring(7);
                System.out.println(" Login fallito: " + errore);
                socket.close();
            }
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
    
    // Sessione admin
    private static void sessioneAdmin(Socket socket, ObjectOutputStream out, ObjectInputStream in, String username) {
        Scanner scanner = new Scanner(System.in);
        boolean inSessione = true;
        
        try {
            while (inSessione) {
                System.out.println();
                System.out.println("========================================");
                System.out.println("  MENU AMMINISTRATORE - " + username);
                System.out.println("========================================");
                System.out.println("1. Aggiungi materiale");
                System.out.println("2. Rimuovi materiale");
                System.out.println("3. Visualizza tutti i prestiti");
                System.out.println("4. Visualizza prestiti in ritardo");
                System.out.println("5. Blocca/Sblocca utente");
                System.out.println("6. Cerca materiale");
                System.out.println("0. Logout");
                System.out.println("========================================");
                System.out.print("Scelta: ");
                
                int scelta = scanner.nextInt();
                scanner.nextLine();
                
                String comando = "";
                
                switch (scelta) {
                    case 1: // Aggiungi materiale
                        System.out.println("Tipo (1=Libro, 2=Rivista): ");
                        int tipo = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Titolo: ");
                        String titolo = scanner.nextLine();
                        System.out.print("Autore: ");
                        String autore = scanner.nextLine();
                        
                        String tipoStr = (tipo == 1) ? "LIBRO" : "RIVISTA";
                        String campo3;
                        
                        if (tipo == 1) {
                            System.out.print("ISBN: ");
                            campo3 = scanner.nextLine();
                        } else {
                            System.out.print("Numero Edizione: ");
                            campo3 = scanner.nextLine();
                        }
                        
                        System.out.print("Anno: ");
                        int anno = scanner.nextInt();
                        scanner.nextLine();
                        
                        comando = "AGGIUNGI_MATERIALE:" + tipoStr + ";" + titolo + ";" + autore + ";" + campo3 + ";" + anno;
                        break;
                        
                    case 2: // Rimuovi materiale
                        System.out.print("ID materiale: ");
                        int idMat = scanner.nextInt();
                        scanner.nextLine();
                        comando = "RIMUOVI_MATERIALE:" + idMat;
                        break;
                        
                    case 3: // Tutti i prestiti
                        comando = "TUTTI_PRESTITI";
                        break;
                        
                    case 4: // Prestiti in ritardo
                        comando = "PRESTITI_RITARDO";
                        break;
                        
                    case 5: // Blocca utente
                        System.out.print("ID utente: ");
                        int idUt = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("1. Blocca");
                        System.out.println("2. Sblocca");
                        int az = scanner.nextInt();
                        scanner.nextLine();
                        boolean blocca = (az == 1);
                        comando = "BLOCCA_UTENTE:" + idUt + ":" + blocca;
                        break;
                        
                    case 6: // Cerca
                        System.out.print("Cerca: ");
                        String query = scanner.nextLine();
                        comando = "CERCA_MATERIALE:" + query;
                        break;
                        
                    case 0: // Logout
                        comando = "EXIT";
                        inSessione = false;
                        break;
                        
                    default:
                        System.out.println("Scelta non valida");
                        continue;
                }
                
                // Invia comando
                out.writeObject(comando);
                out.flush();
                
                if (!comando.equals("EXIT")) {
                    // Ricevi e mostra risposta
                    String risposta = (String) in.readObject();
                    mostraRisposta(risposta);
                }
            }
            
            socket.close();
            System.out.println(" Logout effettuato");
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
    
    // 3
    // istrazione nuovo utente
    private static void registrazioneNuovoUtente() {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println();
            System.out.println("=== REGISTRAZIONE NUOVO UTENTE ===");
            System.out.print("Codice Fiscale (16 caratteri): ");
            String cf = scanner.nextLine().toUpperCase();
            
            if (cf.length() != 16) {
                System.out.println(" Codice fiscale deve essere di 16 caratteri");
                return;
            }
            
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

            // Validazione email e telefono
            models.Utente tempUtente = new models.Utente();
            tempUtente.setEmail(email);
            tempUtente.setTelefono(telefono);

            if (!tempUtente.validaEmail()) {
                System.out.println(" Email non valida (formato: esempio@dominio.com)");
                return;
            }

            if (!tempUtente.validaTelefono()) {
                System.out.println(" Telefono non valido (9-15 cifre, può iniziare con +)");
                return;
            }

            // Connessione al server
            Socket socket = new Socket("localhost", config.getPortaServer());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            // Invia comando registrazione
            String comando = "REGISTRAZIONE:" + cf + ";" + username + ";" + password + ";" +  nome + ";" + cognome + ";" + email + ";" + telefono;
            out.writeObject(comando);
            out.flush();
            
            // Ricevi risposta
            String risposta = (String) in.readObject();
            
            if (risposta.startsWith("OK:")) {
                System.out.println(" Registrazione completata!");
                System.out.println("Ora puoi effettuare il login come utente");
            } else {
                String errore = risposta.substring(7);
                System.out.println(" Registrazione fallita: " + errore);
            }
            
            socket.close();
            
        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }
    
    // Mostra risposta dal server
    private static void mostraRisposta(String risposta) {
        System.out.println();
        
        if (risposta.startsWith("OK:")) {
            String dati = risposta.substring(3);
            
            // Se contiene || è una lista
            if (dati.contains("||")) {
                String[] elementi = dati.split("\\|\\|");
                System.out.println("Risultati trovati: " + elementi.length);
                System.out.println("----------------------------------------");
                
                for (String elemento : elementi) {
                    String[] parti = elemento.split(";");
                    // Stampa formattata base
                    for (int i = 0; i < parti.length; i++) {
                        System.out.println(parti[i]);
                    }
                    System.out.println("----------------------------------------");
                }
            } else {
                System.out.println(" " + dati);
            }
            
        } else if (risposta.startsWith("ERRORE:")) {
            String errore = risposta.substring(7);
            System.out.println(" " + errore);
        }
    }
    
    // Chiudi server
    private static void chiudiServer() {
        try {
            serverAvviato = false;
            
            if (threadPool != null) {
                threadPool.shutdown();
            }
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println(" Server chiuso");
            
        } catch (IOException e) {
            System.err.println("Errore chiusura server: " + e.getMessage());
        }
    }
}