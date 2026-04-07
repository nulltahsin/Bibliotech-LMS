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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class memberborrowedController implements Initializable {

    @FXML private TableView<BorrowedBookModel> borrowedBooksTable;
    @FXML private TableColumn<BorrowedBookModel, String> colSerial;
    @FXML private TableColumn<BorrowedBookModel, String> colBookName;
    @FXML private TableColumn<BorrowedBookModel, Void> colSelect;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));

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

      //popup window on mouseclick
        borrowedBooksTable.setRowFactory(tv -> {
            TableRow<BorrowedBookModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2)
                {
                    BorrowedBookModel clickedRow = row.getItem();
                    showBookDetailsPopup(clickedRow.getBookID());
                }
            });
            return row;
        });

        loadBorrowedBooks();
    }

    private void showBookDetailsPopup(String bookId) {
        Database db = Database.getInstance();
        Books fullBookData = db.getBookById(bookId);
        //fetch bookid from db

        if (fullBookData != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("BookDetails.fxml"));
                Parent root = loader.load();

                //pass the book to details controller
                BookDetailsController controller = loader.getController();
                controller.setBookData(fullBookData);

                Stage stage = new Stage();
                stage.setTitle("Book Details");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL); //unmoveable outside window
                stage.setResizable(false);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBorrowedBooks() {
        String memberId = ProfileController.currentMemberId;
        if (memberId != null) {
            Database db = Database.getInstance();
            ObservableList<BorrowedBookModel> tableData = db.getBorrowedBooksByMember(memberId);
            borrowedBooksTable.setItems(tableData);
        }
    }

    @FXML
    private void handleReturnBooks(ActionEvent event) {
        boolean isSelected = false;
        Database db = Database.getInstance();

        for (BorrowedBookModel book : borrowedBooksTable.getItems()) {
            if (book.getSelectBox().isSelected()) {
                isSelected = true;
                db.requestBookReturn(book.getBookID());
            }
        }

        if (!isSelected) {
            new Alert(Alert.AlertType.WARNING, "Please select a book!").show();
            return;
        }

        new Alert(Alert.AlertType.INFORMATION, "Return request sent!").show();
        loadBorrowedBooks();
    }

    public void handleBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("memberdashboardview.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Member Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}