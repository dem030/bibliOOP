package server;
import utils.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ServerMain {
    private int porta;
    private int maxThread;
    ExecutorService threadPool;
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
                serverSocket.close();
            }
            if (dbManager != null) {
                dbManager.closeConnection();
                
            }
        }
    }

    public void main(String[] args) {
        try {
            XMLConfigParser.leggiConfigurazione("../../config/config.xml");
            dbManager = DataBaseManager.getIstance();
            dbManager.inizializza(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
            threadPool = Executor.newFixedThreadPool(maxThread);
            serverSocket = new ServerSocket(config.getPortaServer());
            inEsecuzione = true;
            while (inEsecuzione) {
                Socket clientSocket = serverSocket.accept();
                CLientHandler clientHandler = new ClientHandler(clientSocket, config);
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