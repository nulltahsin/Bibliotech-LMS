package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BooksController implements Initializable {

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
    private TableColumn<Books, Void> colAction;

    @FXML
    private TextField searchBooksText;

    @FXML
    private Label totalbooks;
     @FXML
     private Label availablebooks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (booksTable != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("BookID"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("BookName"));
            colAuthor.setCellValueFactory(new PropertyValueFactory<>("Author"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));


            setupActionColumn();

            setupSearchFilter();
            loadData();
        }
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");

            {
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand; -fx-font-size: 16px;");
                btnDelete.setOnAction(e -> {
                    Books book = getTableView().getItems().get(getIndex());
                    deleteBookAction(book);
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });
    }


    private void deleteBookAction(Books book) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Book");
        alert.setHeaderText("Are you sure you want to delete this book?");
        alert.setContentText("Book Title: " + book.getBookName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Database db = Database.getInstance();
            if (db.deleteBook(book.getBookID())) {

                setupSearchFilter();
            } else {
                Alert errAlert = new Alert(Alert.AlertType.ERROR);
                errAlert.setTitle("Error");
                errAlert.setHeaderText(null);
                errAlert.setContentText("Could not delete the book. It might be issued to a member.");
                errAlert.showAndWait();
            }
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
    public void updatesummarylabels(){
        Database db=Database.getInstance();

        int totalbookscount= db.getTotalBooksCount();
        int availablebookscount=db.getAvailableBooksCount();

        totalbooks.setText(String.valueOf(totalbookscount));

        availablebooks.setText(String.valueOf(availablebookscount));


    }

    private void loadData() {
        updatesummarylabels();

    }

    public void searchBooks(ActionEvent actionEvent) {
        // this is now empty
    }

    public void AddBook(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addbook.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Add a New Book");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleBack(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardview.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
