package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

import exceptions.*;
import models.*;
import utils.Configurazione;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Utente utenteLoggato;
    private DataBaseManager dbManager;
    private Configurazione config;
    private boolean inEsecuzione;

    public ClientHandler(Socket socket, Configurazione config) {
        this.socket = socket;
        this.config = config;
        this.dbManager = DataBaseManager.getInstance();
        this.utenteLoggato = null;
        this.inEsecuzione = true;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());


            while (inEsecuzione) {
                try {
                    String comando = (String) in.readObject();

                    if (comando == null || comando.equals("EXIT") && utenteLoggato != null)  {
                        System.out.println("Client disconnesso");
                        break;
                    }

                    String risposta = processaComando(comando);
                    out.writeObject(risposta);
                    out.flush();

                } catch (IOException | ClassNotFoundException e) {
                    
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Errore apertura stream: " + e.getMessage());
        } finally {
            chiudiConnessione();
        }
    }

    

    private String processaComando(String comando) {
        try {
            String[] parti = comando.split(":");
            String azione = parti[0];

            switch (azione) {

                // richieste utente
                case "LOGIN":
                    return gestisciLogin(parti[1], parti[2]);

                case "REGISTRAZIONE":
                    return gestisciRegistrazione(parti[1].split(";"));

                case "CERCA_MATERIALE":
                    return gestisciCercaMateriale(parti[1]);

                case "PRENOTA_MATERIALE":
                    return gestisciPrenotazione(Integer.parseInt(parti[1]));

                case "VISUALIZZA_PRESTITI":
                    return gestisciVisualizzaPrestiti();

                case "RESTITUISCI":
                    return gestisciRestituzione(Integer.parseInt(parti[1]));

                case "RINNOVA":
                    return gestisciRinnovo(Integer.parseInt(parti[1]));

                // richieste admin
                case "AGGIUNGI_MATERIALE":
                    return gestisciAggiungiMateriale(parti[1].split(";"));

                case "RIMUOVI_MATERIALE":
                    return gestisciRimuoviMateriale(Integer.parseInt(parti[1]));

                case "TUTTI_PRESTITI":
                    return gestisciTuttiPrestiti();

                case "PRESTITI_RITARDO":
                    return gestisciPrestitiInRitardo();

                case "BLOCCA_UTENTE":
                    return gestisciBloccaUtente(
                            Integer.parseInt(parti[1]),
                            Boolean.parseBoolean(parti[2])
                    );
                default:
                    return "ERRORE:Comando non riconosciuto";
            }

        } catch (Exception e) {
            System.err.println("Errore processing comando: " + e.getMessage());
            return "ERRORE:" + e.getMessage();
        }
    }

    // operazioni utente
    private String gestisciLogin(String username, String password) {
        try {
            utenteLoggato = dbManager.autenticaUtente(username, password);
            return "OK:" + serializeUtente(utenteLoggato);

        } catch (UtenteNonTrovato e) {
            return "ERRORE:Credenziali errate";
        } catch (OperazioneNonConsentitaException e) {
            return "ERRORE:Utente bloccato";
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciRegistrazione(String[] dati) {
        try {
            Utente u = new Utente();
            u.setCodice_fiscale(dati[0]);
            u.setUsername(dati[1]);
            u.setPassword(dati[2]);
            u.setNome(dati[3]);
            u.setCognome(dati[4]);
            u.setEmail(dati[5]);
            u.setTelefono(dati[6]);

            int id = dbManager.registraUtente(u);
            return "OK:Registrazione completata. ID=" + id;

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciCercaMateriale(String query) {
        try {
            List<Materiale> materiali = dbManager.cercaMateriale(query);

            if (materiali.isEmpty())
                return "OK:Nessun risultato";

            StringBuilder sb = new StringBuilder("OK:");
            for (Materiale m : materiali) {
                sb.append(serializeMateriale(m)).append("||");
            }
            return sb.toString();

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciPrenotazione(int idMateriale) {
        if (utenteLoggato == null)
            return "ERRORE:Devi fare login prima";

        try {
            Prestito p = dbManager.prenotaMateriale(utenteLoggato.getId_ut(),idMateriale,config);

            return "OK:Prenotazione effettuata. Scadenza=" + p.getDataScadenza();

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciVisualizzaPrestiti() {
        if (utenteLoggato == null)
            return "ERRORE:Devi fare login prima";

        try {
            List<Prestito> prestiti = dbManager.getPrestiti(utenteLoggato.getId_ut());

            if (prestiti.isEmpty())
                return "OK:Nessun prestito";

            StringBuilder sb = new StringBuilder("OK:");
            for (Prestito p : prestiti) {
                sb.append(serializePrestito(p)).append("||");
            }
            return sb.toString();

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciRestituzione(int idPrestito) {
        if (utenteLoggato == null)
            return "ERRORE:Devi fare login prima";

        try {
            double penale = dbManager.restituisciMateriale(idPrestito, config);
            return penale > 0
                    ? "OK:Restituito. Penale=" + penale
                    : "OK:Restituito. Nessuna penale";

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciRinnovo(int idPrestito) {
        if (utenteLoggato == null)
            return "ERRORE:Devi fare login prima";

        try {
            LocalDate nuovaScadenza = dbManager.rinnovaPrestito(idPrestito, config);
            return "OK:Rinnovato. Nuova scadenza=" + nuovaScadenza;

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }


    // operazioni admin
    private boolean isAdmin() {
        return utenteLoggato != null && utenteLoggato.getRuoloUtente() == Utente.ruoloUtente.ADMIN;
    }

    private String gestisciAggiungiMateriale(String[] dati) {
        if (!isAdmin())
            return "ERRORE:Operazione non consentita";

        try {
            String tipo = dati[0];
            int id;

            if (tipo.equals("LIBRO")) {
                Libro l = new Libro();
                l.setTitolo(dati[1]);
                l.setAutore(dati[2]);
                l.setIsbn(dati[3]);
                l.setAnnoPubblicazione(Integer.parseInt(dati[4]));
                id = dbManager.aggiungiMateriale(l);
            } else {
                Rivista r = new Rivista();
                r.setTitolo(dati[1]);
                r.setAutore(dati[2]);
                r.setNumeroEdizione(Integer.parseInt(dati[3]));
                r.setAnnoPubblicazione(Integer.parseInt(dati[4]));
                id = dbManager.aggiungiMateriale(r);
            }

            return "OK:Materiale aggiunto ID=" + id;

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciRimuoviMateriale(int idMateriale) {
        if (!isAdmin())
            return "ERRORE:Operazione non consentita";

        try {
            dbManager.rimuoviMateriale(idMateriale);
            return "OK:Materiale rimosso";

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciTuttiPrestiti() {
        if (!isAdmin())
            return "ERRORE:Operazione non consentita";

        try {
            List<Prestito> prestiti = dbManager.getTuttiPrestiti();
            StringBuilder sb = new StringBuilder("OK:");

            for (Prestito p : prestiti) {
                sb.append(serializePrestito(p)).append("||");
            }
            return sb.toString();

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciPrestitiInRitardo() {
        if (!isAdmin())
            return "ERRORE:Operazione non consentita";

        try {
            List<Prestito> prestiti = dbManager.getPrestitiInRitardo();
            StringBuilder sb = new StringBuilder("OK:");

            for (Prestito p : prestiti) {
                sb.append(serializePrestito(p)).append("||");
            }
            return sb.toString();

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String gestisciBloccaUtente(int idUtente, boolean blocca) {
        if (!isAdmin())
            return "ERRORE:Operazione non consentita";

        try {
            dbManager.bloccaUtente(idUtente, blocca);
            return blocca ? "OK:Utente bloccato" : "OK:Utente sbloccato";

        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }
    
    //serializzazione
    private String serializeUtente(Utente u) {
        return u.getId_ut() + ";" +
            u.getUsername() + ";" +
            u.getNome() + ";" +
            u.getCognome() + ";" +
            u.getRuoloUtente() + ";" +
            u.isBloccato();
    }

    private String serializeMateriale(Materiale m) {
        String tipo = (m instanceof Libro) ? "LIBRO" : "RIVISTA";
        String disponibile = m.isDisponibile() ? "Si" : "No";

        String result = "ID: " + m.getId_pz() + ";" + "Tipo: " + tipo + ";" + "Titolo: " + m.getTitolo() + ";" + "Autore: " + m.getAutore() + ";" + "Disponibile: " + disponibile + ";";

        if (m instanceof Libro) {
            Libro l = (Libro) m;
            result += "ISBN: " + l.getIsbn() + ";" + "Anno: " + l.getAnnoPubblicazione();
        } else {
            Rivista r = (Rivista) m;
            result += "Num. Edizione: " + r.getNumeroEdizione() + ";" + "Anno: " + r.getAnnoPubblicazione();
        }
        return result;
    }

    private String serializePrestito(Prestito p) {
        String stato = (p.getDataRestituzione() != null) ? "RESTITUITO" : "IN CORSO";
        return "ID Prestito: " + p.getId() + ";" + "Utente: " + p.getUtente().getUsername() + " (ID: " + p.getUtente().getId_ut() + ");" + "Materiale: " + p.getMateriale().getTitolo() + " (ID: " + p.getMateriale().getId_pz() + ");" + "Data Prestito: " + p.getDataPrestito() + ";" + "Data Scadenza: " + p.getDataScadenza() + ";" + "Data Restituzione: " + (p.getDataRestituzione() != null ? p.getDataRestituzione() : "-") + ";" + "Stato: " + stato + ";" + "Penale: " + p.getPenale() + " EUR";
    }
    private void chiudiConnessione() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Errore chiusura connessione: " + e.getMessage());
        }
    }
}
