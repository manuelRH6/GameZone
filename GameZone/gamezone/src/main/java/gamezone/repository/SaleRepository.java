package gamezone.repository;

import gamezone.model.Sale;
import gamezone.model.VideoGame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {

    private static final String FILE_PATH = "data/sales.json";
    private final VideoGameRepository gameRepo;

    public SaleRepository(VideoGameRepository gameRepo) {
        this.gameRepo = gameRepo;
        try {
            Files.createDirectories(Paths.get("data"));
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                writeJsonArray(new JSONArray());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error inicializando repositorio de ventas: " + e.getMessage());
        }
    }

    public void save(Sale sale) {
        JSONArray arr = readJsonArray();
        arr.put(toJson(sale));
        writeJsonArray(arr);
    }

    public List<Sale> findAll() {
        List<Sale> list = new ArrayList<>();
        JSONArray arr = readJsonArray();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String gameTitle = obj.getString("gameTitle");
            VideoGame game = gameRepo.findByTitle(gameTitle);
            if (game != null) {
                Sale sale = new Sale(
                    obj.getString("id"),
                    game,
                    obj.getInt("quantity"),
                    obj.getDouble("unitPrice")
                );
                list.add(sale);
            }
        }
        return list;
    }

    private JSONObject toJson(Sale sale) {
        JSONObject obj = new JSONObject();
        obj.put("id", sale.getId());
        obj.put("gameTitle", sale.getVideoGame().getTitle());
        obj.put("quantity", sale.getQuantity());
        obj.put("unitPrice", sale.getUnitPrice());
        obj.put("total", sale.getTotal());
        obj.put("saleDate", sale.getSaleDate().toString());
        return obj;
    }

    private JSONArray readJsonArray() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            return content.trim().isEmpty() ? new JSONArray() : new JSONArray(content);
        } catch (IOException e) {
            return new JSONArray();
        }
    }

    private void writeJsonArray(JSONArray arr) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(arr.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo ventas JSON: " + e.getMessage());
        }
    }
}
