package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.ObservableList;
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

public class issuebookController implements Initializable {

    @FXML
    private ComboBox<String> booknamePicker;
    //combobox

    @FXML
    private TextField booknumField;


    @FXML
    private ComboBox<String> studentidPicker;

    @FXML
    private DatePicker issuedatepicker;

    private ObservableList<Books> availableBooks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadAvailableBooks();


        loadMemberIDs();
          // for displaying bookid
        booknamePicker.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {

                for (int i = 0; i < availableBooks.size(); i++) {

                    Books book = availableBooks.get(i);
                    String nameInList = book.getBookName();

                    if (nameInList.equals(newVal))
                    {
                        String idToDisplay = book.getBookID();
                        booknumField.setText(idToDisplay);

                        break;
                    }
                    //displays the book in the picker
                }
            }
        });
    }

    private void loadAvailableBooks() {
        Database db = Database.getInstance();
        availableBooks = db.getAvailableBooks();

        for (int i = 0; i < availableBooks.size(); i++) {
            Books currentBook = availableBooks.get(i);
            String nameOfBook = currentBook.getBookName();
            booknamePicker.getItems().add(nameOfBook);
        }
    }


    private void loadMemberIDs() {
        Database db = Database.getInstance();
        ObservableList<member> members = db.getMembers();
        for (int i = 0; i < members.size(); i++) {

            member currentMember = members.get(i);

            String memberID = currentMember.getId();
            studentidPicker.getItems().add(memberID);
        }
    }

    public void issuedone(ActionEvent actionEvent) {
        String bookName = booknamePicker.getValue();
        String bookID = booknumField.getText();


        String studentid = studentidPicker.getValue();

        String issueDate = (issuedatepicker.getValue() != null) ? issuedatepicker.getValue().toString() : "";


        if (bookName == null || bookID.isEmpty() || studentid == null || issueDate.isEmpty()) {
            showAlert("Error", "All information are required.");
            return;
        }

        Database db = Database.getInstance();
        if (db.issueBook(bookID, studentid, issueDate)) {
            showAlert("Success", "Book issued successfully!");
            handleBack(actionEvent);
        }
        else
        {
            showAlert("Error", "Could not issue book.");
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardview.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showAlert(String error, String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(error);
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}