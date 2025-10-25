package ComparadorDeImagens.dao;

import ComparadorDeImagens.model.LoginUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUserDAO {

    public void create(LoginUser user) {
        String sql = "INSERT INTO userpassword (user, password) VALUES (?, ?)";

        try (Connection connection = ConnectionDAO.getLogginConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();

            System.out.println("Usuário cadastrado com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    // Validar login
    public boolean login(String username, String password) {
        
        String sql = "SELECT * FROM userpassword WHERE User = ? AND Password = ?";

        try (Connection connection = ConnectionDAO.getLogginConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }
}
