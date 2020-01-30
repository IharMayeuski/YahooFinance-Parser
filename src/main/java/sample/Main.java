package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;

public class Main extends Application {
    final String TITLE_NAME = "Yahoo parser";
    final String BUTTON_NAME = "\t\tApply\t\t\t";
    final String SCENE_NAME = "Close date:";
    final String NOT_FOUND = "--";

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

        fillSceneService.fillScene(grid, scene, SCENE_NAME);
        fillSceneService.fillGrid(grid);
        DatePicker datePickerClose = fillSceneService.createDatePicker(grid, 1);
        fillSceneService.createButton(grid, button, 2);
        fillSceneService.createLabel(grid, labelText, 3);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Service service = new Service();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                long timeStart = System.currentTimeMillis() / 1000;
                if (nonNull(fileSource) && nonNull(datePickerClose.getValue())) {
                    try {
                        String fileName = service.getNewFileNameForData(fileSource.getPath(), datePickerClose);
                        File fileForCopy = new File(fileName);
                        Service.copyFileUsingStream(fileSource, fileForCopy);
                        FileInputStream excelFile = new FileInputStream(new File(fileForCopy.getPath()));
                        Workbook workbook = new XSSFWorkbook(excelFile);
                        Sheet sh = workbook.getSheet("Sheet1");
                        boolean mistakeFlag = false;
                        int countMistake = 0;
                        for (int j = 1; j <= sh.getLastRowNum(); j++) {
                            Row rowOne = sh.getRow(j);
                            Cell cell2 = rowOne.getCell(2);
                            String prefix = cell2.getStringCellValue();
                            String url = service.getUrlFromCloseDate(prefix, datePickerClose);
                            try {
                                String num = !(service.getValueFromSite(url, datePickerClose)).isEmpty()
                                        ? service.getValueFromSite(url, datePickerClose)
                                        : NOT_FOUND;
                                sh.getRow(j).createCell(11).setCellValue(num);
                                if (num.equals(NOT_FOUND)) {
                                    mistakeFlag = true;
                                    ++countMistake;
                                }
                            } catch (ParseException e) {
                                sh.getRow(j).createCell(11).setCellValue(NOT_FOUND);
                                mistakeFlag = true;
                                ++countMistake;
                            }
                        }
                        excelFile.close();
                        FileOutputStream outputStream = new FileOutputStream(fileForCopy.getPath());
                        workbook.write(outputStream);
                        workbook.close();
                        outputStream.close();
                        long timeFinish = System.currentTimeMillis() / 1000;
                        String finidhedMessage = mistakeFlag
                                ? countMistake + "/" + sh.getLastRowNum()
                                    + " elems NOT found! Finished in : "
                                    + (timeFinish - timeStart) + " secs."
                                : "Finished successful in : " + (timeFinish - timeStart) + " secs.";
                        labelText.setText(finidhedMessage);
                    } catch (IOException e) {
                        labelText.setText(e.toString());
                    }
                } else {
                    labelText.setText("Check your date");
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}