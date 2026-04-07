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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class addmemberController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField batchField;
    @FXML
    private TextField emailField;

    public void initialize(URL location, ResourceBundle resources) {

        generateAndSetId();
        idField.setEditable(false); //not changeable
        idField.setStyle("-fx-background-color: #e0e0e0;");
    }


    public void generateAndSetId(){
        Database db =Database.getInstance();
        Random rand = new Random();
        String newID;
        while(true){
            int num=rand.nextInt(1000)+1;
            newID=String.valueOf(num);
            if(!db.isMemberIdExists(newID)){
                break;
            }
        }
        idField.setText(newID);
    }

  @FXML

  void savemember(ActionEvent event){

      String id = idField.getText().trim();
      String name = nameField.getText().trim();
      String dept = departmentField.getText().trim();
      String batch = batchField.getText().trim();
      String email = emailField.getText().trim();

      if (id.isEmpty() || name.isEmpty()) {
          showAlert("Error", "Member ID and Name are required.");
          return;
      }

      Database db = Database.getInstance();
      boolean success = db.addmember(id, name, dept, batch, email);

      if (success) {
          showAlert("Successful", "Member Added Successfully");
          clearFields(null);

          generateAndSetId();// Optional: clear fields after success
      } else {
          showAlert("Error", "Could not add Member! (Check if ID is already taken)");
      }
  }
    @FXML
    void clearFields(ActionEvent event) {
        nameField.clear();
        departmentField.clear();
        batchField.clear();
        emailField.clear();
    }



    private void showAlert(String error, String s) {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();


    }


    @FXML
    void handleBack(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("memberspage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
