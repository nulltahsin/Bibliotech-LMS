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

    /**
     * Main login method triggered by the Login button
     */

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
            // signup.fxml লোড করা হচ্ছে
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));

            // বর্তমান স্টেজ (উইন্ডো) খুঁজে বের করা
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // নতুন সিন (Scene) সেট করা
            stage.setScene(new Scene(root));
            stage.setTitle("Join BiblioTech - Signup");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Signup page");
        }
    }
    /**
     * Helper method to switch scenes
     */
    private void loadNewScene(String fxmlFile, String title) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Set up the scene (keeping your 1000x600 size preference)
            Scene newScene = new Scene(root, 1000, 600);

            // Get current stage from the login button
            Stage stage = (Stage) loginBtn.getScene().getWindow();


            stage.setScene(newScene);
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Navigation Error", "Could not load the " + title + " view.");
        }
    }

    /**
     * Standardized Alert method
     */
    private void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}