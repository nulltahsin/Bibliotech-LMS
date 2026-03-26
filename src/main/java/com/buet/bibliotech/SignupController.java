package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class SignupController {

    @FXML private TextField nameField, deptField, batchField, emailField;
    @FXML private PasswordField passwordIdField; // পাসওয়ার্ড এবং আইডি ফিল্ড

    @FXML
    private void handleSignup(ActionEvent event) {
        String name = nameField.getText();
        String dept = deptField.getText();
        String batch = batchField.getText();
        String email = emailField.getText();
        String passId = passwordIdField.getText();

        if (name.isEmpty() || passId.isEmpty()) {
            showAlert("Error", "Full Name and Password/ID are required!");
            return;
        }

        Database db = Database.getInstance();

        // এখানে passId-কে 'id' প্যারামিটার হিসেবে পাঠানো হচ্ছে
        boolean success = db.addmember(passId, name, dept, batch, email);

        if (success) {
            showAlert("Success", "Registration successful! Use your ID to login.");
            handleBackToLogin(event);
        } else {
            showAlert("Failed", "Could not register. This ID might already be in use.");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/buet/bibliotech/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("BiblioTech - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
