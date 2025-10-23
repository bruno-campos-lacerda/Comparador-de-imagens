/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparadorDeImagens.dao;

//import ComparadorDeImagens.model.LoginUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author brula
 */
public class ImagesDAO {
    
    //Inserir imagens
    public void InsertImages(String dateTime, byte[] images) {
        String sql = "INSERT INTO images (dateTime, image) VALUES (?, ?)";

        try (Connection connection = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, dateTime);
            stmt.setBytes(2, images);
            stmt.executeUpdate();

            System.out.println("Imagem salva com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar imagem: " + e.getMessage());
        }
    }

    // Obter imagens
    public boolean GetImages(String dateTime, byte[] images) {
        
        String sql = "SELECT * FROM images WHERE dateTime = ?";

        try (Connection connection = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, dateTime);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erro ao obter imagens: " + e.getMessage());
            return false;
        }
    }
}
