package server;
import utils.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class ServerMain {
    private int maxThread;
    ExecutorService threadPool;
    ClientHandler clientHandler;
    ServerSocket serverSocket;
    Configurazione config;
    DataBaseManager dbManager;
    boolean inEsecuzione;
    public void chiudiServer() {
        inEsecuzione = false;
        if (threadPool != null) {
            threadPool.shutdown();
            try{
                if (! threadPool.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();

                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    System.err.println("Errore durante la chiusura del server: " + e.getMessage());
                }
            }
            if (dbManager != null) {
                try{
                dbManager.chiudiConnessione();
                } catch (SQLException e) {
                    System.err.println("Errore durante la chiusura della connessione al database: " + e.getMessage());
                }
                
            }
        }
    }

    public void main(String[] args) {
        try {
            Configurazione config = Configurazione.getInstance();
            XMLConfigParser.leggiConfigurazione("../../config/config.xml");
            dbManager = DataBaseManager.getInstance();
            dbManager.inizializza(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
            threadPool = Executors.newFixedThreadPool(maxThread);
            serverSocket = new ServerSocket(config.getPortaServer());
            inEsecuzione = true;
            while (inEsecuzione) {
                Socket clientSocket = serverSocket.accept();
                clientHandler = new ClientHandler(clientSocket, config);
                threadPool.submit(clientHandler);

            }
        } catch (Exception e) {
            if (inEsecuzione) {
                System.err.println("Errore nel server: " + e.getMessage());
            }
        }
        finally { chiudiServer(); }
    }
}