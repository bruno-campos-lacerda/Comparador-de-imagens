/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparadorDeImagens.service;

import java.io.IOException;
import java.net.*;
import java.net.http.*;
import org.json.*;
import javax.swing.JOptionPane;
//import javax.swing.*;

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
            
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray features = jsonResponse.getJSONArray("features");
            
            
            System.out.println("datetime nos itens disponiveis:\n");
            
            int i = 0;
            while(i <= 10000){
                for(int j = 0; j < features.length(); j++){
                    JSONObject feature = features.getJSONObject(j);
                    String id = feature.getString("id");
                    
                    JSONObject dateProperties = feature.getJSONObject("properties");
                    String datetime = dateProperties.getString("datetime");
                    
                    //JSONObject coordinatesProperties = feature.getJSONObject("geometry");
                    //JSONArray coordinatesArray = coordinatesProperties.getJSONArray("coordinates");
                    JSONArray bboxFeatures = feature.getJSONArray("bbox");
                    String coordinates = bboxFeatures.toString();
                    /*for(int k = 0; k < coordinatesArray.length(); k++){
                        JSONObject coord = coordinatesArray.getJSONObject(k);
                        coordinates += coord.getString();
                    }*/
                    
                    System.out.println("Item " + (i + 1) + "\n" + "-> datetime: " + datetime + "\n"
                                                                + "-> id: " + id + "\n"
                                                                + "-> coordinates" + coordinates);
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
