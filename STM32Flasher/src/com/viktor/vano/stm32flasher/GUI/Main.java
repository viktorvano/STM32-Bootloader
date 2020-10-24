package com.viktor.vano.stm32flasher.GUI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import static com.viktor.vano.stm32flasher.AppFunctions.*;
import static com.viktor.vano.stm32flasher.ObjectsAndVariables.*;
import static com.viktor.vano.stm32flasher.SerialComPort.*;

public class Main extends Application {
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage){
        stage.setTitle("STM32 Flasher");
        Scene scene = new Scene(pane, stageWidth, stageHeight);
        stage.setMinHeight(stageHeight);
        stage.setMinWidth(stageWidth);
        stage.setMaxHeight(stageHeight);
        stage.setMaxWidth(stageWidth);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(new Image("/com/viktor/vano/stm32flasher/resources/icon.png"));
        stage.show();
        stageReference = stage;

        initializeComPortMenu();
        initializeLayout();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(btnConnect.getText().equals("Disconnect"))
            disconnectFromSerialPort();
    }

    public static void customPrompt(@NotNull String title, @NotNull String message, @NotNull Alert.AlertType alertType)
    {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            Alert alert = new Alert(alertType);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
            if(alertType.equals(Alert.AlertType.ERROR))
                alert.setOnCloseRequest(event1 -> {
                    System.out.println("Leaving app from Error Prompt Handler......");
                    System.exit(-23);
                });
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
