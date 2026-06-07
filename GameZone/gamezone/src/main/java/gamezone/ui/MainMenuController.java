package gamezone.ui;

import gamezone.model.*;
import gamezone.service.VideoGameService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

public class MainMenuController {

    private final VideoGameService service;
    private final Stage stage;
    private BorderPane root;
    private VBox contentArea;

    public MainMenuController(VideoGameService service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    public BorderPane buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f1a;");

        root.setTop(buildHeader());
        root.setLeft(buildSideMenu());

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: #0f0f1a;");
        root.setCenter(contentArea);

        showWelcome();
        return root;
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 15 25;");
        header.setAlignment(Pos.CENTER_LEFT);

        Text icon = new Text("🎮");
        icon.setFont(Font.font(28));

        VBox titleBox = new VBox(2);
        Text title = new Text("  GameZone");
        title.setFont(Font.font("System Bold", 22));
        title.setFill(Color.web("#e94560"));
        Text subtitle = new Text("  Sistema de Gestión de Videojuegos");
        subtitle.setFont(Font.font(12));
        subtitle.setFill(Color.web("#888aaa"));
        titleBox.getChildren().addAll(title, subtitle);

        header.getChildren().addAll(icon, titleBox);
        return header;
    }

    private VBox buildSideMenu() {
        VBox menu = new VBox(5);
        menu.setPrefWidth(210);
        menu.setPadding(new Insets(20, 10, 20, 10));
        menu.setStyle("-fx-background-color: #16213e;");

        String[][] menuItems = {
            {"📋", "Listar Juegos", "list"},
            {"➕", "Agregar Juego", "add"},
            {"🔍", "Buscar por Título", "searchTitle"},
            {"🕹️", "Buscar por Plataforma", "searchPlatform"},
            {"💰", "Realizar Venta", "sell"},
            {"📊", "Ver Ventas", "sales"},
            {"🚪", "Salir", "exit"}
        };

        for (String[] item : menuItems) {
            Button btn = createMenuButton(item[0] + "  " + item[1], item[2]);
            menu.getChildren().add(btn);
        }

        return menu;
    }

