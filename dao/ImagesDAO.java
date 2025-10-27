package ComparadorDeImagens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável por salvar e buscar imagens no banco de dados.
 * A tabela esperada é:
 * 
 * CREATE TABLE images (
 *     id INT AUTO_INCREMENT PRIMARY KEY,
 *     _datetime DATETIME NOT NULL,
 *     imagem LONGBLOB NOT NULL
 * );
 * 
 * @author Pedro
 */
public class ImagesDAO {

    /**
     * Insere uma imagem com data/hora no banco de dados.
     * @param dateTime Timestamp com data e hora da imagem
     * @param imageBytes bytes da imagem (PNG)
     */
    public void InsertImages(Timestamp dateTime, byte[] imageBytes) {
        String sql = "INSERT INTO images (_datetime, imagem) VALUES (?, ?)";
        try (Connection connection = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, dateTime);
            stmt.setBytes(2, imageBytes);
            stmt.executeUpdate();

            System.out.println("✅ Imagem salva com sucesso no banco!");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar imagem: " + e.getMessage());
        }
    }

    /**
     * Verifica se existe uma imagem com a data/hora informada.
     * @param dateTime Timestamp da imagem a buscar
     * @return true se existir, false caso contrário
     */
    public boolean GetImages(Timestamp dateTime) {
        String sql = "SELECT 1 FROM images WHERE _datetime = ?";
        try (Connection connection = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setTimestamp(1, dateTime);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Erro ao obter imagem: " + e.getMessage());
            return false;
        }
    }

    /**
     * Classe estática para representar imagens com timestamp
     */
    public static class ImageItem {
        private Timestamp timestamp;
        private byte[] imageBytes;

        public ImageItem(Timestamp timestamp, byte[] imageBytes) {
            this.timestamp = timestamp;
            this.imageBytes = imageBytes;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public byte[] getImageBytes() {
            return imageBytes;
        }
    }

    /**
     * Retorna todas as imagens do banco em ordem crescente de data
     */
    public List<ImageItem> getAllImages() {
        List<ImageItem> images = new ArrayList<>();
        String sql = "SELECT _datetime, imagem FROM images ORDER BY _datetime ASC";

        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("_datetime");
                byte[] bytes = rs.getBytes("imagem");
                images.add(new ImageItem(ts, bytes));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }
}
