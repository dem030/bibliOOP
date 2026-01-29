package server;
import java.sql.*;

public class DataBaseManager {
    static private DataBaseManager istance = null;
    private Connection conn;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private DataBaseManager() {
    }
    static getIstance() {
        if (istance == null) {
            istance = new DataBaseManager();
        }
        return istance;
    }
    public void inizializza(String dburl, String username, String password){
        try {
            this.dbUrl= dburl;
            this.dbUsername= username;
            this.dbPassword= password;
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dburl, username, password);

        } catch (Exception e) {
            
        }

    }
    
}