    private Button createMenuButton(String label, String action) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #ccccdd; " +
            "-fx-font-size: 13px; -fx-padding: 12 15; -fx-cursor: hand; " +
            "-fx-background-radius: 8;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #e94560; -fx-text-fill: white; " +
            "-fx-font-size: 13px; -fx-padding: 12 15; -fx-cursor: hand; " +
            "-fx-background-radius: 8;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #ccccdd; " +
            "-fx-font-size: 13px; -fx-padding: 12 15; -fx-cursor: hand; " +
            "-fx-background-radius: 8;"
        ));
        btn.setOnAction(e -> handleMenuAction(action));
        return btn;
    }

    private void handleMenuAction(String action) {
        contentArea.getChildren().clear();
        switch (action) {
            case "list":          showAllGames(); break;
            case "add":           showAddGame(); break;
            case "searchTitle":   showSearchByTitle(); break;
            case "searchPlatform":showSearchByPlatform(); break;
            case "sell":          showSellGame(); break;
            case "sales":         showAllSales(); break;
            case "exit":          stage.close(); break;
        }
    }

    private void showWelcome() {
        VBox welcome = new VBox(15);
        welcome.setAlignment(Pos.CENTER);
        welcome.setPadding(new Insets(60));

        Text icon = new Text("🎮");
        icon.setFont(Font.font(64));

        Text title = new Text("Bienvenido a GameZone");
        title.setFont(Font.font("System Bold", 26));
        title.setFill(Color.web("#e94560"));

        Text subtitle = new Text("Selecciona una opción del menú lateral para comenzar");
        subtitle.setFont(Font.font(14));
        subtitle.setFill(Color.web("#888aaa"));
        subtitle.setTextAlignment(TextAlignment.CENTER);

        welcome.getChildren().addAll(icon, title, subtitle);
        contentArea.getChildren().add(welcome);
    }

    private void showAllGames() {
        contentArea.getChildren().add(sectionTitle("📋 Todos los Videojuegos"));

        List<VideoGame> games = service.getAllVideoGames();
        if (games.isEmpty()) {
            contentArea.getChildren().add(infoLabel("No hay videojuegos en el catálogo."));
            return;
        }

        TableView<VideoGame> table = buildGameTable(games);
        contentArea.getChildren().add(table);

        HBox actions = new HBox(10);
        Button btnEdit = styledButton("✏️ Editar seleccionado", "#16213e");
        Button btnDelete = styledButton("🗑️ Eliminar seleccionado", "#c0392b");

        btnEdit.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selecciona un juego."); return; }
            showEditGame(selected);
        });

        btnDelete.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selecciona un juego."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar '" + selected.getTitle() + "'?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmar eliminación");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    service.deleteVideoGame(selected.getTitle());
                    showAllGames();
                }
            });
        });

        actions.getChildren().addAll(btnEdit, btnDelete);
        contentArea.getChildren().add(actions);
    }

    private TableView<VideoGame> buildGameTable(List<VideoGame> games) {
        TableView<VideoGame> table = new TableView<>();
        table.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white;");

        String[][] cols = {{"Título","title"},{"Tipo",""},{"Plataforma","platform"},
                           {"Género","genre"},{"Precio Final",""},{"Stock","stock"}};

        TableColumn<VideoGame, String> colTitle = new TableColumn<>("Título");
        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        colTitle.setPrefWidth(180);

        TableColumn<VideoGame, String> colType = new TableColumn<>("Tipo");
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue() instanceof DigitalVideoGame ? "Digital" : "Físico"));
        colType.setPrefWidth(80);

        TableColumn<VideoGame, String> colPlatform = new TableColumn<>("Plataforma");
        colPlatform.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPlatform()));
        colPlatform.setPrefWidth(100);

        TableColumn<VideoGame, String> colGenre = new TableColumn<>("Género");
        colGenre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));
        colGenre.setPrefWidth(100);

        TableColumn<VideoGame, String> colPrice = new TableColumn<>("Precio Final");
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            String.format("$%.0f", c.getValue().calculateFinalPrice())));
        colPrice.setPrefWidth(110);

        TableColumn<VideoGame, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStock()));
        colStock.setPrefWidth(70);

        table.getColumns().addAll(colTitle, colType, colPlatform, colGenre, colPrice, colStock);
        table.setItems(FXCollections.observableArrayList(games));
        table.setPrefHeight(380);
        styleTable(table);
        return table;
    }

    private void showAddGame() {
        contentArea.getChildren().add(sectionTitle("➕ Agregar Videojuego"));

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rdDigital = new RadioButton("Digital"); rdDigital.setToggleGroup(typeGroup); rdDigital.setSelected(true);
        RadioButton rdPhysical = new RadioButton("Físico"); rdPhysical.setToggleGroup(typeGroup);
        rdDigital.setStyle("-fx-text-fill: #ccccdd;");
        rdPhysical.setStyle("-fx-text-fill: #ccccdd;");

        HBox typeBox = new HBox(20, new Label("Tipo:"), rdDigital, rdPhysical);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        styleLabel((Label) typeBox.getChildren().get(0));

        TextField tfTitle = styledField("Título");
        TextField tfPrice = styledField("Precio base");
        TextField tfPlatform = styledField("Plataforma");
        TextField tfStock = styledField("Stock");
        TextField tfGenre = styledField("Género");

        TextField tfSizeGB = styledField("Tamaño (GB)");
        TextField tfDownloadPlatform = styledField("Plataforma de descarga");
        TextField tfCondition = styledField("Estado (nuevo/usado)");
        TextField tfDistributor = styledField("Distribuidor");

        VBox digitalFields = new VBox(8, labeledField("Tamaño GB:", tfSizeGB), labeledField("Plataforma descarga:", tfDownloadPlatform));
        VBox physicalFields = new VBox(8, labeledField("Estado:", tfCondition), labeledField("Distribuidor:", tfDistributor));
        physicalFields.setVisible(false); physicalFields.setManaged(false);

        typeGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isDigital = n == rdDigital;
            digitalFields.setVisible(isDigital); digitalFields.setManaged(isDigital);
            physicalFields.setVisible(!isDigital); physicalFields.setManaged(!isDigital);
        });

        Button btnSave = styledButton("💾 Guardar", "#e94560");
        btnSave.setOnAction(e -> {
            try {
                String title = tfTitle.getText().trim();
                double price = Double.parseDouble(tfPrice.getText().trim());
                String platform = tfPlatform.getText().trim();
                int stock = Integer.parseInt(tfStock.getText().trim());
                String genre = tfGenre.getText().trim();

                VideoGame game;
                if (rdDigital.isSelected()) {
                    double sizeGB = Double.parseDouble(tfSizeGB.getText().trim());
                    game = new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, tfDownloadPlatform.getText().trim());
                } else {
                    game = new PhysicalVideoGame(title, price, platform, stock, genre, tfCondition.getText().trim(), tfDistributor.getText().trim());
                }

                service.addVideoGame(game);
                showAlert(Alert.AlertType.INFORMATION, "✅ Videojuego agregado exitosamente.");
                contentArea.getChildren().clear();
                showAddGame();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Verifica que precio, stock y tamaño sean números válidos.");
            } catch (IllegalStateException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10,
            typeBox,
            labeledField("Título:", tfTitle),
            labeledField("Precio:", tfPrice),
            labeledField("Plataforma:", tfPlatform),
            labeledField("Stock:", tfStock),
            labeledField("Género:", tfGenre),
            digitalFields, physicalFields,
            btnSave
        );
        form.setMaxWidth(420);
        contentArea.getChildren().add(form);
    }

    private void showEditGame(VideoGame game) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(sectionTitle("✏️ Editar: " + game.getTitle()));

        TextField tfTitle = styledField(game.getTitle());
        TextField tfPrice = styledField(String.valueOf(game.getPrice()));
        TextField tfPlatform = styledField(game.getPlatform());
        TextField tfStock = styledField(String.valueOf(game.getStock()));
        TextField tfGenre = styledField(game.getGenre());

        VBox extraFields = new VBox(8);
        TextField tfSizeGB = null, tfDownloadPlatform = null, tfCondition = null, tfDistributor = null;

        if (game instanceof DigitalVideoGame) {
            DigitalVideoGame d = (DigitalVideoGame) game;
            tfSizeGB = styledField(String.valueOf(d.getSizeGB()));
            tfDownloadPlatform = styledField(d.getDownloadPlatform());
            extraFields.getChildren().addAll(labeledField("Tamaño GB:", tfSizeGB), labeledField("Plataforma descarga:", tfDownloadPlatform));
        } else {
            PhysicalVideoGame p = (PhysicalVideoGame) game;
            tfCondition = styledField(p.getCondition());
            tfDistributor = styledField(p.getDistributor());
            extraFields.getChildren().addAll(labeledField("Estado:", tfCondition), labeledField("Distribuidor:", tfDistributor));
        }

        final TextField fSizeGB = tfSizeGB, fDownPlatform = tfDownloadPlatform, fCondition = tfCondition, fDistributor = tfDistributor;
        String originalTitle = game.getTitle();

        Button btnUpdate = styledButton("💾 Actualizar", "#e94560");
        btnUpdate.setOnAction(e -> {
            try {
                VideoGame updated;
                if (game instanceof DigitalVideoGame) {
                    updated = new DigitalVideoGame(tfTitle.getText().trim(), Double.parseDouble(tfPrice.getText().trim()),
                        tfPlatform.getText().trim(), Integer.parseInt(tfStock.getText().trim()),
                        tfGenre.getText().trim(), Double.parseDouble(fSizeGB.getText().trim()), fDownPlatform.getText().trim());
                } else {
                    updated = new PhysicalVideoGame(tfTitle.getText().trim(), Double.parseDouble(tfPrice.getText().trim()),
                        tfPlatform.getText().trim(), Integer.parseInt(tfStock.getText().trim()),
                        tfGenre.getText().trim(), fCondition.getText().trim(), fDistributor.getText().trim());
                }
                service.updateVideoGame(originalTitle, updated);
                showAlert(Alert.AlertType.INFORMATION, "✅ Videojuego actualizado correctamente.");
                contentArea.getChildren().clear();
                showAllGames();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error: " + ex.getMessage());
            }
        });

        VBox form = new VBox(10,
            labeledField("Título:", tfTitle), labeledField("Precio:", tfPrice),
            labeledField("Plataforma:", tfPlatform), labeledField("Stock:", tfStock),
            labeledField("Género:", tfGenre), extraFields, btnUpdate
        );
        form.setMaxWidth(420);
        contentArea.getChildren().add(form);
    }

    private void showSearchByTitle() {
        contentArea.getChildren().add(sectionTitle("🔍 Buscar por Título"));

        TextField tfSearch = styledField("Escribe el título del juego...");
        tfSearch.setPrefWidth(350);
        Button btnSearch = styledButton("Buscar", "#e94560");

        VBox resultBox = new VBox(10);

        btnSearch.setOnAction(e -> {
            resultBox.getChildren().clear();
            String query = tfSearch.getText().trim();
            if (query.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Escribe un título para buscar."); return; }
            VideoGame found = service.findByTitle(query);
            if (found == null) {
                resultBox.getChildren().add(infoLabel("❌ No se encontró ningún juego con ese título."));
            } else {
                resultBox.getChildren().add(buildGameCard(found));
            }
        });

        HBox searchRow = new HBox(10, tfSearch, btnSearch);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        contentArea.getChildren().addAll(searchRow, resultBox);
    }

    private void showSearchByPlatform() {
        contentArea.getChildren().add(sectionTitle("🕹️ Buscar por Plataforma"));

        TextField tfPlatform = styledField("Ej: PS5, PC, Xbox...");
        tfPlatform.setPrefWidth(350);
        Button btnSearch = styledButton("Buscar", "#e94560");
        VBox resultBox = new VBox(10);

        btnSearch.setOnAction(e -> {
            resultBox.getChildren().clear();
            String platform = tfPlatform.getText().trim();
            if (platform.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Escribe una plataforma."); return; }
            List<VideoGame> found = service.findByPlatform(platform);
            if (found == null || found.isEmpty()) {
                resultBox.getChildren().add(infoLabel("❌ No se encontraron juegos para esa plataforma."));
            } else {
                resultBox.getChildren().add(buildGameTable(found));
            }
        });

        HBox searchRow = new HBox(10, tfPlatform, btnSearch);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        contentArea.getChildren().addAll(searchRow, resultBox);
    }

    private void showSellGame() {
        contentArea.getChildren().add(sectionTitle("💰 Realizar Venta"));

        TextField tfTitle = styledField("Título del videojuego");
        TextField tfQty = styledField("Cantidad");
        tfTitle.setPrefWidth(320);
        tfQty.setPrefWidth(100);

        Button btnSell = styledButton("💳 Confirmar Venta", "#27ae60");

        VBox resultBox = new VBox(10);

        btnSell.setOnAction(e -> {
            resultBox.getChildren().clear();
            try {
                String title = tfTitle.getText().trim();
                String qtyText = tfQty.getText().trim();
                if (qtyText.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Ingresa una cantidad."); return; }
                int qty;
                try { qty = Integer.parseInt(qtyText); } catch (NumberFormatException nfe) { showAlert(Alert.AlertType.WARNING, "La cantidad debe ser un número válido."); return; }

                Sale sale = service.sellVideoGame(title, qty);

                VBox receipt = new VBox(8);
                receipt.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 20; -fx-background-radius: 10;");
                receipt.setMaxWidth(400);

                Text receiptTitle = new Text("✅ Venta Exitosa");
                receiptTitle.setFont(Font.font("System Bold", 18));
                receiptTitle.setFill(Color.web("#27ae60"));

                receipt.getChildren().addAll(
                    receiptTitle,
                    receiptLabel("ID de Venta:", sale.getId()),
                    receiptLabel("Juego:", sale.getVideoGame().getTitle()),
                    receiptLabel("Cantidad:", String.valueOf(sale.getQuantity())),
                    receiptLabel("Precio unitario:", String.format("$%.0f", sale.getUnitPrice())),
                    receiptLabel("TOTAL:", String.format("$%.0f", sale.getTotal())),
                    receiptLabel("Fecha:", sale.getFormattedDate())
                );
                resultBox.getChildren().add(receipt);

            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "⚠️ " + ex.getMessage());
            }
        });

        HBox inputRow = new HBox(10, tfTitle, tfQty, btnSell);
        inputRow.setAlignment(Pos.CENTER_LEFT);
        contentArea.getChildren().addAll(inputRow, resultBox);
    }

    private void showAllSales() {
        contentArea.getChildren().add(sectionTitle("📊 Historial de Ventas"));

        List<Sale> sales = service.getAllSales();
        if (sales.isEmpty()) {
            contentArea.getChildren().add(infoLabel("No hay ventas registradas aún."));
            return;
        }

        TableView<Sale> table = new TableView<>();
        table.setStyle("-fx-background-color: #1a1a2e;");

        TableColumn<Sale, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        colId.setPrefWidth(120);

        TableColumn<Sale, String> colGame = new TableColumn<>("Juego");
        colGame.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getVideoGame().getTitle()));
        colGame.setPrefWidth(180);

        TableColumn<Sale, Number> colQty = new TableColumn<>("Cantidad");
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()));
        colQty.setPrefWidth(80);

        TableColumn<Sale, String> colUnit = new TableColumn<>("Precio Unit.");
        colUnit.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            String.format("$%.0f", c.getValue().getUnitPrice())));
        colUnit.setPrefWidth(110);

        TableColumn<Sale, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            String.format("$%.0f", c.getValue().getTotal())));
        colTotal.setPrefWidth(110);

        TableColumn<Sale, String> colDate = new TableColumn<>("Fecha");
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFormattedDate()));
        colDate.setPrefWidth(140);

        table.getColumns().addAll(colId, colGame, colQty, colUnit, colTotal, colDate);
        table.setItems(FXCollections.observableArrayList(sales));
        table.setPrefHeight(380);
        styleTable(table);

        double totalRevenue = sales.stream().mapToDouble(Sale::getTotal).sum();
        Label lblTotal = new Label(String.format("💵 Ingresos totales: $%.0f", totalRevenue));
        lblTotal.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 15px; -fx-font-weight: bold;");

        contentArea.getChildren().addAll(table, lblTotal);
    }

    private VBox buildGameCard(VideoGame game) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #1a1a2e; -fx-padding: 20; -fx-background-radius: 10;");
        card.setMaxWidth(450);

        String type = game instanceof DigitalVideoGame ? "🎮 Digital" : "📦 Físico";
        Text title = new Text(type + " — " + game.getTitle());
        title.setFont(Font.font("System Bold", 16));
        title.setFill(Color.web("#e94560"));

        card.getChildren().addAll(
            title,
            receiptLabel("Plataforma:", game.getPlatform()),
            receiptLabel("Género:", game.getGenre()),
            receiptLabel("Precio final:", String.format("$%.0f", game.calculateFinalPrice())),
            receiptLabel("Stock disponible:", String.valueOf(game.getStock()))
        );

        if (game instanceof DigitalVideoGame) {
            DigitalVideoGame d = (DigitalVideoGame) game;
            card.getChildren().addAll(
                receiptLabel("Tamaño:", d.getSizeGB() + " GB"),
                receiptLabel("Descarga en:", d.getDownloadPlatform())
            );
        } else {
            PhysicalVideoGame p = (PhysicalVideoGame) game;
            card.getChildren().addAll(
                receiptLabel("Estado:", p.getCondition()),
                receiptLabel("Distribuidor:", p.getDistributor())
            );
        }
        return card;
    }

    private HBox receiptLabel(String key, String value) {
        Label k = new Label(key);
        k.setStyle("-fx-text-fill: #888aaa; -fx-min-width: 140;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        return new HBox(8, k, v);
    }

    private HBox labeledField(String labelText, TextField field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #888aaa; -fx-min-width: 160;");
        lbl.setAlignment(Pos.CENTER_RIGHT);
        HBox row = new HBox(10, lbl, field);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(230);
        tf.setStyle(
            "-fx-background-color: #1a1a2e; -fx-text-fill: white; " +
            "-fx-prompt-text-fill: #555577; -fx-border-color: #333355; " +
            "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 7 10;"
        );
        return tf;
    }

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + "; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }

    private Text sectionTitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("System Bold", 20));
        t.setFill(Color.web("#e94560"));
        return t;
    }

    private Label infoLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #888aaa; -fx-font-size: 14px;");
        return l;
    }

    private void styleLabel(Label l) {
        l.setStyle("-fx-text-fill: #888aaa; -fx-min-width: 40;");
    }

    private void styleTable(TableView<?> table) {
        table.setStyle(
            "-fx-background-color: #1a1a2e; " +
            "-fx-table-cell-border-color: #222244; " +
            "-fx-control-inner-background: #1a1a2e;"
        );
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("GameZone");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
