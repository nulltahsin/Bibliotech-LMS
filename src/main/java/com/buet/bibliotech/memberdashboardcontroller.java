package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class memberdashboardcontroller implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String currentID = ProfileController.currentMemberId;

        if (currentID != null) {
            Database db = Database.getInstance();
            member m = db.getMemberById(currentID);

            if (m != null) {

                welcomeLabel.setText("Welcome, " + m.getName() + "!");
            } else {
                welcomeLabel.setText("Welcome, Bookworm!");
            }
        }
    }

    public void gotoborrowedbooks(ActionEvent actionEvent) {

        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("memberborrowedbooks.fxml"));
            Parent root=loader.load();

            Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Borrowed Books");
            stage.setScene(new Scene(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> confirm= alert.showAndWait();

        if (confirm.isPresent() && confirm.get()== ButtonType.OK)  {
            try {
               FXMLLoader loader=new FXMLLoader(getClass().getResource("login.fxml"));
               Parent root=loader.load();

               Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
               stage.setTitle("Bibliotech");
               stage.setScene(new Scene(root));
           } catch (IOException e) {
               e.printStackTrace();
           }
        }
    }

    public void gotoprofile(ActionEvent actionEvent) throws IOException {

        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("profilepage.fxml"));
            Parent root=loader.load();

            Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Member Dashboard");
            stage.setScene(new Scene(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void gotorequestbooks(ActionEvent actionEvent) {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("RequestBook.fxml"));
            Parent root=loader.load();

            Stage stage=(Stage)((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setTitle("Availble Books");
            stage.setScene(new Scene(root));
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }
}
