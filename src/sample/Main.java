package sample;

import com.sun.rowset.internal.Row;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Objects.nonNull;


public class Main extends Application {
    int placeDateOnYahoo = 0;
    int placeCloseRateOnYahoo = 4;

    String titleName = "Yahoo parser";
    String buttonName = "            Apply           ";
    String sceneName = "Choose your dates:";
    String dateStart = "Start date:";
    String dateEnd = "End date:";
    String dateClose = "Close date:";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FillSceneService fillSceneService = new FillSceneService();
        FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle(titleName);
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 350, 200);
        Button button = new Button(buttonName);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(primaryStage);

        fillSceneService.fillScene(grid, scene, sceneName);
        fillSceneService.fillGrid(grid);
        DatePicker datePickerStart = fillSceneService.createDateLable(grid, dateStart, 1);
        DatePicker datePickerEnd = fillSceneService.createDateLable(grid, dateEnd, 2);
        DatePicker datePickerClose = fillSceneService.createDateLable(grid, dateClose, 3);
        fillSceneService.createButton(grid, button, 4);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(file.getPath());
                Service service = new Service();
                if (nonNull(file) && service.checkDates(datePickerStart, datePickerEnd, datePickerClose)) {
                    Map<String, String> dataValue = new HashMap<>();
                    SimpleDateFormat oldDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        String prefix = "MMIT.L";
                        String url = (service.getUrl(prefix, datePickerStart, datePickerEnd));
                        System.out.println(url.toString());
                        Document doc = Jsoup.connect(url).get();
                        Element table = doc.select("table").get(placeDateOnYahoo); //select the first table.
                        Elements rows = table.select("tr");
                        for (Element row : rows) {
                            Elements elements = row.select("td");
                            if (elements.size() == 7) {
                                String oldDateString = elements.get(0).text();
                                Date date = oldDateFormat.parse(oldDateString);
                                String newDateString = newDateFormat.format(date);
                                if ((datePickerClose.getValue().toString()).equals(newDateString)){
                                    String value = elements.get(placeCloseRateOnYahoo).text();
                                    dataValue.put(prefix, value);
                                }
                            }
                        }
                        System.out.println(dataValue.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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