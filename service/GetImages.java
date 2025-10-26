package ComparadorDeImagens.service;

import ComparadorDeImagens.dao.ImagesDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import org.json.*;

public class GetImages {

    private static final int MAX_PAGINAS = 500;
    private static final int THREADS = 10;
    private static final int MAX_IMAGENS = 10000;
    private static final int RETRY = 3;
    private static final String BASE_DIR = "images"; // pasta para salvar as imagens

    public static void main(String[] args) throws InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        ImagesDAO dao = new ImagesDAO();

        // cria pasta caso não exista
        new File(BASE_DIR).mkdirs();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        int totalSalvas = 0;
        int pagina = 1;
        String nextUrl = "https://data.inpe.br/bdc/stac/v1/search?collections=LCC_L8_30_1M_STK_Cerrado-1&limit=50";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

        while (nextUrl != null && pagina <= MAX_PAGINAS && totalSalvas < MAX_IMAGENS) {
            System.out.println("\n🔎 Buscando página " + pagina + ": " + nextUrl);

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(nextUrl))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonResponse = new JSONObject(response.body());

                JSONArray features = jsonResponse.optJSONArray("features");
                if (features == null || features.isEmpty()) {
                    System.out.println("⚠️ Nenhuma feature nesta página.");
                    break;
                }

                for (int j = 0; j < features.length(); j++) {
                    if (totalSalvas >= MAX_IMAGENS) break;

                    JSONObject feature = features.getJSONObject(j);
                    JSONObject props = feature.optJSONObject("properties");
                    if (props == null) continue;

                    String datetime = props.optString("datetime", null);
                    if (datetime == null) continue;

                    Timestamp timestamp;
                    LocalDateTime ldt;
                    try {
                        Instant instant = Instant.parse(datetime);
                        ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        timestamp = Timestamp.valueOf(ldt);
                    } catch (Exception e) {
                        continue;
                    }

                    JSONObject assets = feature.optJSONObject("assets");
                    if (assets == null) continue;

                    for (String key : assets.keySet()) {
                        JSONObject asset = assets.getJSONObject(key);
                        String href = asset.optString("href", null);
                        String type = asset.optString("type", "");
                        if (href == null || !type.equals("image/png")) continue;

                        // Evita duplicatas
                        if (dao.existsImageByDatetime(timestamp) || dao.existsImageByHref(href)) {
                            System.out.println("⏩ Já existe: " + timestamp + " | " + href);
                            continue;
                        }

                        // Define nome do arquivo
                        String filename = BASE_DIR + "/" + ldt.format(formatter) + "_" + key + ".png";

                        executor.submit(() -> {
                            int attempts = 0;
                            while (attempts < RETRY) {
                                try {
                                    HttpRequest imgReq = HttpRequest.newBuilder().uri(URI.create(href)).build();
                                    HttpResponse<byte[]> imgResp = client.send(imgReq, HttpResponse.BodyHandlers.ofByteArray());
                                    byte[] imageBytes = imgResp.body();

                                    // Salva no disco
                                    try (FileOutputStream fos = new FileOutputStream(filename)) {
                                        fos.write(imageBytes);
                                    }

                                    // Salva referência no banco
                                    dao.InsertImagePath(timestamp, filename, href);
                                    System.out.println("✅ Salvo: " + filename);
                                    break;
                                } catch (Exception e) {
                                    attempts++;
                                    System.out.println("⚠️ Falha (" + attempts + "): " + href);
                                    try { Thread.sleep(1000); } catch (InterruptedException ie) { }
                                }
                            }
                        });

                        totalSalvas++;
                        break; // pega só 1 imagem por feature
                    }
                }

                // Próxima página
                nextUrl = null;
                JSONArray links = jsonResponse.optJSONArray("links");
                if (links != null) {
                    for (int i = 0; i < links.length(); i++) {
                        JSONObject link = links.getJSONObject(i);
                        if ("next".equals(link.optString("rel"))) {
                            nextUrl = link.optString("href", null);
                            break;
                        }
                    }
                }

                pagina++;
                Thread.sleep(1500);

            } catch (IOException | InterruptedException | JSONException e) {
                System.out.println("Erro na página: " + e.getMessage());
                break;
            }
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.HOURS);

        System.out.println("\n🚀 Processo concluído. Total de imagens salvas: " + totalSalvas);
    }
}
