package gamezone.repository;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.model.VideoGame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";

    public VideoGameRepository() {
        try {
            Files.createDirectories(Paths.get("data"));
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                writeJsonArray(new JSONArray());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error inicializando repositorio: " + e.getMessage());
        }
    }

    public List<VideoGame> findAll() {
        List<VideoGame> list = new ArrayList<>();
        JSONArray arr = readJsonArray();
        for (int i = 0; i < arr.length(); i++) {
            list.add(fromJson(arr.getJSONObject(i)));
        }
        return list;
    }

    public VideoGame findByTitle(String title) {
        JSONArray arr = readJsonArray();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("title").toLowerCase().contains(title.toLowerCase())) {
                return fromJson(obj);
            }
        }
        return null;
    }

    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame vg : findAll()) {
            if (vg.getPlatform().equalsIgnoreCase(platform)) {
                result.add(vg);
            }
        }
        return result.isEmpty() ? null : result;
    }

    public boolean existsByTitle(String title) {
        return findByTitle(title) != null;
    }

    public void save(VideoGame game) {
        JSONArray arr = readJsonArray();
        arr.put(toJson(game));
        writeJsonArray(arr);
    }

    public boolean update(String title, VideoGame newGame) {
        JSONArray arr = readJsonArray();
        for (int i = 0; i < arr.length(); i++) {
            if (arr.getJSONObject(i).getString("title").equalsIgnoreCase(title)) {
                arr.put(i, toJson(newGame));
                writeJsonArray(arr);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String title) {
        JSONArray arr = readJsonArray();
        for (int i = 0; i < arr.length(); i++) {
            if (arr.getJSONObject(i).getString("title").equalsIgnoreCase(title)) {
                arr.remove(i);
                writeJsonArray(arr);
                return true;
            }
        }
        return false;
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
            throw new RuntimeException("Error escribiendo archivo JSON: " + e.getMessage());
        }
    }

    private JSONObject toJson(VideoGame game) {
        JSONObject obj = new JSONObject();
        obj.put("type", game instanceof DigitalVideoGame ? "digital" : "physical");
        obj.put("title", game.getTitle());
        obj.put("price", game.getPrice());
        obj.put("platform", game.getPlatform());
        obj.put("stock", game.getStock());
        obj.put("genre", game.getGenre());
        if (game instanceof DigitalVideoGame) {
            DigitalVideoGame d = (DigitalVideoGame) game;
            obj.put("sizeGB", d.getSizeGB());
            obj.put("downloadPlatform", d.getDownloadPlatform());
        } else {
            PhysicalVideoGame p = (PhysicalVideoGame) game;
            obj.put("condition", p.getCondition());
            obj.put("distributor", p.getDistributor());
        }
        return obj;
    }

    private VideoGame fromJson(JSONObject obj) {
        String type = obj.getString("type");
        String title = obj.getString("title");
        double price = obj.getDouble("price");
        String platform = obj.getString("platform");
        int stock = obj.getInt("stock");
        String genre = obj.getString("genre");

        if ("digital".equals(type)) {
            return new DigitalVideoGame(title, price, platform, stock, genre,
                    obj.getDouble("sizeGB"), obj.getString("downloadPlatform"));
        } else {
            return new PhysicalVideoGame(title, price, platform, stock, genre,
                    obj.getString("condition"), obj.getString("distributor"));
        }
    }
}
