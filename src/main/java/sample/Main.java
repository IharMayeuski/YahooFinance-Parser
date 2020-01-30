package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.nonNull;

public class Main extends Application {
    final String TITLE_NAME = "Yahoo parser";
    String buttonName = "            Apply           ";
    String sceneName = "Choose your dates:";
    String dateStart = "Start date:";
    String dateEnd = "End date:";
    String dateClose = "Close date:";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FillSceneService fillSceneService = new FillSceneService();
        FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle(TITLE_NAME);
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 350, 200);
        Button button = new Button(buttonName);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        final File fileSource = fileChooser.showOpenDialog(primaryStage);

        fillSceneService.fillScene(grid, scene, sceneName);
        fillSceneService.fillGrid(grid);
        final DatePicker datePickerStart = fillSceneService.createDateLable(grid, dateStart, 1);
        final DatePicker datePickerEnd = fillSceneService.createDateLable(grid, dateEnd, 2);
        final DatePicker datePickerClose = fillSceneService.createDateLable(grid, dateClose, 3);
        fillSceneService.createButton(grid, button, 4);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.println(dtf.format(now));

                Service service = new Service();
                if (nonNull(fileSource) && service.checkDates(datePickerStart, datePickerEnd, datePickerClose)) {
                    try {
                        File fileForCopy = new File(fileSource.getPath() + datePickerClose.getValue().toString() + ".xlsx");
                        service.copyFileUsingStream(fileSource, fileForCopy);
                        FileInputStream excelFile = new FileInputStream(new File(fileForCopy.getPath()));
                        Workbook workbook = new XSSFWorkbook(excelFile);
                        Sheet sh = workbook.getSheet("Sheet1");
                        for (int j = 1; j <= sh.getLastRowNum(); j++) {
                            Row rowOne = sh.getRow(j);
                            Cell cell2 = rowOne.getCell(2);
                            String prefix = cell2.getStringCellValue();
                            String url = service.getUrl(prefix, datePickerStart, datePickerEnd);
                            String num = service.getValueFromSite(url, datePickerClose);
                            sh.getRow(j).createCell(11).setCellValue(num);
                        }
                        excelFile.close();
                        FileOutputStream outputStream = new FileOutputStream(fileForCopy.getPath());
                        workbook.write(outputStream);
                        workbook.close();
                        outputStream.close();
                    } catch (IOException e) {
                        System.out.println("!!!!!!!!!!!!!!!!");
                        e.printStackTrace();
                    }
                    System.out.println("All is ok! Process finished!");
                    now = LocalDateTime.now();
                    System.out.println(dtf.format(now));
                } else {
                    System.out.println("check your date");
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