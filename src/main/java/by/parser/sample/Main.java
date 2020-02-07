package by.parser.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

import static by.parser.sample.Constant.*;
import static java.util.Objects.nonNull;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ServiceFillScene fillSceneService = new ServiceFillScene();
        FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle(TITLE_NAME);
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 500, 200);
        Button button = new Button(BUTTON_NAME);
        Label labelText = new Label("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File fileSource = fileChooser.showOpenDialog(primaryStage);

        fillSceneService.fillScene(grid, SCENE_NAME);
        fillSceneService.fillGrid(grid);
        DatePicker datePickerClose = fillSceneService.createDatePicker(grid, 1);
        fillSceneService.createButton(grid, button, 2);
        fillSceneService.createLabel(grid, labelText, 3);
        button.setOnAction(event -> {
            ServiceMain service = new ServiceMain();
            long timeStart = System.currentTimeMillis() / 1000;
            if (nonNull(fileSource) && nonNull(datePickerClose.getValue())) {
                Map<Integer, String> elementsFromSourceFile = service.getElementsFromSourceFile(fileSource.getPath());
                Map<Integer, String> elementsForFillInFill = service.getValueFromYahoo(elementsFromSourceFile, datePickerClose);
                service.writeResultInFile(fileSource, datePickerClose, elementsForFillInFill);

                boolean mistakeFlag = elementsFromSourceFile.size() != elementsForFillInFill.size();
                int countMistake = elementsFromSourceFile.size() - elementsForFillInFill.size();

                for (final Map.Entry<Integer, String> element: elementsForFillInFill.entrySet()) {
                    if (element.getValue().equals(NOT_FOUND)) {
                        mistakeFlag = true;
                        ++countMistake;
                    }
                }
                String timeForOutput = service.getTimeForOutput(timeStart, System.currentTimeMillis() / 1000);
                String finishHeadMessage = mistakeFlag
                        ? countMistake + "/" + elementsFromSourceFile.size() + " elems NOT found! Finished in : " + timeForOutput
                        : "Finished successful in : " + timeForOutput;
                labelText.setText(finishHeadMessage);
            } else {
                labelText.setText("Check your data (source file and close date)!");
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}