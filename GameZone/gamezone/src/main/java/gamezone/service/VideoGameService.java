package gamezone.service;

import gamezone.model.Sale;
import gamezone.model.VideoGame;
import gamezone.repository.SaleRepository;
import gamezone.repository.VideoGameRepository;

import java.util.List;
import java.util.UUID;

public class VideoGameService {

    private final VideoGameRepository gameRepo;
    private final SaleRepository saleRepo;

    public VideoGameService(VideoGameRepository gameRepo, SaleRepository saleRepo) {
        this.gameRepo = gameRepo;
        this.saleRepo = saleRepo;
    }

    public void addVideoGame(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (game.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (game.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (gameRepo.existsByTitle(game.getTitle())) {
            throw new IllegalStateException("El videojuego ya existe en el catálogo");
        }
        gameRepo.save(game);
    }

    public List<VideoGame> getAllVideoGames() {
        return gameRepo.findAll();
    }

    public VideoGame findByTitle(String title) {
        return gameRepo.findByTitle(title);
    }

    public List<VideoGame> findByPlatform(String platform) {
        return gameRepo.findByPlatform(platform);
    }

    public void updateVideoGame(String title, VideoGame newGame) {
        if (!gameRepo.existsByTitle(title)) {
            throw new IllegalArgumentException("El videojuego '" + title + "' no existe en el catálogo.");
        }
        gameRepo.update(title, newGame);
    }

    public void deleteVideoGame(String title) {
        if (!gameRepo.existsByTitle(title)) {
            throw new IllegalArgumentException("El videojuego '" + title + "' no existe en el catálogo.");
        }
        gameRepo.delete(title);
    }

    public Sale sellVideoGame(String title, int quantity) {
        VideoGame game = gameRepo.findByTitle(title);
        if (game == null) {
            throw new IllegalArgumentException("El videojuego '" + title + "' no existe en el catálogo.");
        }
        if (game.getStock() < quantity) {
            throw new IllegalArgumentException(
                "Stock insuficiente. Disponible: " + game.getStock() + ", solicitado: " + quantity);
        }
        double unitPrice = game.calculateFinalPrice();
        game.setStock(game.getStock() - quantity);
        gameRepo.update(title, game);

        String saleId = "VTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepo.save(sale);
        return sale;
    }

    public List<Sale> getAllSales() {
        return saleRepo.findAll();
    }
}
