package com.viktor.vano.stm32flasher;

import com.fazecast.jSerialComm.SerialPort;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ObjectsAndVariables {
    public static final int stageHeight = 600;
    public static final int stageWidth = 800;
    public static SerialPort[] serialPorts;
    public static ChoiceBox<String> choiceBoxSerialPorts;
    public static final Pane pane = new Pane();
    public static Button btnConnect, buttonFile, buttonFlash, buttonEraseMemory;
    public static Label labelFile, labelFileSize, labelFileSizeBar, labelFlashProgress;
    public static FileChooser fileChooser;
    public static Stage stageReference;
    public static File file;
    public static long fileSize = 0, readChars = 0;
    public static ProgressBar progressBarMemory, progressBarFlashedApp;
    public static byte[] binaryContent;
}
