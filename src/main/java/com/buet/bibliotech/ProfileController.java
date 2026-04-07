package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private TextField profileName, profileID, profileEmail, profileDept, profileBatch;
    @FXML private Button saveButton;

    public static String currentMemberId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMemberDetails();
    }

    private void loadMemberDetails() {
        if (currentMemberId == null) return;

        Database db = Database.getInstance();
        member m = db.getMemberById(currentMemberId);
        if (m != null) {
            profileName.setText(m.getName());
            profileID.setText(m.getId());
            profileEmail.setText(m.getEmail());
            profileDept.setText(m.getDepartment());
            profileBatch.setText(m.getBatch());
        }
    }

    @FXML
    void enableEditing(ActionEvent event) {
        profileName.setEditable(true);
        profileEmail.setEditable(true);
        profileDept.setEditable(true);
        profileBatch.setEditable(true);
        profileID.setEditable(true);

        saveButton.setVisible(true);


        profileName.requestFocus();
    }

    @FXML
    public void saveProfile(ActionEvent event) {
        String oldId = currentMemberId;
        String newId = profileID.getText();
        String name = profileName.getText();
        String email = profileEmail.getText();
        String dept = profileDept.getText();
        String batch = profileBatch.getText();

        Database db = Database.getInstance();

        if (!newId.equals(oldId) && db.isMemberIdExists(newId)) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID already exists! Please choose a unique Member ID.");
            return;
        }
        //ensure unique ids


        boolean success = db.updateMemberProfileFull(oldId, newId, name, email, dept, batch);

        if (success) {
            currentMemberId = newId;
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
            setFieldsEditable(false);
            saveButton.setVisible(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update database.");
        }
    }

    private void setFieldsEditable(boolean state) {
        TextField[] fields = {profileName, profileID, profileEmail, profileDept, profileBatch};
        String style = state ? "-fx-background-color: #f1f3f5; -fx-border-color: #dee2e6;" : "-fx-background-color: transparent;";
        for (TextField f : fields) {
            f.setEditable(state);
            f.setStyle(style + "-fx-font-size: 16px;");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("memberdashboardview.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}