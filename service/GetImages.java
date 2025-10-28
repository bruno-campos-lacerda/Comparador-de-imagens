package ComparadorDeImagens.service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import ComparadorDeImagens.dao.ImagesDAO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.Timestamp;
import java.util.Base64;

/*
 * Classe responsavel por consumir a API do INPE,
 * converter o campo datetime para Timestamp
 * e salvar as imagens PNG no banco de dados.
 *
 * @author Pedro
 */
public class GetImages {

    public static void main(String[] args) {
        
        // URL da API da Earth Search (AWS Element84)
        String apiUrl = "https://earth-search.aws.element84.com/v1/search?"
                + "collections=sentinel-2-l2a"
                + "&limit=100"
                + "&bbox=-48.5,-16.5,-47.5,-15.5"; // Cerrado Goiano, por exemplo

        // Cliente HTTP
        HttpClient client = HttpClient.newBuilder().build();

        // Requisição GET para a API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            // Envia a requisição e obtém a resposta JSON
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Converte a resposta em JSON
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray features = jsonResponse.getJSONArray("features");

            System.out.println("🔍 Iniciando coleta de imagens... Total de itens: " + features.length());

            for(int i = 0; i < 150; i++){
                // Percorre os resultados (limitando só para evitar sobrecarga)
                for (int j = 0; j < features.length(); j++) {
                    JSONObject feature = features.getJSONObject(j);

                    // Obtém o campo "datetime"
                    JSONObject dateProperties = feature.optJSONObject("properties");
                    if (dateProperties == null) continue;

                    String datetime = dateProperties.optString("datetime", null);
                    if (datetime == null) continue;

                    // Converte o datetime ISO 8601 → Timestamp
                    Timestamp timestamp;
                    try {
                        Instant instant = Instant.parse(datetime);
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        timestamp = Timestamp.valueOf(localDateTime);
                    } catch (Exception e) {
                        System.out.println("❌ Erro ao converter datetime: " + datetime + " → " + e.getMessage());
                        continue;
                    }

                    // Obtém os assets (imagens)
                    JSONObject assets = feature.optJSONObject("assets");
                    if (assets == null) continue;

                    // Busca o campo "thumbnail" (único JPEG disponível)
                    JSONObject thumb = assets.optJSONObject("thumbnail");
                    if (thumb == null) continue;

                    String href = thumb.optString("href", null);
                    if (href == null || !href.endsWith(".jpg")) continue;

                    try {
                        // Faz o download da imagem em bytes
                        HttpRequest imgRequest = HttpRequest.newBuilder()
                                .uri(URI.create(href))
                                .build();
                        HttpResponse<byte[]> imgResponse = client.send(imgRequest, HttpResponse.BodyHandlers.ofByteArray());
                        byte[] imageBytes = imgResponse.body();

                        // (Opcional) prévia Base64 para verificação
                        String preview = Base64.getEncoder().encodeToString(imageBytes);
                        if (preview.length() > 200) {
                            preview = preview.substring(0, 200) + "...";
                        }

                        // Aqui você salva no banco (como antes)
                        ImagesDAO dao = new ImagesDAO();
                        dao.InsertImagePath(timestamp, imageBytes);

                        // Apenas imprime (sem banco)
                        System.out.println("✅ Imagem coletada (" + timestamp + ") - " + imageBytes.length + " bytes");
                        System.out.println("📸 Link: " + href);
                        System.out.println();

                    } catch (Exception e) {
                        System.out.println("⚠️ Erro ao baixar imagem: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("\n✅ Processo concluído com sucesso!");

        } catch (IOException | InterruptedException e) {
            System.out.println("Erro de conexão: " + e.getMessage());
        } catch (JSONException je) {
            System.out.println("Erro ao processar JSON: " + je.getMessage());
        }
    }
}