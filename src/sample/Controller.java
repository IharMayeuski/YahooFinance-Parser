package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.event.ActionEvent;

public class Controller {
    @FXML
    private Button btn;

    public void click(javafx.event.ActionEvent actionEvent) {
        System.out.println("111");
         btn.setText("You've clicked!");
    }
}
