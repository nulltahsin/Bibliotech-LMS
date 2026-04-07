package com.buet.bibliotech;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class ReturnBookController {

    @FXML private TextField bookIdField;
    @FXML private TextField fineField;


    @FXML
    public void handleReturnBook(ActionEvent event) {
        String bookId = bookIdField.getText().trim();
        String fineAmount = fineField.getText().trim();

        if (bookId.isEmpty()) {
            showPopUp(Alert.AlertType.ERROR, "Error!", "Please enter a valid Book ID.");
        } else {

            showPopUp(Alert.AlertType.INFORMATION, "Success", "Book Return Successful!");


            clearFields();
        }
    }


    private void showPopUp(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        bookIdField.clear();
        fineField.clear();
    }


    @FXML
    public void switchToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboardview.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("BiblioTech - Dashboard");
        stage.show();
    }
}