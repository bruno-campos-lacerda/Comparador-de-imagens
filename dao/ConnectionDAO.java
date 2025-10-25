package ComparadorDeImagens.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionDAO {

    // Dados da conexão
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // use o driver atualizado
    
    private static final String URLLoggin = "jdbc:mysql://localhost:3306/loginuser?useSSL=false&serverTimezone=UTC";
    private static final String URLImage = "jdbc:mysql://localhost:3306/images?useSSL=false&serverTimezone=UTC";
    
    private static final String USER = "root";
    private static final String PASS = "2131";

    // Método de conexão loggin
    public static Connection getLogginConnection() {
//        try {
//            Class.forName(DRIVER);
//            return DriverManager.getConnection(URLLoggin, USER, PASS);
//        } catch (ClassNotFoundException | SQLException ex) {
//            throw new RuntimeException("Erro na conexão: ", ex);
//        }
        return getConnection(URLLoggin);
    }
    
    // Método de conexão image
    public static Connection getImageConnection() {
//        try {
//            Class.forName(DRIVER);
//            return DriverManager.getConnection(URLImage, USER, PASS);
//        } catch (ClassNotFoundException | SQLException ex) {
//            throw new RuntimeException("Erro na conexão: ", ex);
//        }
        return getConnection(URLImage);
    }
    
    // Método de conexão principal
    private static Connection getConnection(String url){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(url, USER, PASS);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro na conexão: ", ex);
        }
    }

    // Fecha conexão, statement e resultset
    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
        closeConnection(con, stmt);
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Fecha conexão e statement
    public static void closeConnection(Connection con, PreparedStatement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
