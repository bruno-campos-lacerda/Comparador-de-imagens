package projeto.aps.dao;


import projeto.aps.model.LoginUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUserDAO {

    private static Connection connection;

    public LoginUserDAO(Connection connection) {
    this.connection = connection;
    }


    public void create(LoginUser user) {
    String sql = "INSERT INTO usuarios (username, password) VALUES (?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.executeUpdate();
        System.out.println("Usuário cadastrado com sucesso!");
    } catch (SQLException e) {
        System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
    }
}


    // Validar login
    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM userpassword WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true se encontrou
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }
}
