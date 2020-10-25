package com.viktor.vano.stm32flasher;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.viktor.vano.stm32flasher.ObjectsAndVariables.*;
import static com.viktor.vano.stm32flasher.GUI.Main.*;

public class AppFunctions {
    public static void initializeLayout()
    {
        buttonFile = new Button("File");
        buttonFile.setLayoutX(stageWidth*0.05);
        buttonFile.setLayoutY(stageHeight*0.20);
        buttonFile.setOnAction(event -> {
            file = fileChooser.showOpenDialog(stageReference);
            if (file != null) {
                System.out.println("File: " + file.getPath());
                try {
                    fileSize = Files.size(Paths.get(file.getPath()));
                    labelFileSize.setText("File size: " + fileSize + " Bytes");
                    progressBarMemory.setProgress(fileSize/22528.0);
                    labelFileSizeBar.setText(Math.round((fileSize/22528.0)*10000.0)/100.0 + " % of 22 528 Byte Memory");
                    binaryContent = new byte[(int) fileSize];
                    InputStream inputStream = new FileInputStream(file);
                    inputStream.read(binaryContent);
                    readChars = 0;
                    if(fileSize > 22528)
                        customPrompt("File Error",
                                "File is too big: " + fileSize + " Bytes\nThat is " +
                                        (Math.round((fileSize/22528.0)*10000.0)/100.0)
                                        + " % of 22 528 Byte Memory", Alert.AlertType.ERROR);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(file.getPath().length() > 50)
                    labelFile.setText("..." + file.getPath().substring(file.getPath().length()-50));
                else
                    labelFile.setText(file.getPath());
                buttonFlash.setDisable(fileSize == 0 || btnConnect.getText().equals("Connect"));
            }
        });
        pane.getChildren().add(buttonFile);

        labelFile = new Label("Please select a file.");
        labelFile.setFont(Font.font("Arial", 20));
        labelFile.setLayoutX(stageWidth*0.12);
        labelFile.setLayoutY(stageHeight*0.205);
        pane.getChildren().add(labelFile);

        labelFileSize = new Label("File size: " + fileSize + " Bytes");
        labelFileSize.setFont(Font.font("Arial", 26));
        labelFileSize.setLayoutX(stageWidth*0.12);
        labelFileSize.setLayoutY(stageHeight*0.35);
        pane.getChildren().add(labelFileSize);

        labelFileSizeBar = new Label(Math.round((fileSize/22528.0)*10000.0)/100.0 + " % of 22 528 Byte Memory");
        labelFileSizeBar.setFont(Font.font("Arial", 18));
        labelFileSizeBar.setLayoutX(stageWidth*0.60);
        labelFileSizeBar.setLayoutY(stageHeight*0.40);
        pane.getChildren().add(labelFileSizeBar);

        progressBarMemory = new ProgressBar(0.0);
        progressBarMemory.setLayoutX(stageWidth*0.60);
        progressBarMemory.setLayoutY(stageHeight*0.35);
        progressBarMemory.setPrefWidth(250);
        pane.getChildren().add(progressBarMemory);

        progressBarFlashedApp = new ProgressBar(0.0);
        progressBarFlashedApp.setLayoutX(stageWidth*0.05);
        progressBarFlashedApp.setLayoutY(stageHeight*0.65);
        progressBarFlashedApp.setPrefWidth(600);
        pane.getChildren().add(progressBarFlashedApp);

        labelFlashProgress = new Label("                        0 %\n\n0 Bytes flashed of " +
                fileSize + " Byte application");
        labelFlashProgress.setFont(Font.font("Arial", 18));
        labelFlashProgress.setLayoutX(stageWidth*0.30);
        labelFlashProgress.setLayoutY(stageHeight*0.70);
        pane.getChildren().add(labelFlashProgress);

        buttonFlash = new Button("FLASH");
        buttonFlash.setLayoutX(stageWidth*0.83);
        buttonFlash.setLayoutY(stageHeight*0.645);
        buttonFlash.setDisable(true);
        buttonFlash.setStyle(
                "    -fx-background-color: linear-gradient(#ff5400, #be1d00);\n" +
                "    -fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0;\n" +
                "    -fx-text-fill: white;");
        buttonFlash.setOnAction(event -> {
            setButtonsEnable(false);
            readChars = 0;

            serialPorts[choiceBoxSerialPorts.getSelectionModel().getSelectedIndex()].
                    writeBytes("#$FLASH_START".getBytes(StandardCharsets.UTF_8), "#$FLASH_START".length());
        });
        pane.getChildren().add(buttonFlash);

        buttonEraseMemory = new Button("ERASE FLASH MEMORY");
        buttonEraseMemory.setLayoutX(stageWidth*0.70);
        buttonEraseMemory.setLayoutY(stageHeight*0.85);
        buttonEraseMemory.setDisable(true);
        buttonEraseMemory.setStyle(
                "    -fx-background-color: \n" +
                "        #090a0c,\n" +
                "        linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\n" +
                "        linear-gradient(#20262b, #191d22),\n" +
                "        radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\n" +
                "    -fx-background-radius: 5,4,3,5;\n" +
                "    -fx-background-insets: 0,1,2,0;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n" +
                "    -fx-font-family: \"Arial\";\n" +
                "    -fx-text-fill: linear-gradient(white, #d0d0d0);\n" +
                "    -fx-font-size: 12px;\n" +
                "    -fx-padding: 10 20 10 20;");
        buttonEraseMemory.setOnAction(event -> {
            setButtonsEnable(false);
            serialPorts[choiceBoxSerialPorts.getSelectionModel().getSelectedIndex()].
                    writeBytes("#$ERASE_MEM".getBytes(StandardCharsets.UTF_8), "#$ERASE_MEM".length());
        });
        pane.getChildren().add(buttonEraseMemory);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Binary File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Binary File - *.bin", "*.bin"));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
            if(fileSize != 0) {
                progressBarFlashedApp.setProgress((double)readChars / (double)fileSize);
                labelFlashProgress.setText("                        " + (readChars*100 / fileSize) + " %\n\n"
                        + readChars + " Bytes flashed of " +
                        fileSize + " Byte application");
            }
            else{
                progressBarFlashedApp.setProgress(0);
            labelFlashProgress.setText("                        0 %\n\n0 Bytes flashed of " +
                    fileSize + " Byte application");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void setButtonsEnable(boolean value)
    {
        buttonFlash.setDisable(!value);
        buttonEraseMemory.setDisable(!value);
        btnConnect.setDisable(!value);
        buttonFile.setDisable(!value);
    }

    public static void messageHandler(String message)
    {
        if(message.equals("Flash: Erased!\n"))
        {
            buttonFlash.setDisable(fileSize == 0);
            buttonEraseMemory.setDisable(false);
            btnConnect.setDisable(false);
            buttonFile.setDisable(false);
        }else if(message.equals("Flash: Unlocked!\n") || message.equals("Flash: OK\n"))
        {
            if(readChars < fileSize)
            {
                byte[] word = new byte[4];
                for(int i=0; i<4; i++)
                {
                    word[i] = binaryContent[(int) readChars + i];
                }
                serialPorts[choiceBoxSerialPorts.getSelectionModel().getSelectedIndex()].
                        writeBytes(word, 4);
                readChars += 4;
            }else
            {
                serialPorts[choiceBoxSerialPorts.getSelectionModel().getSelectedIndex()].
                        writeBytes("#$FLASH_FINISH".getBytes(StandardCharsets.UTF_8), "#$FLASH_FINISH".length());
                setButtonsEnable(true);
            }
        }else if(message.equals("Flash: Success!\n"))
        {
            customPrompt("Flashing Successful",
                    "Flashing was finished successfully.", Alert.AlertType.INFORMATION);
            buttonFlash.setDisable(fileSize == 0);
            buttonEraseMemory.setDisable(false);
            btnConnect.setDisable(false);
            buttonFile.setDisable(false);
        }
    }
}
