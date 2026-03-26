package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;


public class memberborrowedController implements Initializable {

    @FXML private TableView<BorrowedBookModel> borrowedBooksTable;
    @FXML private TableColumn<BorrowedBookModel, String> colSerial;
    @FXML private TableColumn<BorrowedBookModel, String> colBookName;
    @FXML private TableColumn<BorrowedBookModel, Void> colSelect; // নতুন কলাম

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        // চেকবক্সকে কলামের সাথে সেটআপ করা
        colSelect.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getTableView().getItems().get(getIndex()).getSelectBox());
                }
            }
        });

        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {
        String memberId = ProfileController.currentMemberId;
        if (memberId != null) {
            Database db = Database.getInstance();

            // Get the list of models directly from the database
            ObservableList<BorrowedBookModel> tableData = db.getBorrowedBooksByMember(memberId);

            // Set them directly to the table
            borrowedBooksTable.setItems(tableData);
        }
    }

    // ... ইম্পোর্টগুলো ঠিক থাকবে
    @FXML
    private void handleReturnBooks(ActionEvent event) {
        boolean isSelected = false;
        Database db = Database.getInstance();

        for (BorrowedBookModel book : borrowedBooksTable.getItems()) {
            if (book.getSelectBox().isSelected()) {
                isSelected = true;
                // এটি Database ক্লাসের requestBookReturn মেথডকে কল করবে
                db.requestBookReturn(book.getBookName());
            }
        }

        if (!isSelected) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select at least one book!");
            alert.showAndWait();
            return;
        }

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setContentText("Return request sent to Admin!");
        successAlert.showAndWait();

        loadBorrowedBooks(); // টেবিল রিফ্রেশ
    }

    public void handleBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("memberdashboardview.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}