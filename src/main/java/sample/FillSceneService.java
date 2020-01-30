package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FillSceneService {
    public void fillGrid(GridPane grid) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
    }

    public void fillScene(GridPane grid, Scene scene, String sceneName) {
        Text scenetitle = new Text(sceneName);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
    }

    public DatePicker createDateLable(GridPane grid, String text, int row) {
        Label dateStart = new Label(text);
        grid.add(dateStart, 0, row);
        DatePicker datePicker = new DatePicker();
        HBox hBoxDate = new HBox(datePicker);
        grid.add(hBoxDate, 1, row);
        return datePicker;
    }

    public void createButton(GridPane grid, Button button, int row) {
        HBox hBox = new HBox(40);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().add(button);
        grid.add(hBox, 1, 4);
    }
}
