package gamezone;

import gamezone.repository.SaleRepository;
import gamezone.repository.VideoGameRepository;
import gamezone.service.VideoGameService;
import gamezone.ui.MainMenuController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Main extends Application {

    private VideoGameService service;

    @Override
    public void init() {
        VideoGameRepository gameRepo = new VideoGameRepository();
        SaleRepository saleRepo = new SaleRepository(gameRepo);
        service = new VideoGameService(gameRepo, saleRepo);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GameZone - Sistema de Gestión");
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(650);

        MainMenuController menuController = new MainMenuController(service, primaryStage);
        Scene scene = new Scene(menuController.buildUI(), 950, 650);
        scene.getStylesheets().add(getClass().getResource("/gamezone/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
