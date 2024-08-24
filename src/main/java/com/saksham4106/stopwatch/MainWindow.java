package com.saksham4106.stopwatch;

import com.saksham4106.stopwatch.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("main-window-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 721, 450);
        stage.setOpacity(0.8);
        scene.setFill(new Color(0.1, 0.1, 0.1, 0.8));
        stage.setTitle("Stopwatch");
        stage.setScene(scene);
        stage.show();


        FXTrayIcon icon = new FXTrayIcon(stage, getClass().getResource("Clock.png"));
        icon.addExitItem(true);
        icon.addTitleItem(true);
        icon.show();
    }

    public static void main(String[] args) {
        launch();
    }
}