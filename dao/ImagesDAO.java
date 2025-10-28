package ComparadorDeImagens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImagesDAO {

    /**
     * Insere uma imagem no banco com caminho no disco.
     * @param dateTime Timestamp da imagem
     *
     */
    public void InsertImagePath(Timestamp dateTime, byte [] image) {
        String sql = "INSERT INTO images (_datetime, images) VALUES (?, ?)";
        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, dateTime);
            stmt.setBytes(2, image);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar imagem: " + e.getMessage());
        }
    }

    /**
     * Verifica se já existe uma imagem com determinado href.
     */
//    public boolean existsImageByHref(String datetime) {
//        String sql = "SELECT COUNT(*) FROM images WHERE _datetime = ?";
//        try (Connection conn = ConnectionDAO.getImageConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, datetime);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Erro ao verificar href: " + e.getMessage());
//        }
//        return false;
//    }

    /**
     * Verifica se já existe uma imagem com determinado timestamp.
//     */
//    public boolean existsImageByDatetime(Timestamp timestamp) {
//        String sql = "SELECT COUNT(*) FROM images WHERE _datetime = ?";
//        try (Connection conn = ConnectionDAO.getImageConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setTimestamp(1, timestamp);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1) > 0;
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Erro ao verificar datetime: " + e.getMessage());
//        }
//        return false;
//    }

    /**
     * Retorna todas as imagens do banco, em ordem crescente de data.
     */
    public List<ImageItem> getAllImages() {
        List<ImageItem> images = new ArrayList<>();
        String sql = "SELECT _datetime, image FROM images ORDER BY _datetime ASC";

        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("_datetime");
                byte[] image = rs.getBytes("image");
                images.add(new ImageItem(ts, image));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return images;
    }

    /**
     * Classe auxiliar para representar uma imagem com timestamp e caminho.
     */
    public static class ImageItem {
        private Timestamp datatime;
        private byte [] image;

        public ImageItem(Timestamp datatime, byte[] image) {
            this.datatime = datatime;
            this.image = image;
        }

        public Timestamp getDatatime() {
            return datatime;
        }

        public void setDatatime(Timestamp datatime) {
            this.datatime = datatime;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }

        
    }
}
