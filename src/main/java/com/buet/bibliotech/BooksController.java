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
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class BooksController implements Initializable {

    @FXML private TableView<Books> booksTable;
    @FXML private TableColumn<Books, String> colId;
    @FXML private TableColumn<Books, String> colTitle;
    @FXML private TableColumn<Books, String> colAuthor;
    @FXML private TableColumn<Books, String> colCategory;
    @FXML private TableColumn<Books, Integer> colTotal;
    @FXML private TableColumn<Books, Integer> colAvailable;
    @FXML private TableColumn<Books, Void> colAction;

    @FXML public Label totalBooksLabel;
    @FXML public Label AvailableBooksLabel;
    @FXML private TextField searchBooksText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (booksTable != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("BookID"));
            colTitle.setCellValueFactory(new PropertyValueFactory<>("BookName"));
            colAuthor.setCellValueFactory(new PropertyValueFactory<>("Author"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
            colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
            colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));

            setupActionColumn();
            setupSearchFilter();
            updateSummaryLabels();

           //popup window
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
        }
    }

    private void showBookDetailsPopup(Books book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookDetails.fxml"));
            Parent root = loader.load();


            BookDetailsController controller = loader.getController();
            controller.setBookData(book);

            Stage stage = new Stage();
            stage.setTitle("Book Information - " + book.getBookName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); //unmoveable main window
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
                if (empty)
                    setGraphic(null);
                else setGraphic(btnDelete);
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
            if (Database.getInstance().deleteBook(book.getBookID())) {
                setupSearchFilter();
                updateSummaryLabels();
            }
        }
    }

    private void setupSearchFilter() {
        Database db = Database.getInstance();
        ObservableList<Books> masterData = db.getAvailableBooks();

        FilteredList<Books> filteredData = new FilteredList<>(masterData, p -> true);

        searchBooksText.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                //show all boosk when not typed anything
                return book.getBookName().toLowerCase().contains(lowerCaseFilter) ||
                        book.getBookID().toLowerCase().contains(lowerCaseFilter);
                //searhc by id or name
            });
        });

        SortedList<Books> sortedData = new SortedList<>(filteredData);
          //automatically sorts the filtered data
        sortedData.comparatorProperty().bind(booksTable.comparatorProperty());
        booksTable.setItems(sortedData);
    }

    private void updateSummaryLabels() {
        Database db = Database.getInstance();
        totalBooksLabel.setText(String.valueOf(db.getTotalBooksCount()));
        AvailableBooksLabel.setText(String.valueOf(db.getAvailableBooksCount()));
    }

    public void AddBook(ActionEvent event) {
        navigateTo(event, "addbook.fxml", "Add Book");
    }

    public void handleBack(ActionEvent event) {
        navigateTo(event, "dashboardview.fxml", "Dashboard");
    }

    private void navigateTo(ActionEvent event, String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void searchBooks(ActionEvent event) {

    }
}