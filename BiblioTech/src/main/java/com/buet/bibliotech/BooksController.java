package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
    private TextField searchBooksText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (booksTable != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("BookID"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("BookName"));
            colAuthor.setCellValueFactory(new PropertyValueFactory<>("Author"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));


            setupSearchFilter();
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

    private void loadData() {

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