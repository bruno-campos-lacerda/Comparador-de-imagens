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
     */
    public void InsertImagePath(Timestamp dateTime, byte [] image) {
        String sql = "INSERT INTO images (datetime, image) VALUES (?, ?)";
        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, dateTime);
            stmt.setBytes(2, image);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar imagem: " + e.getMessage());
        }
    }
    
    public void DeletImage(int id){
        String sql = "DELETE FROM images WHERE id = ?;";
        try(Connection conn = ConnectionDAO.getImageConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch(SQLException e){
            System.err.println("❌ Erro ao apagar imagem: " + e.getMessage());
        }
    }
    
    public byte[] getImage(Timestamp date){
        String sql = "SELECT image FROM images WHERE datetime = ?";
        byte[] image;
        
        try(Connection conn = ConnectionDAO.getImageConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setTimestamp(1, date);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                image = rs.getBytes("image");
                return image;
            }
        }catch(SQLException e){
            System.out.println(e);
        }
        
        return null;
    }
    
    public List<ImageData> getAllImagesDateTimes() {
        List<ImageData> imagesDateTime = new ArrayList<>();
        String sql = "SELECT id, datetime FROM images;";

        try (Connection conn = ConnectionDAO.getImageConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp ts = rs.getTimestamp("datetime");
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
}
