/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparadorDeImagens.service;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.util.Base64;
import org.json.*;
import javax.swing.JOptionPane;

/**
 *
 * @author brula
 */
public class GetImages {
    public static void main(String[] args) {
        String apiUrl = "https://data.inpe.br/bdc/stac/v1/search?collections=LCC_L8_30_1M_STK_Cerrado-1&limit=10000";
            
        //client é a conexão com a api
        HttpClient client = HttpClient.newBuilder().build();

        //request é a construção da requisicao, o pedido que será feito
        //.header("Accept", "application/Json") é o formato em que queremos que seja retornado, no caso .JSON
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().header("Accept", "application/Json").build();
        
        try {
            //response se trata do retorno da requisicao
            //a conexao client envia a requisicao request, que deve ser retornada como String
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            
            //JSONObject tranforma em string todos os metadados dentro de {}
            //JSONOArray tranforma em string todos os metadados dentro de []
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray features = jsonResponse.getJSONArray("features");
            
            System.out.println("datetime nos itens disponiveis:\n");
            
            int i = 0;
            while(i <= 10000){
                for(int j = 0; j < features.length(); j++){
                    //feature recebe os metadados dentro de {}, no proximo loop, ele vai para o proximo {}
                    JSONObject feature = features.getJSONObject(j);
                    
                    //dateProperties recebe dentro de feature, o objeto "properties"
                    //datetime recebe dentro de dateProperties, o objeto "datetime" em formato de String
                    JSONObject dateProperties = feature.getJSONObject("properties");
                    String datetime = dateProperties.getString("datetime");
                    
                    //assets recebe dentro de feature, o objeto "assets", que contém as imagens
                    JSONObject assets = feature.optJSONObject("assets");
                    String image64Preview = "";
                    
                    //percorre os objetos dentro de assets
                    for(String key : assets.keySet()){
                        JSONObject asset = assets.getJSONObject(key);
                        
                        //href e type passam pela funcao optString(texto a ser encontrado, valor padrao caso nao encontrado) e retornam o link da imagem
                        String href = asset.optString("href", "sem link");
                        String type = asset.optString("type", "sem tipo");
                        
                        //cria uma nova requisicao para para obter as imagens em formato de bytes
                        HttpRequest imgRequest = HttpRequest.newBuilder().uri(URI.create(href)).build();
                        HttpResponse<byte[]> imgResponse = client.send(imgRequest, HttpResponse.BodyHandlers.ofByteArray());
                        byte[] imageBytes = imgResponse.body();
                        
                        //a String image64Preview recebe imageBytes convertida para String
                        image64Preview += Base64.getEncoder().encodeToString(imageBytes) + "\n";
                        if(image64Preview.length() > 200) //limita o numero de caracteres para nao entupir o console
                            image64Preview = image64Preview.substring(0, 200) + "...";
                    }
                    
                    System.out.println("===== ITEM " + (i + 1) + " =====" 
                                        + "\n" + "-> datetime: " + datetime + "\n"
                                        + "-> image: " + image64Preview + "\n");
                    i++;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException je) {
            System.out.println("Erro ao processar JSON: " + je.getMessage());
        }

    }
}
