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

public class loginController {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernamefield;

    @FXML
    private TextField passwordfield;

    @FXML
    private RadioButton adminRadio;

    @FXML
    private RadioButton memberRadio;

    @FXML
    private ToggleGroup roleGroup;



    @FXML
    public void doLogin() {
        String enteredUser = usernamefield.getText().trim();
        String enteredPass = passwordfield.getText().trim();

        if (enteredUser.isEmpty() || enteredPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Information Required", "Please enter both Username and Password.");
            return;
        }

        Database db = Database.getInstance();

        String actualRole = db.getLoginRole(enteredUser, enteredPass);

        if (adminRadio.isSelected()) {
            if (actualRole.equals("ADMIN")) {
                loadNewScene("dashboardview.fxml", "Admin Dashboard");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Admin Credentials", "Incorrect Admin username or password.");
            }
        }
        else if (memberRadio.isSelected()) {

            if (actualRole.equals("MEMBER")) {
                ProfileController.currentMemberId = enteredPass;
                loadNewScene("memberdashboardview.fxml", "Member Portal");



            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Credentials", "Member Name and Password do not match.");
            }
        }
    }



    @FXML
    private void handleGoToSignup(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Join BiblioTech - Signup");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Signup page");
        }
    }

    private void loadNewScene(String fxmlFile, String title) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();


            Scene newScene = new Scene(root, 1000, 600);
            Stage stage = (Stage) loginBtn.getScene().getWindow();


            stage.setScene(newScene);
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Navigation Error", "Could not load the " + title + " view.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}