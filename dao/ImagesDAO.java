package ComparadorDeImagens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImagesDAO {

    /*
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
    
    /*
     * Made by Bruno Campos
     * Delete images from database
     */
//    public void DeletImage(Timestamp dateTime){
//        //String sql = "SELECT _datetime, images FROM images ORDER BY _datetime ASC";
//        //String sql = "DELETE FROM images WHERE _datetime = '?'";
//        String sql = """
//                     DELETE FROM images WHERE ctid IN (SELECT ctid FROM images WHERE _datetime = ? LIMIT 1);
//                     """;
//        try(Connection conn = ConnectionDAO.getImageConnection();
//            PreparedStatement stmt = conn.prepareStatement(sql)){
//            stmt.setTimestamp(1, dateTime);
//            stmt.executeUpdate();
//        }catch(SQLException e){
//            System.err.println("❌ Erro ao apagar imagem: " + e.getMessage());
//        }
//    }
    public void DeletImage(int id){
        //String sql = "SELECT _datetime, images FROM images ORDER BY _datetime ASC";
        //String sql = "DELETE FROM images WHERE _datetime = '?'";
//        String sql = """
//                     DELETE FROM images WHERE ctid IN (SELECT ctid FROM images WHERE id = ?);
//                     """;
        String sql = "DELETE FROM images WHERE id = ?;";
        try(Connection conn = ConnectionDAO.getImageConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            System.err.println("❌ Erro ao apagar imagem: " + e.getMessage());
        }
    }

    /*
     * Retorna todas as imagens do banco, em ordem crescente de data.
     */
//    public List<ImageItem> getAllImagesDateTimes() {
//        List<ImageItem> images = new ArrayList<>();
//        //String sql = "SELECT _datetime, images FROM images ORDER BY _datetime ASC";
//        String sql = "SELECT _datetime FROM images;";
//
//        try (Connection conn = ConnectionDAO.getImageConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                Timestamp ts = rs.getTimestamp("_datetime");
//                byte[] image = rs.getBytes("images");
//                images.add(new ImageItem(ts, image));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return images;
//    }
    
    public byte[] getImage(Timestamp date){
        String sql = "SELECT images FROM images WHERE _datetime = ?";
        byte[] image;
        
        try(Connection conn = ConnectionDAO.getImageConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setTimestamp(1, date);
            ResultSet rs = stmt.executeQuery();
            
            image = rs.getBytes("images");
            return image;
        }catch(SQLException e){
            System.out.println(e);
        }
        
        return null;
    }
    
    public List<ImageData> getAllImagesDateTimes() {
        List<ImageData> imagesDateTime = new ArrayList<>();
        //String sql = "SELECT _datetime, images FROM images ORDER BY _datetime ASC";
        String sql = "SELECT id, _datetime FROM images;";

        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp ts = rs.getTimestamp("_datetime");
                imagesDateTime.add(new ImageData(id, ts));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imagesDateTime;
    }

    
    public static class ImageData{
        private int id;
        private Timestamp dateTime;
        
        public ImageData(int id, Timestamp date){
            this.id = id;
            this.dateTime = date;
        }
        
        public int getId(){
            return this.id;
        }
        
        public Timestamp getDateTime(){
            return this.dateTime;
        }
    }
    
    /*
     * Classe auxiliar para representar uma imagem com timestamp e caminho.
     */
//    public static class ImageItem {
//        private Timestamp datatime;
//        private byte [] image;
//
//        public ImageItem(Timestamp datatime, byte[] image) {
//            this.datatime = datatime;
//            this.image = image;
//        }
//
//        public Timestamp getDatatime() {
//            return datatime;
//        }
//
//        public void setDatatime(Timestamp datatime) {
//            this.datatime = datatime;
//        }
//
//        public byte[] getImage() {
//            return image;
//        }
//
//        public void setImage(byte[] image) {
//            this.image = image;
//        }
//
//        
//    }
}
