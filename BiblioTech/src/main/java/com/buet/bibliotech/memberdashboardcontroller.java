package com.buet.bibliotech;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class memberdashboardcontroller {

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
}
