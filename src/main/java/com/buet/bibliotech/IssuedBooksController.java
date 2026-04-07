package com.buet.bibliotech;

import com.buet.bibliotech.db.Database;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IssuedBooksController implements Initializable {

    @FXML private TableView<IssueInfo> issuedTable;
    @FXML private TableColumn<IssueInfo, String> colBookId;
    @FXML private TableColumn<IssueInfo, String> colBookName;
    @FXML private TableColumn<IssueInfo, String> colAuthor;
    @FXML private TableColumn<IssueInfo, String> colMemberName;
    @FXML private TableColumn<IssueInfo, String> colIssueDate;
    @FXML private Label IssuedBooksLabel;
    @FXML private TextField searchText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueTime"));

        loadData();
    }

    private void loadData() {
        Database db = Database.getInstance();
        ObservableList<IssueInfo> list = db.getPendingReturns();


        FilteredList<IssueInfo> filteredData = new FilteredList<>(list, p -> true);
        searchText.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(issue -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return issue.getBookName().toLowerCase().contains(lower) ||
                        issue.getMemberName().toLowerCase().contains(lower);
            });
        });

        issuedTable.setItems(filteredData);
        IssuedBooksLabel.setText(String.valueOf(list.size()));
    }

    @FXML
    void handleBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("dashboardview.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Admin Dashboard");
    }
}