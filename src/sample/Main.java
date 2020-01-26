package sample;

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
import javafx.stage.Stage;

import java.time.*;

public class Main extends Application {
    String titleName = "Yahoo parser";
    String buttonName = "            Apply           ";
    String sceneName = "Choose your dates:";
    String dateStart = "Start date:";
    String dateEnd = "End date:";
    String dateClose = "Close date:";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle(titleName);
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 350, 200);
        Button button = new Button(buttonName);
        fillScene(grid, scene, sceneName);
        fillGrid(grid);
        DatePicker datePickerStart = createDateLable(grid, dateStart, 1);
        DatePicker datePickerEnd = createDateLable(grid, dateEnd, 2);
        DatePicker datePickerClose = createDateLable(grid, dateClose, 3);
        createButton(grid, button, 4);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Service service = new Service();
                if (service.checkDates(datePickerStart, datePickerEnd, datePickerClose)) {
                    System.out.println(service.getUrl("HSBA.L", datePickerStart, datePickerEnd));
                } else {
                    System.out.println("check your date");
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fillGrid(GridPane grid) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
    }

    private void fillScene(GridPane grid, Scene scene, String sceneName) {
        Text scenetitle = new Text(sceneName);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
    }

    private DatePicker createDateLable(GridPane grid, String text, int row) {
        Label dateStart = new Label(text);
        grid.add(dateStart, 0, row);
        DatePicker datePicker = new DatePicker();
        HBox hBoxDate = new HBox(datePicker);
        grid.add(hBoxDate, 1, row);
        return datePicker;
    }

    private void createButton(GridPane grid, Button button, int row) {
        HBox hBox = new HBox(40);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().add(button);
        grid.add(hBox, 1, 4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}