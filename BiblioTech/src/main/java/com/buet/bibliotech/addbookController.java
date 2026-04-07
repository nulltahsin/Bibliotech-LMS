package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
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
import java.sql.SQLException;

public class addbookController {

    @FXML
    private TextField AuthorField;
    @FXML
    private TextField bookNameField;
    @FXML
    private TextField bookIDField;
    @FXML
    private TextField categoryField;


    public void addbook(ActionEvent actionEvent) throws SQLException {
        String author= AuthorField.getText();
        String bookname = bookNameField.getText();
        String bookid = bookIDField.getText();
        String category = categoryField.getText();

        if (bookid.isEmpty() || bookname.isEmpty() || author.isEmpty() || category.isEmpty()) {
            showAlert("Error", "Information are required.");
            return;
        }

        Database db= Database.getInstance();
        boolean success=db.addbook(bookid,bookname,author,category);

        if (success){
            showAlert("Successful","Book Added Successfully");
        }
        else{
            showAlert("Error","Could not add Book!");
        }

    }

    public void clearFields(ActionEvent actionEvent) {
        AuthorField.clear();
        bookIDField.clear();
        bookNameField.clear();
        categoryField.clear();

    }
     @FXML
    private void showAlert(String error, String s) {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();


    }
    @FXML
    public void handleback(ActionEvent actionEvent) {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Books.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Available Books");
            }
        catch(IOException e)
        {
                e.printStackTrace();
            }
        }
    }

