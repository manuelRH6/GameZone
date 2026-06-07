package gamezone.model;

import gamezone.interfaces.Sellable;
import gamezone.interfaces.Displayable;

public class DigitalVideoGame extends VideoGame implements Sellable, Displayable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock,
                            String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB() { return sizeGB; }
    public void setSizeGB(double sizeGB) { this.sizeGB = sizeGB; }

    public String getDownloadPlatform() { return downloadPlatform; }
    public void setDownloadPlatform(String downloadPlatform) { this.downloadPlatform = downloadPlatform; }

    @Override
    public double calculateFinalPrice() {
        if (sizeGB > 50) {
            return price + 5000;
        }
        return price;
    }

    @Override
    public double sell(int qty) {
        if (qty > stock) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stock);
        }
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("🎮 [DIGITAL] %s | Plataforma: %s | Género: %s | " +
                             "Precio: $%.0f | Stock: %d | Tamaño: %.1f GB | Descarga: %s",
                title, platform, genre, calculateFinalPrice(), stock, sizeGB, downloadPlatform);
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, "Digital", platform, genre,
                            String.format("$%.0f", calculateFinalPrice()), stock,
                            sizeGB + " GB", downloadPlatform};
    }

    @Override
    public String toString() {
        return "DigitalVideoGame{title='" + title + "', price=" + price +
               ", platform='" + platform + "', stock=" + stock +
               ", genre='" + genre + "', sizeGB=" + sizeGB +
               ", downloadPlatform='" + downloadPlatform + "'}";
    }
}
