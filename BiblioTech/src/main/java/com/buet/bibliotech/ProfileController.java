package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    // These fx:id must match the labels in your Profile FXML
    @FXML private Label profileName;
    @FXML private Label profileID;
    @FXML private Label profileEmail;
    @FXML private Label profileDept;
    @FXML private Label profileBatch;

    /* EDITED: Static variable to hold the ID passed from LoginController */
    public static String currentMemberId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // As soon as the page opens, fetch data from DB
        loadMemberDetails();
    }

    private void loadMemberDetails() {
        if (currentMemberId == null) return;

        Database db = Database.getInstance();
        member m = db.getMemberById(currentMemberId);

        if (m != null) {
            /* EDITED: Connecting database data to the UI labels */
            profileName.setText(m.getName());
            profileID.setText(m.getId());
            profileEmail.setText(m.getEmail());
            profileDept.setText(m.getDepartment());
            profileBatch.setText(m.getBatch());
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("memberdashboardview.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Member Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}