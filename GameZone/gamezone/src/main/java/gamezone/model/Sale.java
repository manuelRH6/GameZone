package gamezone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {

    private String id;
    private VideoGame videoGame;
    private int quantity;
    private double unitPrice;
    private double total;
    private LocalDateTime saleDate;

    public Sale(String id, VideoGame videoGame, int quantity, double unitPrice) {
        this.id = id;
        this.videoGame = videoGame;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
        this.saleDate = LocalDateTime.now();
    }

    public String getId() { return id; }
    public VideoGame getVideoGame() { return videoGame; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotal() { return total; }
    public LocalDateTime getSaleDate() { return saleDate; }

    public String getFormattedDate() {
        return saleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public Object[] toTableRow() {
        return new Object[]{
            id,
            videoGame.getTitle(),
            quantity,
            String.format("$%.0f", unitPrice),
            String.format("$%.0f", total),
            getFormattedDate()
        };
    }

    @Override
    public String toString() {
        return "Sale{id='" + id + "', game='" + videoGame.getTitle() +
               "', qty=" + quantity + ", total=" + total + ", date=" + getFormattedDate() + "}";
    }
}
