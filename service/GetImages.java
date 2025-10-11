/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ComparadorDeImagens.service;

import java.io.*;
import java.net.*;
import java.net.http.*;
//import javax.swing.*;

/**
 *
 * @author brula
 */
public class GetImages {
    public static void main(String[] args) {
        try {
            String apiUrl = "https://data.inpe.br/bdc/stac/v1/search";

            // Corpo JSON do POST
            String jsonInputString = """
                {
                    "collections": ["LCC_L8_30_1M_STK_Cerrado-1"],
                    "limit": 10
                }
            """;

            // Conexão
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Enviar o JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Ler resposta
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                // Imprimir o JSON completo
                System.out.println(response.toString());
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
