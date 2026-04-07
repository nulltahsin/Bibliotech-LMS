package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

public class RequestBookController implements Initializable {
    @FXML
    private TableView<Books> booksTable;
    @FXML
    private TableColumn<Books, String> colId;
    @FXML
    private TableColumn<Books, String> colTitle;
    @FXML
    private TableColumn<Books, String> colAuthor;
    @FXML
    private TableColumn<Books, String> colCategory;

    @FXML
    private TextField searchBooksText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (booksTable != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("BookID"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("BookName"));
            colAuthor.setCellValueFactory(new PropertyValueFactory<>("Author"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));

        //pop up window on mouse click
            booksTable.setRowFactory(tv -> {
                TableRow<Books> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        Books clickedRow = row.getItem();
                        showBookDetailsPopup(clickedRow);
                    }
                });
                return row;
            });

            Platform.runLater(() -> {
                showAlert("Instruction", null, "Select a book from the list to request.");
            });

            setupSearchFilter();
        }
    }

   //pop up window
    private void showBookDetailsPopup(Books book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookDetails.fxml"));
            Parent root = loader.load();

            // pass the book data to the popup controller
            BookDetailsController controller = loader.getController();
            controller.setBookData(book);

            Stage stage = new Stage();
            stage.setTitle("Book Information - " + book.getBookName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); //blocks outer widnow
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchFilter() {
        Database db = Database.getInstance();
        ObservableList<Books> masterData = db.getAvailableBooks();

        FilteredList<Books> filteredData = new FilteredList<>(masterData, p -> true);

        searchBooksText.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getBookName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else if (book.getBookID().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Books> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(booksTable.comparatorProperty());

        booksTable.setItems(sortedData);
    }

    @FXML
    void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("memberdashboardview.fxml"));
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

    public void searchBooks(ActionEvent actionEvent) {
    }

    @FXML
    public void Request(ActionEvent actionEvent) {
        Books selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            return;
        }

        String memberID = ProfileController.currentMemberId;

        if (Database.getInstance().placeRequest(selectedBook.getBookID(), memberID)) {
            showAlert("Success", "Request Sent", "Request sent to admin for approval.");
            setupSearchFilter();
        } else {
            showAlert("Error", "Duplicate Request", "You have already requested this book.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}