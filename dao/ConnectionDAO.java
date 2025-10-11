package ComparadorDeImagens.dao;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectionDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/loginuser";
    private static final String USER = "root";
    private static final String PASSWORD = "1618f17LLP*";

    public static Connection conectar() throws SQLException{
        return DriverManager.getConnection(URL,USER ,PASSWORD);
    }
}
